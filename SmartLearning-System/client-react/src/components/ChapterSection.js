// components/ChapterSection.js
import React, { useEffect, useState, useContext } from 'react';
import { useParams } from 'react-router-dom';
import Apis, { authApis, endpoints, apiUrl } from '../configs/Apis';
import { SidebarContext } from '../reducers/SidebarContext';
import { MyUserContext } from '../reducers/MyUserReducer'; // ⬅️ lấy user, có role
import '../static/chapterSection.css';
import { showSuccess, showError } from '../utils/toast';
import ExerciseSection from './ExerciseSection';

const ChapterSection = () => {
    const { subjectId, chapterId } = useParams();
    const { collapsed } = useContext(SidebarContext);
    const user = useContext(MyUserContext);
    const role = user?.role || 'STUDENT'; // mặc định readonly nếu chưa đăng nhập
    const canManage = role === 'TEACHER';
    const [attachments, setAttachments] = useState([]);
    const [file, setFile] = useState(null);
    const [attachmentType, setAttachmentType] = useState('CONTENT'); // CONTENT | SUMMARY
    const [loading, setLoading] = useState(true);
    const [uploading, setUploading] = useState(false);
    const [chapterTitle, setChapterTitle] = useState('');

    const loadAttachments = async () => {
        setLoading(true);
        try {
            const res = await Apis.get(endpoints.chapterAttachments(chapterId));
            setAttachments(res.data || []);
        } catch (e) {
            console.error('Load attachments error:', e);
            setAttachments([]);
        }
        setLoading(false);
    };

    const loadChapterTitle = async () => {
        try {
            // lấy danh sách chapters theo subjectId rồi tìm đúng chapterId
            const resChapters = await Apis.get(`${endpoints.chapters}/${subjectId}`);
            const list = resChapters.data || [];
            const found = list.find(c => String(c.id) === String(chapterId));
            setChapterTitle(found?.title || '');
        } catch (e) {
            console.error('Load chapter title error:', e);
            setChapterTitle('');
        }
    };

    useEffect(() => {
        if (chapterId) {
            loadAttachments();
            loadChapterTitle();
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [chapterId]);

    const onUpload = async (e) => {
        e.preventDefault();
        if (!canManage) return; // chặn nếu không có quyền
        if (!file) return;

        const form = new FormData();
        form.append('file', file);
        form.append('type', attachmentType); // gửi type về BE (CONTENT | SUMMARY)

        setUploading(true);
        try {
            const res = await authApis().post(
                endpoints.chapterAttachments(chapterId),
                form,
                { headers: { 'Content-Type': 'multipart/form-data' } }
            );
            setAttachments(prev => [res.data, ...prev]);
            setFile(null);
            e.target.reset?.();
            showSuccess('Tệp đã được tải lên thành công!');
        } catch (err) {
            console.error('Upload failed:', err);
            showError('Tải tệp lên thất bại!');
        }
        setUploading(false);
    };

    const openInNewTab = (att) => {
        window.open(apiUrl(endpoints.attachmentOpen(att.id)), '_blank');
    };

    const downloadFile = (att) => {
        window.open(apiUrl(endpoints.attachmentDownload(att.id)), '_blank');
    };

    const deleteAttachment = async (attId) => {
        if (!canManage) return; // chặn nếu không có quyền
        if (!window.confirm('Xoá tệp này?')) return;
        try {
            await authApis().delete(endpoints.attachmentDelete(attId));
            setAttachments(prev => prev.filter(x => x.id !== attId));
            showSuccess('Tệp đã được xoá thành công!');
        } catch (err) {
            console.error('Delete failed:', err);
            showError('Xoá tệp thất bại!');
        }
    };

    // Label/icon theo extension (không dùng thumbnail image)
    const renderExtIcon = (ext) => {
        const e = (ext || '').toLowerCase();
        if (e === 'pdf') return <span className="cs-exttag pdf">PDF</span>;
        if (e === 'docx') return <span className="cs-exttag docx">DOCX</span>;
        if (e === 'doc') return <span className="cs-exttag doc">DOC</span>;
        return <span className="cs-exttag file">FILE</span>;
    };

    const humanType = (t) => (t === 'SUMMARY' ? 'TÓM TẮT' : t === 'CONTENT' ? 'NỘI DUNG' : t);

    return (
        <div className="cs-container" style={{ paddingLeft: collapsed ? '80px' : '300px' }}>
            {/* SECTION: Nội dung môn học */}
            <section className="content-subject" style={{ display: 'flex', marginTop: '20px' }}>
                <h2>Nội dung chương: {chapterTitle} </h2>
            </section>

            {/* Upload form (chỉ TEACHER thấy) */}
            {canManage && (
                <form onSubmit={onUpload} className="cs-upload">
                    <div className="cs-field">
                        <label>Loại tài liệu</label>
                        <select
                            value={attachmentType}
                            onChange={(e) => setAttachmentType(e.target.value)}
                        >
                            <option value="CONTENT">Nội dung bài học</option>
                            <option value="SUMMARY">Tóm tắt bài học</option>
                        </select>
                    </div>

                    <div className="cs-field">
                        <label>Chọn tệp</label>
                        <input
                            type="file"
                            accept=".pdf,.doc,.docx"
                            onChange={(e) => setFile(e.target.files?.[0] || null)}
                        />
                        <div className="cs-hint">Hỗ trợ PDF, DOC, DOCX</div>
                    </div>

                    <button type="submit" disabled={uploading || !file} className="cs-btn">
                        {uploading ? 'Đang tải lên...' : 'Tải lên'}
                    </button>
                </form>
            )}

            {/* Danh sách tệp */}
            <div className="cs-list">
                {loading ? (
                    <div className="cs-empty">Đang tải danh sách tệp...</div>
                ) : attachments.length === 0 ? (
                    <div className="cs-empty">Chưa có tệp đính kèm.</div>
                ) : (
                    attachments.map((att) => {
                        const ext = (att.extension || '').toLowerCase();
                        const badgeClass = att.type ? att.type.toLowerCase() : '';
                        return (
                            <div key={att.id} className="cs-item">
                                <div className="cs-thumb no-image">
                                    {renderExtIcon(ext)}
                                </div>

                                <div className="cs-meta">
                                    <div className="cs-title">
                                        {att.filename}
                                        <span className={`cs-badge ${badgeClass}`}>{humanType(att.type)}</span>
                                    </div>
                                    <div className="cs-sub">
                                        {ext.toUpperCase()} • {new Date(att.uploadedAt).toLocaleString()}
                                    </div>
                                </div>

                                <div className="cs-actions">
                                    <button type="button" className="cs-ghost" onClick={() => openInNewTab(att)}>Xem</button>
                                    <button type="button" className="cs-ghost" onClick={() => downloadFile(att)}>Tải về</button>
                                    {canManage && (
                                        <button
                                            type="button"
                                            className="cs-danger"
                                            onClick={() => deleteAttachment(att.id)}
                                        >
                                            Xoá
                                        </button>
                                    )}
                                </div>
                            </div>
                        );
                    })
                )}
            </div>
            <ExerciseSection chapterId={chapterId} role={role} />
        </div>
    );
};

export default ChapterSection;
