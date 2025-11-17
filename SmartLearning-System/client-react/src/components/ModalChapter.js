// components/ModalChapter.js
import React, { useEffect, useState } from 'react';
import Apis, { endpoints } from '../configs/Apis';
import { showSuccess, showError } from '../utils/toast';  // <-- thêm
import '../static/modalChapter.css';

const ModalChapter = ({ open, onClose, subjectId, initial = null, onSaved }) => {
    const [form, setForm] = useState({ orderIndex: '', title: '', summaryText: '' });
    const [saving, setSaving] = useState(false);
    const [err, setErr] = useState('');

    useEffect(() => {
        if (!open) return;
        if (initial) {
            setForm({
                orderIndex: initial.orderIndex ?? '',
                title: initial.title ?? '',
                summaryText: initial.summaryText ?? ''
            });
        } else {
            setForm({ orderIndex: '', title: '', summaryText: '' });
        }
        setErr('');
    }, [open, initial]);

    if (!open) return null;

    const onChange = (e) => {
        const { name, value } = e.target;
        setForm(prev => ({ ...prev, [name]: value }));
    };

    const validate = () => {
        if (!form.title?.trim()) return 'Vui lòng nhập tiêu đề chương';
        const oi = parseInt(form.orderIndex, 10);
        if (Number.isNaN(oi) || oi < 0) return 'Thứ tự phải là số không âm';
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
                title: form.title.trim(),
                summaryText: form.summaryText?.trim() || ''
            };

            if (initial?.id) {
                await Apis.put(`${endpoints.chapters}/${initial.id}`, payload, { params: { subjectId } });
                showSuccess('Cập nhật chương thành công.');            // <-- toast PUT
            } else {
                await Apis.post(`${endpoints.chapters}`, payload, { params: { subjectId } });
                showSuccess('Tạo chương thành công.');                 // <-- toast POST
            }

            onSaved && onSaved();
            onClose && onClose();
        } catch (e) {
            console.error(e);
            showError('Lưu chương thất bại. Vui lòng thử lại.');      // <-- toast lỗi
            setErr('Lưu chương thất bại. Vui lòng thử lại.');
        } finally {
            setSaving(false);
        }
    };

    return (
        <div className="mc-overlay" onClick={onClose}>
            <div className="mc-dialog" onClick={(e) => e.stopPropagation()}>
                <div className="mc-header">
                    <h3>{initial ? 'Chỉnh sửa chương' : 'Thêm chương'}</h3>
                    <button className="mc-close" onClick={onClose} aria-label="Đóng">✕</button>
                </div>

                <form className="mc-form" onSubmit={submit}>
                    <div className="mc-field">
                        <label>Thứ tự (orderIndex)</label>
                        <input name="orderIndex" type="number" min="0"
                            value={form.orderIndex} onChange={onChange} placeholder="VD: 1" required />
                    </div>

                    <div className="mc-field">
                        <label>Tiêu đề (title)</label>
                        <input name="title" type="text"
                            value={form.title} onChange={onChange} placeholder="VD: Hàm số" required />
                    </div>

                    <div className="mc-field">
                        <label>Mô tả ngắn (summaryText)</label>
                        <textarea name="summaryText" rows={4}
                            value={form.summaryText} onChange={onChange}
                            placeholder="Mô tả nội dung chương…" />
                    </div>

                    {err && <div className="mc-error">{err}</div>}

                    <div className="mc-actions">
                        <button type="button" className="mc-btn ghost" onClick={onClose}>Hủy</button>
                        <button type="submit" className="mc-btn primary" disabled={saving}>
                            {saving ? 'Đang lưu…' : (initial ? 'Cập nhật' : 'Tạo mới')}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default ModalChapter;
