// components/AnswerSection.js
import React, { useEffect, useState, useCallback, useMemo } from 'react';
import Apis, { endpoints } from '../configs/Apis';
import '../static/answerSection.css';
import ModalAnswer from './ModalAnswer';
import { showError, showSuccess } from '../utils/toast';

const AnswerSection = ({
    questionId,
    selectedAnswerId,
    onSelect,
    showCorrect = false,
    className = '',
    canManage = false, // truyền từ ExerciseSection
}) => {
    const [loading, setLoading] = useState(false);
    const [answers, setAnswers] = useState([]);
    const [error, setError] = useState(null);

    // modal tạo/sửa đáp án
    const [openAnswerModal, setOpenAnswerModal] = useState(false);
    const [editingAnswer, setEditingAnswer] = useState(null);

    const loadAnswers = useCallback(async () => {
        setLoading(true);
        try {
            const url = endpoints.answersByQuestion(questionId);
            const res = await Apis.get(url, {
                params: { sort: 'id', dir: 'ASC', page: 0, size: 100 },
            });
            setAnswers(res.data?.items || []);
            setError(null);
        } catch {
            setError('Không tải được đáp án');
        } finally {
            setLoading(false);
        }
    }, [questionId]);

    useEffect(() => {
        if (questionId) loadAnswers();
    }, [questionId, loadAnswers]);

    const selectedAnswer = useMemo(
        () => answers.find((x) => x.id === selectedAnswerId) || null,
        [answers, selectedAnswerId]
    );

    const onAddedOrUpdated = async () => {
        await loadAnswers();
    };

    const deleteSelected = async () => {
        if (!selectedAnswer) return;
        if (!window.confirm('Xóa đáp án đang chọn?')) return;
        try {
            await Apis.delete(`${endpoints.answers}/${selectedAnswer.id}`);
            showSuccess('Xóa thành công');
            await loadAnswers();
            // tuỳ bạn: có thể reset selection
            // onSelect?.(undefined);
        } catch (e) {
            console.error(e);
            showError('Xóa đáp án thất bại');
        }
    };

    const openCreate = () => {
        setEditingAnswer(null);
        setOpenAnswerModal(true);
    };

    const openEditSelected = () => {
        if (!selectedAnswer) return;
        setEditingAnswer(selectedAnswer);
        setOpenAnswerModal(true);
    };

    if (loading) return <div className="ans-skeleton">Đang tải đáp án…</div>;
    if (error) return <div className="ans-error">{error}</div>;

    return (
        <div className={`ans-inline ${className}`}>
            {/* Toolbar: Thêm / Sửa / Xóa */}
            {canManage && (
                <div className="as-toolbar" onClick={(e) => e.stopPropagation()}>
                    <button className="as-add-btn" onClick={openCreate}>
                        + Thêm đáp án
                    </button>

                    <div className="as-actions">
                        <button
                            type="button"
                            className="as-icon-btn"
                            title={selectedAnswer ? 'Sửa đáp án đã chọn' : 'Chọn một đáp án để sửa'}
                            aria-label="Sửa"
                            onClick={openEditSelected}
                            disabled={!selectedAnswer}
                        >
                            ✏️
                        </button>
                        <button
                            type="button"
                            className="as-icon-btn danger"
                            title={selectedAnswer ? 'Xóa đáp án đã chọn' : 'Chọn một đáp án để xóa'}
                            aria-label="Xóa"
                            onClick={deleteSelected}
                            disabled={!selectedAnswer}
                        >
                            🗑️
                        </button>
                    </div>
                </div>
            )}

            {/* Danh sách đáp án */}
            {answers.length === 0 ? (
                <div className="ans-empty">Chưa có đáp án.</div>
            ) : (
                answers.map((a, idx) => {
                    const isSelected = selectedAnswerId === a.id;
                    const isCorrect = showCorrect && a.isCorrect === true;

                    return (
                        <div key={a.id} className="ans-row">
                            <button
                                type="button"
                                className={`ans-chip ${isSelected ? 'selected' : ''} ${isCorrect ? 'correct' : ''}`}
                                onClick={() => onSelect?.(a.id, a)}
                                title={a.isCorrect ? 'Đáp án đúng' : 'Đáp án'}
                            >
                                <span className="ans-letter">{String.fromCharCode(65 + idx)}</span>
                                <span className="ans-text">{a.answerText}</span>
                            </button>
                        </div>
                    );
                })
            )}

            {/* Modal tạo/sửa đáp án */}
            <ModalAnswer
                open={openAnswerModal}
                onClose={() => setOpenAnswerModal(false)}
                questionId={questionId}
                initial={editingAnswer}        // null = tạo mới, object = sửa
                onAdded={onAddedOrUpdated}     // reload sau khi lưu
            />
        </div>
    );
};
export default AnswerSection;
