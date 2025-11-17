// src/components/ModalSubmission.js
import React, { useContext, useEffect, useState, useMemo } from "react";
import "../static/submissionChapter.css";
import "../static/modalSubmission.css";
import Apis, { endpoints } from "../configs/Apis";
import { showError, showSuccess, showWarning } from "../utils/toast";
import { MyUserContext } from "../reducers/MyUserReducer";

const ModalSubmission = ({ open, onClose, chapter, loading, error, responses }) => {
    const [grades, setGrades] = useState({});      // keyed by submissionId
    const [feedbacks, setFeedbacks] = useState({});// keyed by submissionId
    const [saving, setSaving] = useState({});      // { [submissionId]: boolean }
    const [saveMsg, setSaveMsg] = useState({});    // { [submissionId]: "ok"/"err" }
    const [gradedLocal, setGradedLocal] = useState({}); // { [submissionId]: "GRADED" }
    const user = useContext(MyUserContext);

    // Khởi tạo state theo submissionId (mỗi student 1 submission)
    useEffect(() => {
        if (!open) return;
        const nextGrades = {};
        const nextFeedbacks = {};

        (responses || []).forEach((it) => {
            const sid = it?.submission?.id;
            if (!sid) return;
            if (nextGrades[sid] === undefined) nextGrades[sid] = it?.submission?.grade ?? "";
            if (nextFeedbacks[sid] === undefined) nextFeedbacks[sid] = it?.submission?.feedback ?? "";
        });

        setGrades(nextGrades);
        setFeedbacks(nextFeedbacks);
        setGradedLocal({});
    }, [open, responses]);

    const onGradeChange = (submissionId, val) =>
        setGrades((prev) => ({ ...prev, [submissionId]: val }));

    const onFeedbackChange = (submissionId, val) =>
        setFeedbacks((prev) => ({ ...prev, [submissionId]: val }));

    // Email Teacher -> Student (đã chấm)
    const sendGradedEmail = async ({
        teacherEmail,
        teacherName,
        studentEmail,
        exerciseTitle,
        submissionId,
        grade,
        feedback,
    }) => {
        const payload = {
            studentEmail,
            teacherEmail,
            teacherName,
            exerciseTitle,
            submissionId,
            viewUrl: "",
            grade: grade ?? null,
            feedback: feedback ?? "",
        };
        try {
            Apis.post(endpoints.email, payload);
            return true;
        } catch (err) {
            console.error("Send graded email error:", err);
            return false;
        }
    };

    // Lấy meta teacher từ context
    const getTeacherMeta = (ctxUser) => {
        const t = (ctxUser && (ctxUser.user || ctxUser)) || {};
        const teacherId = t.userId ?? t.id;
        const teacherEmail = t.email || "";
        const teacherName =
            t.name || [t.lastName, t.firstName].filter(Boolean).join(" ").trim() || "Teacher";
        return { teacherId, teacherEmail, teacherName };
    };

    // Notification cho student
    const postNotification = async ({
        studentId,
        teacherId,
        exerciseTitle,
        submissionId,
        grade,
        feedback,
    }) => {
        const msg =
            `Mã bài nộp #${submissionId}` +
            (grade != null ? ` | Điểm: ${grade}` : "") +
            (feedback ? ` | Nhận xét: ${feedback}` : "");
        const payload = {
            studentId,
            teacherId,
            type: "SUBMISSION",
            title: `Bài đã được chấm: ${exerciseTitle || "Bài tập"}`,
            message: msg,
            isReaded: false,
        };
        try {
            await Apis.post(endpoints.notifications, payload);
            return true;
        } catch (err) {
            console.error("Post notification error:", err);
            return false;
        }
    };
    const locallyGRADED = useMemo(() => {
        return new Set(
            Object.entries(gradedLocal)
                .filter(([_, st]) => st === "GRADED")
                .map(([id]) => Number(id))
        );
    }, [gradedLocal]);

    // Lưu điểm/feedback + gửi email + notification
    const handleSave = async (submission, firstQuestion, student) => {
        const submissionId = submission?.id;
        const exerciseId = firstQuestion?.excerciseId; // theo DTO hiện tại
        const studentId = student?.userId ?? student?.id;

        if (!submissionId || !exerciseId || !studentId) {
            setSaveMsg((m) => ({
                ...m,
                [submissionId || "unknown"]: "Thiếu submissionId/exerciseId/studentId",
            }));
            return;
        }

        const url = `${endpoints.submissions}/${submissionId}?exerciseId=${exerciseId}&studentId=${studentId}`;
        const gradeValRaw = grades[submissionId];
        const feedbackVal = feedbacks[submissionId];

        const body = {
            grade: gradeValRaw === "" ? null : Number(gradeValRaw),
            feedback: feedbackVal ?? "",
            status: "GRADED", // ✅ cập nhật trạng thái toàn submission
        };

        try {
            setSaving((s) => ({ ...s, [submissionId]: true }));
            setSaveMsg((m) => ({ ...m, [submissionId]: "" }));

            // 1) Lưu điểm/feedback + status
            await Apis.put(url, body);

            // 2) Chuẩn bị dữ liệu chung
            const usr = (student && student.user) || {};
            const studentEmail = usr?.email || "";
            const exerciseTitle =
                (firstQuestion && firstQuestion.exerciseTitle) || (chapter && chapter.title) || "Bài tập";
            const { teacherId, teacherEmail, teacherName } = getTeacherMeta(user);

            // 3) Chạy song song: gửi email + post notification
            const [mailed, notified] = await Promise.all([
                sendGradedEmail({
                    teacherEmail,
                    teacherName,
                    studentEmail,
                    exerciseTitle,
                    submissionId,
                    grade: body.grade,
                    feedback: body.feedback,
                }),
                postNotification({
                    studentId,
                    teacherId,
                    exerciseTitle,
                    submissionId,
                    grade: body.grade,
                    feedback: body.feedback,
                }),
            ]);

            // 4) Cập nhật UI & toast
            setGradedLocal((prev) => ({ ...prev, [submissionId]: "GRADED" }));

            if (mailed) {
                showSuccess("Lưu & gửi email thành công");
                setSaveMsg((m) => ({ ...m, [submissionId]: "Đã lưu" }));
            } else {
                showWarning("Lưu thành công (gửi email lỗi)");
                setSaveMsg((m) => ({ ...m, [submissionId]: "Đã lưu (mail lỗi)" }));
            }
            if (!notified) showWarning("Gửi thông báo lỗi");
        } catch (e) {
            console.error("Save grade/feedback error:", e);
            showError("Lưu điểm/feedback thất bại");
            setSaveMsg((m) => ({ ...m, [submissionId]: "Lưu thất bại" }));
        } finally {
            setSaving((s) => ({ ...s, [submissionId]: false }));
        }
    };

    if (!open) return null;

    // ======= Gom nhóm theo STUDENT, hiển thị các câu hỏi/đáp án; input GRADE/FEEDBACK gom 1 chỗ =======
    // Giả sử locallyGRADED là Set<number> chứa các submissionId đã GRADED tại chỗ
    const groupedByStudent = Object.values(
        (responses || [])
            .filter((it) => {
                const sub = it?.submission;
                if (!sub || sub.id == null) return false;
                const id = Number(sub.id);
                const gradedServer = String(sub.status || "").toUpperCase() === "GRADED";
                const gradedLocalFlag = locallyGRADED.has(id);
                return !(gradedServer || gradedLocalFlag);
            })
            .reduce((acc, it) => {
                const sub = it.submission || {};
                const stu = sub.student || {};
                const sid = stu.userId ?? stu.id;
                if (sid == null) return acc;
                if (!acc[sid]) acc[sid] = { student: stu, items: [] };
                acc[sid].items.push(it);
                return acc;
            }, {})
    ).map((grp) => {
        const itemsSorted = grp.items.slice().sort((a, b) => {
            const ai = a?.question?.orderIndex ?? 0;
            const bi = b?.question?.orderIndex ?? 0;
            return ai - bi;
        });
        return { ...grp, items: itemsSorted };
    });
    const isEmptyAfterFilter = !loading && !error && groupedByStudent.length === 0;

    return (
        <div className="sc-modal-backdrop" onClick={onClose}>
            <div className="sc-modal" onClick={(e) => e.stopPropagation()}>
                {/* Header */}
                <div className="sc-modal-header">
                    <div>{chapter ? `Bài tự luận - ${chapter.title}` : "Bài tự luận"}</div>
                    <button
                        type="button"
                        onClick={onClose}
                        className="btn btn-light"
                        style={{ border: "1px solid #c3c5c9ff" }}
                    >
                        Đóng
                    </button>
                </div>

                {/* Body */}
                <div className="sc-modal-body">
                    {loading && <div className="loading-message">Đang tải bài làm…</div>}
                    {!loading && error && <div className="error-message">{error}</div>}

                    {!loading && !error && (
                        <>
                            {isEmptyAfterFilter ? (
                                <div className="no-submissions text-center py-4">
                                    <h6 className="mb-1">Không còn bài nào cần chấm</h6>
                                </div>
                            ) : (
                                <ul className="submission-list">
                                    {groupedByStudent.map((group, gi) => {
                                        const first = group.items[0];
                                        const sub = first?.submission || {};
                                        const firstQuestion = first?.question || {};
                                        const sid = sub.id;
                                        const isSaving = !!saving[sid];

                                        const stu = group.student || {};
                                        const usr = stu.user || {};
                                        const studentName = usr?.name || `#${stu.userId ?? ""}`;
                                        const studentEmail = usr?.email || "Chưa có email";

                                        const statusShown = gradedLocal[sid] || sub.status || "Chưa xác định";

                                        return (
                                            <li key={`stu-${stu.userId ?? gi}`} className="submission-item">
                                                {/* Header học sinh + trạng thái bài nộp */}
                                                <div className="question-header d-flex justify-content-between align-items-center">
                                                    <div>
                                                        Học sinh: <b>{studentName}</b> &nbsp;—&nbsp; Email: {studentEmail}
                                                    </div>
                                                    <div>
                                                        <span
                                                            className={`status-badge ${statusShown === "COMPLETED"
                                                                ? "status-completed"
                                                                : "status-pending"
                                                                }`}
                                                        >
                                                            {statusShown}
                                                        </span>
                                                    </div>
                                                </div>

                                                {/* Danh sách câu hỏi & câu trả lời */}
                                                <div className="student-response-block">
                                                    {group.items.map((it, idx) => {
                                                        const q = it.question || {};
                                                        return (
                                                            <div key={`q-${sid}-${q.id ?? idx}`}>
                                                                <div className="student-info-grid">
                                                                    <div className="student-info-item">
                                                                        <span className="student-info-label">Câu hỏi</span>
                                                                        <span className="student-info-value">
                                                                            Câu {q.orderIndex ?? "-"}: {q.question || "(Không có nội dung câu hỏi)"}
                                                                        </span>
                                                                    </div>
                                                                </div>

                                                                <div className="answer-section">
                                                                    <span className="answer-label">Trả lời:</span>
                                                                    <div className="answer-content">
                                                                        {it.answerEssay || "(Chưa có câu trả lời)"}
                                                                    </div>
                                                                </div>

                                                                <hr className="submission-divider" />
                                                            </div>
                                                        );
                                                    })}
                                                </div>

                                                {/* ✅ Ô nhập Điểm/Feedback chung cho toàn submission của student này */}
                                                <div className="grade-feedback-grid">
                                                    <div className="grade-section">
                                                        <label className="input-label">Điểm</label>
                                                        <input
                                                            type="number"
                                                            step="0.25"
                                                            min="0"
                                                            max="10"
                                                            value={grades[sid] ?? ""}
                                                            onChange={(e) => onGradeChange(sid, e.target.value)}
                                                            className="grade-input"
                                                            disabled={isSaving}
                                                            placeholder="Nhập điểm (0-10)"
                                                        />
                                                    </div>
                                                    <div className="feedback-section">
                                                        <label className="input-label">Feedback</label>
                                                        <textarea
                                                            rows={3}
                                                            value={feedbacks[sid] ?? ""}
                                                            onChange={(e) => onFeedbackChange(sid, e.target.value)}
                                                            className="feedback-textarea"
                                                            placeholder="Nhận xét chung cho bài nộp…"
                                                            disabled={isSaving}
                                                        />
                                                    </div>
                                                </div>

                                                {/* Action */}
                                                <div className="save-action">
                                                    <button
                                                        type="button"
                                                        className="save-button"
                                                        onClick={() => handleSave(sub, firstQuestion, stu)}
                                                        disabled={isSaving}
                                                    >
                                                        {isSaving ? "Đang lưu..." : "Lưu"}
                                                    </button>
                                                    {saveMsg[sid] && (
                                                        <span
                                                            className={`save-message ${saveMsg[sid] === "Đã lưu" ? "save-success" : "save-error"
                                                                }`}
                                                        >
                                                            {saveMsg[sid]}
                                                        </span>
                                                    )}
                                                </div>
                                            </li>
                                        );
                                    })}
                                </ul>
                            )}
                        </>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ModalSubmission;
