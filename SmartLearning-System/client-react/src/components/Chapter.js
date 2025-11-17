import React, { useContext, useEffect, useState } from 'react';
import Apis, { endpoints, authApis } from '../configs/Apis';
import { useParams, useNavigate } from 'react-router-dom';
import { SidebarContext } from '../reducers/SidebarContext';
import { MyUserContext } from '../reducers/MyUserReducer';
import '../static/chapter.css';
import { showSuccess, showError } from '../utils/toast';
import ModalChapter from './ModalChapter';

const Chapter = () => {
    const { subjectId } = useParams();
    const navigate = useNavigate();
    const { collapsed } = useContext(SidebarContext);
    const user = useContext(MyUserContext);
    const role = user?.role || 'STUDENT';
    const canManage = role === 'TEACHER';

    const [subject, setSubject] = useState(null);
    const [chapters, setChapters] = useState([]);
    const [chapterProgress, setChapterProgress] = useState({});
    const [loading, setLoading] = useState(true);
    const [openModal, setOpenModal] = useState(false);
    const [editing, setEditing] = useState(null);
    const [refreshProgress, setRefreshProgress] = useState(false);

    const reload = async () => {
        setLoading(true);
        try {
            const resSubject = await Apis.get(`${endpoints.subjects}/${subjectId}`);
            setSubject(resSubject.data);

            const resChapters = await Apis.get(`${endpoints.chapters}/subject/${subjectId}`);
            const chaptersData = resChapters.data || [];
            setChapters(chaptersData);

            if (role === 'STUDENT' && user?.id) {
                const progressData = {};
                try {
                    const resProgress = await authApis().get(`${endpoints['chapter-progress']}/student/${user.id}`);
                    resProgress.data.forEach(p => {
                        progressData[p.chapterId.id] = { percent: p.percent, lastScore: p.lastScore };
                    });
                } catch (err) {
                    console.error('Error fetching progress:', err.response?.data || err.message);
                }
                chaptersData.forEach(ch => {
                    if (!progressData[ch.id]) {
                        progressData[ch.id] = { percent: 0, lastScore: 0 };
                    }
                });
                setChapterProgress(progressData);
            }
        } catch (err) {
            setSubject(null);
            setChapters([]);
            setChapterProgress({});
            console.error('Error loading chapter data:', err);
            showError('Tải dữ liệu thất bại. Vui lòng thử lại.');
        } finally {
            setLoading(false);
            setRefreshProgress(false);
        }
    };

    const recalculateProgress = async (chapterId) => {
        if (role !== 'STUDENT' || !user?.id) return;
        try {
            const url = `${endpoints['chapter-progress']}/recalculate/${user.id}/${chapterId}`;
            const res = await authApis().post(url);
            setChapterProgress(prev => ({
                ...prev,
                [chapterId]: { percent: res.data.percent, lastScore: res.data.lastScore }
            }));
            showSuccess('Cập nhật tiến độ thành công.');
        } catch (err) {
            console.error('Error recalculating progress:', err.response?.data || err.message);
            showError('Cập nhật tiến độ thất bại.');
        }
    };

    useEffect(() => {
        if (subjectId) reload();
        // eslint-disable-next-line
    }, [subjectId, refreshProgress]);

    const onExerciseSubmit = (chapterId) => {
        recalculateProgress(chapterId);
        setRefreshProgress(true);
    };

    const goToSection = (chapterId) => {
        navigate(`/chapters/${subjectId}/section/${chapterId}`);
    };

    const openCreate = () => {
        if (!canManage) return;
        setEditing(null);
        setOpenModal(true);
    };

    const openEdit = (ch, e) => {
        if (!canManage) return;
        e.stopPropagation();
        setEditing(ch);
        setOpenModal(true);
    };

    const onDelete = async (id, e) => {
        if (!canManage) return;
        e.stopPropagation();
        if (!window.confirm('Bạn có chắc muốn xóa chương này?')) return;
        try {
            await Apis.delete(`${endpoints.chapters}/${id}`);
            showSuccess('Xóa chương thành công.');
            reload();
        } catch (err) {
            console.error(err);
            showError('Xóa chương thất bại. Vui lòng thử lại sau.');
        }
    };

    return (
        <div className="chapter-main" style={{ paddingLeft: collapsed ? '80px' : '300px' }}>
            {loading ? (
                <div>Đang tải...</div>
            ) : (
                subject && (
                    <div className="chapter-header">
                        <div className="chapter-header-left">
                            <h1 className="chapter-title">{subject.title}</h1>
                            <p className="chapter-desc">{subject.description}</p>
                        </div>
                        <div className="chapter-header-img">
                            <img src={subject.image} alt={subject.title} />
                        </div>
                    </div>
                )
            )}

            <div className="chapter-toolbar">
                <h2 className="chapter-section-title">Các chương</h2>
                {canManage && (
                    <button className="chapter-add-btn" onClick={openCreate}>
                        + Thêm chương
                    </button>
                )}
            </div>

            <div className="chapter-list">
                {loading && <div>Đang tải danh sách chương...</div>}
                {!loading && chapters.length === 0 && <div className="chapter-empty">Chưa có chương nào.</div>}

                {!loading &&
                    chapters.map((ch) => (
                        <div
                            className="chapter-card"
                            key={ch.id}
                            onClick={() => goToSection(ch.id)}
                        >
                            {canManage && (
                                <div className="chapter-actions" onClick={(e) => e.stopPropagation()}>
                                    <button
                                        className="chapter-icon-btn"
                                        title="Sửa"
                                        aria-label="Sửa"
                                        onClick={(e) => openEdit(ch, e)}
                                    >
                                        ✏️
                                    </button>
                                    <button
                                        className="chapter-icon-btn danger"
                                        title="Xóa"
                                        aria-label="Xóa"
                                        onClick={(e) => onDelete(ch.id, e)}
                                    >
                                        🗑️
                                    </button>
                                </div>
                            )}

                            <div className="chapter-indexbox">
                                <div>{String(ch.orderIndex).padStart(2, '0')}</div>
                                <div className="chapter-lesson-text">LESSON</div>
                            </div>

                            <div className="chapter-info">
                                <div className="chapter-card-title">{ch.title}</div>
                                <div className="chapter-card-sumary">{ch.summaryText}</div>
                            </div>

                            {/* Nút reload ở TRONG card */}
                            {role === 'STUDENT' && (
                                <button
                                    className="chapter-icon-btn chapter-reload-btn"
                                    title="Cập nhật tiến độ"
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        recalculateProgress(ch.id);
                                    }}
                                >
                                    🔄
                                </button>
                            )}

                            {/* Cụm tiến độ: ở NGOÀI, bên phải, ngang hàng card */}
                            {role === 'STUDENT' && (
                                <div className="chapter-progress">
                                    <div className="progress-bar">
                                        <div
                                            className="progress-bar-fill"
                                            style={{ width: `${chapterProgress[ch.id]?.percent || 0}%` }}
                                        />
                                    </div>
                                    <div className="progress-text">
                                        Tiến độ: {chapterProgress[ch.id]?.percent || 0}%
                                    </div>
                                    <div className="score-text">
                                        Điểm: {chapterProgress[ch.id]?.lastScore || 0}
                                    </div>
                                </div>
                            )}
                        </div>
                    ))}
            </div>

            <ModalChapter
                open={openModal}
                onClose={() => setOpenModal(false)}
                subjectId={parseInt(subjectId, 10)}
                initial={editing}
                onSaved={reload}
            />
        </div>
    );
};

export default Chapter;
