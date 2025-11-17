import React, { useState, useEffect, useContext } from 'react';
import '../static/showGrade.css';
import Apis, { endpoints } from '../configs/Apis';
import { MyUserContext } from '../reducers/MyUserReducer';

const ShowGrade = ({
    open = false,
    onClose = () => { },
    exerciseId = null,
    exerciseTitle = '',
    submissionId = null,
    initialGrade = null, // có thể truyền sẵn từ FE ngay sau khi nộp để hiển thị tức thì
}) => {
    const user = useContext(MyUserContext);
    const studentId = user?.id;
    const [grade, setGrade] = useState(initialGrade);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    // Lấy điểm: ưu tiên submissionId → GET /submissions/{id}
    // Nếu không có submissionId, dùng exerciseId + studentId → GET /submissions/exercise/{exerciseId}?studentId=...
    useEffect(() => {
        let alive = true;

        async function fetchGrade() {
            if (!open) return;

            // Nếu đã có initialGrade thì có thể bỏ qua fetch (nhanh hơn)
            if (initialGrade != null && submissionId == null && exerciseId == null) return;

            setError('');
            setLoading(true);
            try {
                if (submissionId) {
                    const res = await Apis.get(`${endpoints.submissions}/${submissionId}`);
                    if (!alive) return;
                    setGrade(res?.data?.grade ?? null);
                } else if (exerciseId && studentId) {
                    const res = await Apis.get(endpoints.submissionsByExercise(exerciseId), {
                        params: { studentId },
                    });
                    if (!alive) return;
                    const list = Array.isArray(res.data) ? res.data : (res.data?.items || []);
                    // chọn submission mới nhất theo submittedAt
                    const latest = [...list].sort(
                        (a, b) => new Date(b.submittedAt || 0) - new Date(a.submittedAt || 0)
                    )[0];
                    setGrade(latest?.grade ?? null);
                }
            } catch (e) {
                if (!alive) return;
                setError('Không tải được điểm bài nộp.');
            } finally {
                if (alive) setLoading(false);
            }
        }

        fetchGrade();
        return () => { alive = false; };
    }, [open, submissionId, exerciseId, studentId, initialGrade]);

    if (!open) return null;

    return (
        <div className="grade-overlay" style={{ zIndex: 9999 }}>
            <div className="grade-modal" role="dialog" aria-modal="true" aria-labelledby="grade-title">
                <div className="grade-header">
                    <h3 id="grade-title" className="grade-congrats">
                        🎉 Chúc mừng bạn đã hoàn thành bài tập
                    </h3>
                    {exerciseTitle && (
                        <div className="grade-exercise-name">
                            <span>Bài:</span> <strong>{exerciseTitle}</strong>
                        </div>
                    )}
                </div>

                <div className="grade-body">
                    {!loading && !error && (
                        <div className="grade-score">
                            <div className="grade-score-badge">{grade ?? 'Điểm của bạn sẽ được giáo viên chấm sau'}</div>
                        </div>
                    )}
                </div>

                <div className="grade-footer">
                    <button className="grade-ok-btn" onClick={onClose}>Đóng</button>
                </div>
            </div>
        </div>
    );
};

export default ShowGrade;
