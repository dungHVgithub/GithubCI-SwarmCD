// src/components/student/AddSchedule.js
import React, { useEffect, useMemo, useState } from "react";
import Apis, { endpoints } from "../configs/Apis";
import { showWarning, showSuccess, showError } from "../utils/toast";

const timePad = (n) => String(n).padStart(2, "0");
const toISODate = (d) => `${d.getFullYear()}-${timePad(d.getMonth() + 1)}-${timePad(d.getDate())}`;
const toTime = (d) => `${timePad(d.getHours())}:${timePad(d.getMinutes())}:${timePad(d.getSeconds())}`;

const AddSchedule = ({ studentId, onClose, onCreated }) => {
    const [subjects, setSubjects] = useState([]);
    const [loading, setLoading] = useState(false);

    // form state
    const now = useMemo(() => new Date(), []);
    const [subjectId, setSubjectId] = useState("");
    const [studyDate, setStudyDate] = useState(toISODate(now));
    const [startTime, setStartTime] = useState("09:00");
    const [endTime, setEndTime] = useState("10:00");
    const [note, setNote] = useState("");

    // Lấy danh sách môn học của student (cách tương tự StudentDashboard.js)
    useEffect(() => {
        const fetchSubjects = async () => {
            if (!studentId) return;
            try {
                const res = await Apis.get(`${endpoints.students}/${studentId}`);
                setSubjects(res?.data?.subjectList || []);
            } catch (e) {
                console.error("Error fetching student subjects:", e);
                setSubjects([]);
            }
        };
        fetchSubjects();
    }, [studentId]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!subjectId) {
            showWarning("Hãy chọn 1 môn học");
            return;
        }
        if (!studyDate || !startTime || !endTime) {
            showWarning("Vui lòng nhập đầy đủ ngày/giờ");
            return;
        }

        const body = {
            subjectId: Number(subjectId),
            studyDate,
            startTime: startTime.length === 5 ? `${startTime}:00` : startTime, // HH:mm -> HH:mm:ss
            endTime: endTime.length === 5 ? `${endTime}:00` : endTime,
            note: note || "",
        };

        try {
            setLoading(true);
            const { data } = await Apis.post(`/schedules/student/${studentId}`, body);
            // Báo cho parent để add vào calendar
            onCreated?.(data);
            showSuccess("Tạo lịch học thành công");
            onClose?.();
        } catch (err) {
            console.error("Create schedule failed", err);
            const status = err?.response?.status;

            if (status === 500) {
                // BE đang trả 500 khi trùng lịch
                showWarning("Tạo lịch không thành công");
            } else {
                // fallback cho các lỗi khác (400/401/403/404/…)
                showError("Tạo lịch thất bại");
            }
        } finally {
            setLoading(false);
        }
    };

    // đơn giản hoá modal bằng HTML/CSS thuần
    return (
        <div
            role="dialog"
            aria-modal="true"
            style={{
                position: "fixed",
                inset: 0,
                background: "rgba(0,0,0,0.35)",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                zIndex: 9999,
            }}
            onClick={(e) => {
                // click nền để đóng
                if (e.target === e.currentTarget) onClose?.();
            }}
        >
            <div
                style={{
                    width: 420,
                    maxWidth: "90vw",
                    background: "#fff",
                    borderRadius: 12,
                    boxShadow: "0 20px 40px rgba(0,0,0,0.15)",
                    padding: 16,
                }}
            >
                <div style={{ display: "flex", alignItems: "center", marginBottom: 8 }}>
                    <h3 style={{ margin: 0, flex: 1 }}>Tạo lịch học</h3>
                    <button
                        onClick={onClose}
                        style={{
                            border: "none",
                            background: "transparent",
                            fontSize: 18,
                            cursor: "pointer",
                        }}
                        aria-label="Đóng"
                    >
                        ✕
                    </button>
                </div>

                <form onSubmit={handleSubmit} style={{ display: "grid", gap: 12 }}>
                    <label style={{ display: "grid", gap: 6 }}>
                        <span>Môn học</span>
                        <select
                            value={subjectId}
                            onChange={(e) => setSubjectId(e.target.value)}
                            required
                            style={{ padding: 8, borderRadius: 8, border: "1px solid #ddd" }}
                        >
                            <option value="">-- Chọn môn --</option>
                            {subjects.map((s) => (
                                <option key={s.id} value={s.id}>
                                    {s.title}
                                </option>
                            ))}
                        </select>
                    </label>

                    <label style={{ display: "grid", gap: 6 }}>
                        <span>Ngày học</span>
                        <input
                            type="date"
                            value={studyDate}
                            onChange={(e) => setStudyDate(e.target.value)}
                            required
                            style={{ padding: 8, borderRadius: 8, border: "1px solid #ddd" }}
                        />
                    </label>

                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
                        <label style={{ display: "grid", gap: 6 }}>
                            <span>Giờ bắt đầu</span>
                            <input
                                type="time"
                                value={startTime}
                                onChange={(e) => setStartTime(e.target.value)}
                                required
                                step="60"
                                style={{ padding: 8, borderRadius: 8, border: "1px solid #ddd" }}
                            />
                        </label>
                        <label style={{ display: "grid", gap: 6 }}>
                            <span>Giờ kết thúc</span>
                            <input
                                type="time"
                                value={endTime}
                                onChange={(e) => setEndTime(e.target.value)}
                                required
                                step="60"
                                style={{ padding: 8, borderRadius: 8, border: "1px solid #ddd" }}
                            />
                        </label>
                    </div>

                    <label style={{ display: "grid", gap: 6 }}>
                        <span>Ghi chú (tuỳ chọn)</span>
                        <textarea
                            value={note}
                            onChange={(e) => setNote(e.target.value)}
                            rows={3}
                            placeholder="Ví dụ: Ôn chương 1"
                            style={{ padding: 8, borderRadius: 8, border: "1px solid #ddd", resize: "vertical" }}
                        />
                    </label>

                    <button
                        type="submit"
                        disabled={loading}
                        style={{
                            padding: "10px 12px",
                            borderRadius: 10,
                            border: "1px solid #0d6efd",
                            background: loading ? "#9cc0ff" : "#3b80e7ff",
                            color: "#fff",
                            cursor: loading ? "not-allowed" : "pointer",
                        }}
                    >
                        {loading ? "Đang tạo..." : "Tạo lịch học"}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default AddSchedule;
