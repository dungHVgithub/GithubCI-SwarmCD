// SubmissionChapter.js
import React, { useEffect, useState, useContext } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import Apis, { endpoints } from "../configs/Apis";
import { SidebarContext } from "../reducers/SidebarContext";
import "../static/submissionChapter.css";
import ModalSubmission from "../components/ModalSubmission"; // <-- import component modal mới

const SubmissionChapter = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const subjectId = location?.state?.subjectId;

    // Lấy trạng thái sidebar để canh lề giống Submission.js
    const { collapsed } = useContext(SidebarContext) || {};
    const leftOffset = collapsed ? 60 : 220;
    const topOffset = 80;

    const [chapters, setChapters] = useState([]);
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState("");

    // Modal state
    const [showModal, setShowModal] = useState(false);
    const [selectedChapter, setSelectedChapter] = useState(null);
    const [essayExercises, setEssayExercises] = useState([]);
    const [loadingExercises, setLoadingExercises] = useState(false);
    const [exErr, setExErr] = useState("");

    const openExercisesModal = async (chapter) => {
        setSelectedChapter(chapter);
        setLoadingExercises(true);
        setExErr("");
        setEssayExercises([]); // dùng state này để truyền cho ModalSubmission qua prop 'responses'

        try {
            // 1) Lấy exercises của chapter
            const resEx = await Apis.get(`${endpoints.excercises}/chapter/${chapter.id}`);
            const items =
                Array.isArray(resEx.data?.items) ? resEx.data.items
                    : Array.isArray(resEx.data) ? resEx.data
                        : [];
            // 2) Chỉ lấy exercises kiểu ESSAY
            const essayExercisesOnly = items.filter((ex) => ex?.type === "ESSAY");
            // 3) Với mỗi ESSAY exercise, gọi endpoint mới để lấy danh sách EssayResponse
            //    (bao gồm question.question và answerEssay)
            const responseLists = await Promise.all(
                essayExercisesOnly.map((ex) =>
                    Apis.get(endpoints.EssayResponsesByExercise(ex.id))
                        .then((r) => (Array.isArray(r.data) ? r.data : [r.data]).filter(Boolean))
                        .catch(() => [])
                )
            );
            // 4) Gộp tất cả EssayResponse lại
            const mergedResponses = responseLists.flat();
            // 5) Đưa vào state để hiển thị ở modal
            setEssayExercises(mergedResponses);
            setShowModal(true);
        } catch (e) {
            console.error(e);
            setExErr("Không tải được dữ liệu bài tự luận.");
            setShowModal(true);
        } finally {
            setLoadingExercises(false);
        }
    };

    useEffect(() => {
        const loadChapters = async () => {
            if (!subjectId) {
                setErr("Không tìm thấy subjectId. Vui lòng quay lại chọn môn học.");
                return;
            }
            setLoading(true);
            setErr("");
            try {
                const res = await Apis.get(`${endpoints.chapters}/subject/${subjectId}`);
                const data = Array.isArray(res.data) ? res.data : [];
                setChapters(data);
            } catch (e) {
                console.error(e);
                setErr("Không tải được danh sách chương (chapters).");
            } finally {
                setLoading(false);
            }
        };

        loadChapters();
    }, [subjectId]);

    return (
        <div className="submission-chapters" style={{ "--content-left": `${leftOffset}px`, "--content-top": `${topOffset}px` }}>
            <div style={{ display: "flex", alignItems: "center", gap: 8, marginBottom: 12 }}>
                <button type="button" onClick={() => navigate(-1)} className="btn btn-light" style={{ border: "1px solid #c3c5c9ff" }}>
                    ← Quay lại
                </button>
                <h2 style={{ margin: 0 }}>Chương của {chapters.length > 0 ? chapters[0].subjectId?.title : ""}</h2>
            </div>

            {!subjectId && (
                <div className="alert alert-warning">
                    Không có <b>subjectId</b>. Vui lòng quay lại trang trước và chọn môn học.
                </div>
            )}

            {loading && <div>Đang tải chương…</div>}
            {err && !loading && <div className="text-danger">{err}</div>}

            {!loading && !err && subjectId && (
                <>
                    {chapters.length === 0 ? (
                        <div>Chưa có chương nào.</div>
                    ) : (
                        <div className="chapter-list">
                            {chapters.map((c) => (
                                <div
                                    key={c.id ?? `${c.orderIndex}-${c.title}`}
                                    className="chapter-item"
                                    onClick={() => openExercisesModal(c)}
                                    style={{
                                        border: "1px solid #c3c5c9ff",
                                        borderRadius: 8,
                                        padding: "10px 12px",
                                        marginBottom: 10,
                                        width: "40%",
                                        cursor: "pointer",
                                    }}
                                >
                                    <div style={{ display: "flex", alignItems: "baseline", gap: 8 }}>
                                        <span
                                            style={{
                                                fontWeight: 600,
                                                fontVariantNumeric: "tabular-nums",
                                                minWidth: 28,
                                                textAlign: "right",
                                            }}
                                            title="Order index"
                                        >
                                            {(c.orderIndex ?? "-") + "."}
                                        </span>
                                        <h3 style={{ margin: 0, fontSize: 16 }}>{c.title || "(Không tiêu đề)"}</h3>
                                    </div>
                                    {c.summaryText && (
                                        <p style={{ margin: "2px 2px 5px", color: "#1c1d1fff", whiteSpace: "pre-wrap" }}>{c.summaryText}</p>
                                    )}
                                </div>
                            ))}
                        </div>
                    )}
                </>
            )}
            {/* Modal tách riêng component */}
            <ModalSubmission
                open={showModal}
                onClose={() => setShowModal(false)}
                chapter={selectedChapter}
                loading={loadingExercises}      // ✅ dùng state đúng tên
                error={exErr}                   // ✅ dùng state đúng tên
                responses={essayExercises}      // ✅ Modal đang hiển thị danh sách; map sang prop 'responses'
            />
        </div>
    );
};

export default SubmissionChapter;
