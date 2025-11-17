// Submission.js
import React, { useEffect, useState, useCallback, useContext } from 'react';
import Apis, { endpoints } from '../configs/Apis';
import '../static/submission.css';
import { SidebarContext } from '../reducers/SidebarContext';
import { MyUserContext } from '../reducers/MyUserReducer';
import { useNavigate } from 'react-router-dom';

const Submission = () => {
    const { collapsed } = useContext(SidebarContext) || {};
    const leftOffset = collapsed ? 60 : 220;
    const topOffset = 80;

    const user = useContext(MyUserContext);
    const teacherId = user?.id;

    const [classes, setClasses] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [openClasses, setOpenClasses] = useState(() => new Set());
    //Navigate to load chapter 
    const navigate = useNavigate();

    const loadClasses = useCallback(async () => {
        if (!teacherId) { setClasses([]); return; }
        setLoading(true);
        setError('');
        try {
            // Gọi endpoint mới: /api/classes/assign/by-teacher/{teacherId}
            const res = await Apis.get(endpoints.classAssignmentByTeacher(teacherId));
            const assignments = Array.isArray(res.data) ? res.data : [res.data];

            // Transform TeacherAssignmentDTO[] -> shape cũ để UI hiện tại dùng lại được:
            // { id, className, teacherList: [{ userId, subjectList: SubjectDTO[] }] }
            const mapped = assignments.map(a => {
                const firstTeacher = (a.teachers && a.teachers.length > 0) ? a.teachers[0] : null;
                return {
                    id: a.id,
                    className: a.className,
                    teacherList: firstTeacher ? [{
                        userId: firstTeacher.teacherId,             // giữ lại info teacher id nếu cần
                        subjectList: firstTeacher.subjects || []    // danh sách môn dạy
                    }] : []
                };
            });

            setClasses(mapped);
        } catch (err) {
            console.error(err);
            setError('Không tải được danh sách lớp.');
            setClasses([]);
        } finally {
            setLoading(false);
        }
    }, [teacherId]);

    useEffect(() => { loadClasses(); }, [loadClasses]);

    const toggleClass = (classId) => {
        setOpenClasses(prev => {
            const next = new Set(prev);
            next.has(classId) ? next.delete(classId) : next.add(classId);
            return next;
        });
    };
    //Navigate to load chapter
    const onClickSubject = (subjectId) => {
        navigate("/submission/chapters", { state: { subjectId } });
    };
    return (
        <div
            className="submission-page"
            style={{ '--content-left': `${leftOffset}px`, '--content-top': `${topOffset}px` }}
        >
            <h2 className="submission-title">Danh sách lớp của bạn</h2>

            {loading && <div className="submission-loading">Đang tải lớp…</div>}
            {error && !loading && <div className="submission-error">{error}</div>}

            {!loading && !error && (
                <div className="submission-grid">
                    {classes.map((cls) => {
                        const id = cls.id;
                        const isOpen = openClasses.has(id);
                        const subjects = (cls.teacherList || []).flatMap(t => t.subjectList || []);

                        return (
                            <div key={id} className={`submission-card ${isOpen ? 'open' : ''}`}>
                                <button
                                    type="button"
                                    className="submission-header btn-reset"
                                    onClick={(e) => { e.stopPropagation(); toggleClass(id); }}
                                    aria-expanded={isOpen}
                                >
                                    <div className="submission-title-sm">{cls.className}</div>
                                    <span className="submission-caret" aria-hidden="true">
                                        <svg width="18" height="18" viewBox="0 0 24 24">
                                            <path d="M9 6l6 6-6 6" fill="none" stroke="currentColor" strokeWidth="2" />
                                        </svg>
                                    </span>
                                </button>

                                {isOpen && (
                                    <div className="submission-dropdown">
                                        {subjects.length > 0 && (
                                            <ul className="submission-subject-list">
                                                {subjects.map((subj) => (
                                                    <li key={subj.id} className="submission-subject-item" onClick={() => onClickSubject(subj.id)}>
                                                        <div className="submission-subject-row" style={{ display: "flex", alignItems: "center", gap: "8px", margin: "8px" }}>
                                                            {subj.image && (
                                                                <img
                                                                    src={subj.image}
                                                                    alt={subj.title || `Subject #${subj.id}`}
                                                                    style={{ width: "40px", height: "40px", objectFit: "cover", borderRadius: "4px", marginRight: "8px" }}
                                                                />
                                                            )}
                                                            <div className="submission-subject-title">
                                                                {subj.title || `Subject #${subj.id}`}
                                                            </div>
                                                        </div>
                                                    </li>
                                                ))}
                                            </ul>
                                        )}
                                    </div>
                                )}
                            </div>
                        );
                    })}
                    {classes.length === 0 && <div className="submission-empty">Chưa có lớp nào.</div>}
                </div>
            )}
        </div>
    );
};

export default Submission;
