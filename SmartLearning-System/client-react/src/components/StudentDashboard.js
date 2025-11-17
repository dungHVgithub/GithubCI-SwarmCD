import React, { useContext, useState, useEffect } from 'react';
import '../static/studentDashboard.css';
import { MyUserContext } from '../reducers/MyUserReducer';
import { SidebarContext } from '../reducers/SidebarContext';
import Apis, { endpoints } from '../configs/Apis';
import { useNavigate } from "react-router-dom";

const StudentDashboard = () => {
  const user = useContext(MyUserContext);
  const [subjects, setSubjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const { collapsed } = useContext(SidebarContext);
  const navigate = useNavigate();



  useEffect(() => {
    if (!user) {
      navigate('/');
    }
    const fetchSubjects = async () => {
      if (!user?.id) return;
      setLoading(true);
      try {
        const response = await Apis.get(`${endpoints.students}/${user.id}`);
        setSubjects(response.data.subjectList || []);
      } catch (error) {
        setSubjects([]);
        console.error("Error fetching student subjects:", error);
      }
      setLoading(false);
    };
    fetchSubjects();
  }, [user?.id]);

  // Handler khi click vào 1 subject
  const handleSubjectClick = (subjectId) => {
    navigate(`/chapters/${subjectId}`);
  };

  return (
    <div className="main-content" style={{ paddingLeft: collapsed ? '80px' : '300px' }}>
      <section className="welcome-section">
        <div className="avatar-wrapper">
          <img
            src={user?.avatar || "/default-avatar.png"}
            alt="Avatar"
            className="user-avatar"
          />
        </div>
        <span className="welcome-text">
          Xin chào, <b>{user?.name || "Bạn"}</b> chào mừng bạn đến với hệ thống SmartStudy!
        </span>
      </section>
      <section className="subject-section">
        <h2 className="section-list">Các môn học của bạn</h2>
        <div className="subject-list">
          {loading && <p>Đang tải dữ liệu...</p>}
          {!loading && subjects.length === 0 && <p>Bạn chưa có môn học nào.</p>}
          {subjects.map(subject => (
            <div
              key={subject.id}
              className="subject-card"
              style={{ cursor: 'pointer' }}
              onClick={() => handleSubjectClick(subject.id)}
            >
              <div className="subject-card-imgbox">
                <img src={subject.image} alt={subject.title} className="subject-card-img-full" />
              </div>
              <div className="subject-card-body">
                <div className="subject-card-title">{subject.title}</div>
                <div className="subject-card-teacher">
                  Giáo viên: {subject.teacherNames || "Chưa cập nhật"}
                </div>
              </div>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
};

export default StudentDashboard;
