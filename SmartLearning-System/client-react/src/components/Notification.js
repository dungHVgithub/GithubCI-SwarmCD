// Notification.js
import React, { useEffect, useRef } from 'react';
import '../static/notification.css';

const toMillis = (ts) => {
    if (ts == null) return null;
    if (typeof ts === 'string') {
        const parsed = Date.parse(ts);
        if (!Number.isNaN(parsed)) return parsed;
        const n = Number(ts);
        if (!Number.isNaN(n)) ts = n; else return null;
    }
    if (typeof ts === 'number') return ts < 1e12 ? ts * 1000 : ts;
    return null;
};
const timeAgo = (ts) => {
    const ms = toMillis(ts);
    if (!ms) return '';
    const diff = Math.max(1, Math.floor((Date.now() - ms) / 1000));
    if (diff < 60) return `${diff}s trước`;
    const m = Math.floor(diff / 60);
    if (m < 60) return `${m}m trước`;
    const h = Math.floor(m / 60);
    if (h < 24) return `${h}h trước`;
    const d = Math.floor(h / 24);
    return `${d}d trước`;
};

const Notification = ({ open, items = [], loading = false, onClose, onMarkAllRead }) => {
    // ESC để đóng (giữ nguyên)
    useEffect(() => {
        if (!open) return;
        const onEsc = (e) => e.key === 'Escape' && onClose && onClose();
        window.addEventListener('keydown', onEsc);
        return () => window.removeEventListener('keydown', onEsc);
    }, [open, onClose]);

    // ✅ Chỉ mark read 1 lần mỗi lần mở
    const markedRef = useRef(false);
    useEffect(() => {
        if (!open) {
            markedRef.current = false;
            return;
        }
        if (loading) return;
        if (markedRef.current) return;
        if (typeof onMarkAllRead === 'function') onMarkAllRead();
        markedRef.current = true;
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [open, loading]);

    if (!open) return null;
    return (
        <div className="notif-dropdown shadow">
            <div className="notif-header d-flex align-items-center justify-content-between">
                <span className="fw-semibold">Thông báo</span>
                <div className="d-flex gap-2">
                    <button className="btn-onclose-noti" onClick={onClose}>
                        X
                    </button>
                </div>
            </div>

            <div className="notif-list">
                {loading && <div className="text-center text-muted py-3">Đang tải...</div>}
                {!loading && items.length === 0 && (
                    <div className="text-center text-muted py-3">Chưa có thông báo</div>
                )}
                {!loading && items.map(n => (
                    <div key={n.id} className={`notif-item ${n.isReaded ? '' : 'unread'}`}>
                        <div className="d-flex align-items-center">
                            <div className="flex-grow-1">
                                <div className="notif-title">{n.title}</div>
                                <div className="notif-message">
                                    {/* Ưu tiên message từ server; fallback theo type */}
                                    {(n.type === 'SUBMISSION'
                                        ? `${n.teacher?.name || ''} đã chấm bài làm của bạn`
                                        : n.type === 'EXERCISE'
                                            ? `${n.student?.name || ''} đã nộp bài`
                                            : ''
                                    )
                                    }
                                </div>
                            </div>
                        </div>
                        <div className="notif-meta d-flex align-items-center">
                            <span className="badge text-bg-light">{n.type}</span>
                            <small className="text-muted ms-auto">{timeAgo(n.sentAt)}</small>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Notification;
