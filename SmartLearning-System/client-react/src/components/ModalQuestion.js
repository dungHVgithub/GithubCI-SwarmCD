// components/ModalQuestion.js
import React, { useEffect, useState } from 'react';
import Apis, { endpoints } from '../configs/Apis';
import { showSuccess, showError } from '../utils/toast';
import '../static/modalChapter.css'; // style modal dùng lớp mc-*

const ModalQuestion = ({ open, onClose, exerciseId, initial = null, onSaved }) => {
    const [form, setForm] = useState({ orderIndex: '', question: '', solution: '' });
    const [saving, setSaving] = useState(false);
    const [err, setErr] = useState('');
    const [questionId, setQuestionId] = useState(null);



    useEffect(() => {
        if (!open) return;
        if (initial) {
            // Edit: có sẵn id câu hỏi
            setForm({
                orderIndex: initial.orderIndex ?? '',
                question: initial.question ?? '',
                solution: initial.solution ?? ''
            });
            setQuestionId(initial.id ?? null);
        } else {
            // Create
            setForm({ orderIndex: '', question: '', solution: '' });
            setQuestionId(null);
        }
        setErr('');
    }, [open, initial]);

    if (!open) return null;

    const onChange = (e) => {
        const { name, value } = e.target;
        setForm((prev) => ({ ...prev, [name]: value }));
    };

    const validate = () => {
        const oi = parseInt(form.orderIndex, 10);
        if (Number.isNaN(oi) || oi < 0) return 'Thứ tự phải là số không âm';
        if (!form.question?.trim()) return 'Vui lòng nhập nội dung câu hỏi';
        return '';
    };

    const submit = async (e) => {
        e.preventDefault();
        const v = validate();
        if (v) { setErr(v); return; }

        setSaving(true);
        try {
            const payload = {
                orderIndex: parseInt(form.orderIndex, 10),
                question: form.question.trim(),
                solution: form.solution?.trim() || ''
            };

            if (initial?.id) {
                await Apis.put(`${endpoints.questions}/${initial.id}`, payload, { params: { exerciseId } });
                showSuccess('Cập nhật câu hỏi thành công.');
                onSaved && onSaved();
                onClose && onClose();
            } else {
                // tạo mới -> giữ modal lại để có thể thêm đáp án
                const res = await Apis.post(`${endpoints.questions}`, payload, { params: { exerciseId } });
                const savedId = res?.data?.id;
                setQuestionId(savedId);
                showSuccess('Tạo câu hỏi thành công. Bạn có thể bấm “＋ Thêm đáp án”.');
                onSaved && onSaved();
            }
        } catch (e) {
            console.error(e);
            showError('Lưu câu hỏi thất bại. Vui lòng thử lại.');
            setErr('Lưu câu hỏi thất bại. Vui lòng thử lại.');
        } finally {
            setSaving(false);
        }
    };

    const closeAll = () => {
        onClose && onClose();
    };

    return (
        <div className="mc-overlay" onClick={closeAll}>
            <div className="mc-dialog" onClick={(e) => e.stopPropagation()}>
                <div className="mc-header">
                    <h3 className="mc-title">{initial ? 'Chỉnh sửa câu hỏi' : 'Thêm câu hỏi'}</h3>
                    <div className="mc-icons">
                        {/* Close */}
                        <button className="mc-close" onClick={closeAll} aria-label="Đóng">✕</button>
                    </div>
                </div>

                <form className="mc-form" onSubmit={submit}>
                    <div className="mc-field">
                        <label>Thứ tự (orderIndex)</label>
                        <input
                            name="orderIndex"
                            type="number"
                            min="0"
                            value={form.orderIndex}
                            onChange={onChange}
                            placeholder="VD: 1"
                            required
                        />
                    </div>

                    <div className="mc-field">
                        <label>Nội dung câu hỏi</label>
                        <textarea
                            name="question"
                            rows={4}
                            value={form.question}
                            onChange={onChange}
                            placeholder="Nhập đề bài câu hỏi…"
                            required
                        />
                    </div>

                    <div className="mc-field">
                        <label>Lời giải (tuỳ chọn)</label>
                        <textarea
                            name="solution"
                            rows={4}
                            value={form.solution}
                            onChange={onChange}
                            placeholder="Nhập lời giải/giải thích nếu có…"
                        />
                    </div>

                    {err && <div className="mc-error">{err}</div>}

                    <div className="mc-actions">
                        <button type="button" className="mc-btn ghost" onClick={closeAll}>Hủy</button>
                        <button type="submit" className="mc-btn primary" disabled={saving}>
                            {saving ? 'Đang lưu…' : (initial ? 'Cập nhật' : 'Tạo mới')}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default ModalQuestion;
