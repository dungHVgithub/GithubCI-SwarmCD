// components/ModalAnswer.js
import React, { useEffect, useMemo, useState } from 'react';
import Apis, { endpoints } from '../configs/Apis';
import { showSuccess, showError } from '../utils/toast';
import '../static/modalAnswer.css';

const DEFAULT = { answerText: '', isCorrect: false };

const ModalAnswer = ({
    open,
    onClose,
    questionId,
    initial = null,
    onAdded,
    exerciseType = 'MCQ',   // MCQ | ESSAY
}) => {
    // ---- Hooks are always called, no early return above this line ----
    const [form, setForm] = useState(DEFAULT);
    const [submitting, setSubmitting] = useState(false);
    const [err, setErr] = useState('');

    const isEssay = useMemo(
        () => String(exerciseType).toUpperCase() === 'ESSAY',
        [exerciseType]
    );

    useEffect(() => {
        // Khi ẩn modal hoặc là ESSAY thì không reset gì
        if (!open || isEssay) return;

        if (initial) {
            setForm({
                answerText: initial.answerText ?? '',
                isCorrect: !!initial.isCorrect,
            });
        } else {
            setForm(DEFAULT);
        }
        setErr('');
    }, [open, isEssay, initial]);

    const canSubmit = useMemo(() => {
        if (!open || isEssay) return false;
        return Number.isFinite(Number(questionId)) && form.answerText.trim().length > 0;
    }, [open, isEssay, questionId, form.answerText]);

    const change = (field, value) => setForm(prev => ({ ...prev, [field]: value }));

    const submit = async (e) => {
        e?.preventDefault?.();
        if (!canSubmit) return;

        setSubmitting(true);
        try {
            const payload = {
                answerText: form.answerText.trim(),
                isCorrect: !!form.isCorrect,
            };

            if (initial?.id) {
                await Apis.put(`${endpoints.answers}/${initial.id}`, payload, {
                    params: { questionId: Number(questionId) },
                });
                showSuccess('Cập nhật đáp án thành công.');
            } else {
                await Apis.post(endpoints.answers, payload, {
                    params: { questionId: Number(questionId) },
                });
                showSuccess('Tạo đáp án thành công.');
            }
            onAdded?.();
            onClose?.();
        } catch (err) {
            console.error(err);
            setErr('Lưu đáp án thất bại. Vui lòng thử lại.');
            showError('Lưu đáp án thất bại. Vui lòng thử lại.');
        } finally {
            setSubmitting(false);
        }
    };

    // ---- Safe to early-return AFTER hooks ----
    if (!open || isEssay) return null;

    return (
        <div className="mc-overlay" onClick={onClose}>
            <div className="mc-dialog" onClick={(e) => e.stopPropagation()}>
                <div className="mc-header">
                    <h3 className="mc-title">{initial ? 'Chỉnh sửa đáp án' : 'Thêm đáp án'}</h3>
                    <div className="mc-icons">
                        <button className="mc-close" onClick={onClose} aria-label="Đóng">✕</button>
                    </div>
                </div>

                <form className="mc-form" onSubmit={submit}>
                    <div className="mc-field">
                        <label>Nội dung đáp án</label>
                        <input
                            type="text"
                            value={form.answerText}
                            onChange={(e) => change('answerText', e.target.value)}
                            placeholder="Nhập nội dung đáp án…"
                            required
                        />
                    </div>

                    {/* switch custom đã thêm trước đó */}
                    <div className="ma-field">
                        <span id="ma-correct-label" className="ma-label">Đáp án đúng?</span>
                        <div className="ma-switch">
                            <input
                                id={`ma-correct-${questionId ?? 'new'}`}
                                className="ma-switch-input"
                                type="checkbox"
                                aria-labelledby="ma-correct-label"
                                checked={!!form.isCorrect}
                                onChange={(e) => change('isCorrect', e.target.checked)}
                            />
                            <label className="ma-switch-label" htmlFor={`ma-correct-${questionId ?? 'new'}`}>
                                <span className="ma-switch-track"><span className="ma-switch-thumb" /></span>
                            </label>
                        </div>
                    </div>

                    {err && <div className="mc-error">{err}</div>}

                    <div className="mc-actions">
                        <button type="button" className="mc-btn ghost" onClick={onClose}>Hủy</button>
                        <button type="submit" className="mc-btn primary" disabled={submitting || !canSubmit}>
                            {submitting ? 'Đang lưu…' : (initial ? 'Cập nhật' : 'Lưu đáp án')}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default ModalAnswer;
