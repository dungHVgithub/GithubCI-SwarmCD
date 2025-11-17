import React, { useContext, useMemo } from 'react';
import { MyUserContext } from '../../reducers/MyUserReducer';
import { SidebarContext } from '../../reducers/SidebarContext';
import { useNavigate } from 'react-router-dom';
import { FaChevronLeft, FaChevronRight, FaBook, FaCalendarAlt, FaAddressBook, FaRocketchat, FaRobot } from 'react-icons/fa';
import '../../static/sidebar.css';

const Sidebar = () => {
    const user = useContext(MyUserContext);            // Hook 1
    const { collapsed, setCollapsed } = useContext(SidebarContext); // Hook 2
    const navigate = useNavigate();                    // Hook 3

    const role = (user?.role ?? '').toUpperCase();
    const menus = useMemo(() => {
        if (role === 'STUDENT') {
            return [
                { icon: <FaBook />, title: 'Môn học', path: '/studentDashboard' },
                { icon: <FaCalendarAlt />, title: 'Kế hoạch học tập', path: `/studyPlans/${user.id}` },
                { icon: <FaRocketchat />, title: 'Tin nhắn', path: `/message/${user.id}` }
            ];
        }
        if (role === 'TEACHER') {
            return [
                { icon: <FaBook />, title: 'Môn học', path: '/teacherDashboard' }, // FaBook cho TEACHER
                { icon: <FaAddressBook />, title: 'Danh sách bài nộp', path: '/submission' },
                { icon: <FaRocketchat />, title: 'Tin nhắn', path: `/message/${user.id}` },
                { icon: <FaRobot />, title: 'Chat AI', path: `/chatAI/${user.id}` }
            ];
        }
        return []; // chưa đăng nhập / role khác → không hiển thị gì
    }, [role]);

    // ◆ Đặt return sau tất cả hooks
    if (!user) return null;

    const handleMenuClick = (path) => { if (path) navigate(path); };

    return (
        <div className={`sidebar${collapsed ? ' collapsed' : ''}`}>
            <button className="sidebar-toggle" onClick={() => setCollapsed(!collapsed)}>
                {collapsed ? <FaChevronRight size={18} /> : <FaChevronLeft size={18} />}
            </button>

            <div className="sidebar-menu">
                {menus.map((item, idx) => (
                    <div
                        key={idx}
                        className="sidebar-item"
                        data-tooltip={item.title}
                        onClick={() => handleMenuClick(item.path)}
                        style={{ cursor: 'pointer' }}
                    >
                        <span className="sidebar-icon">{item.icon}</span>
                        {!collapsed && <span className="sidebar-label">{item.title}</span>}
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Sidebar;