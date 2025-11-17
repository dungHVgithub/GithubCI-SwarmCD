// src/components/layout/Header.js
import React, { useContext, useEffect, useCallback, useRef, useState } from "react";
import { MyUserContext, MyUserDispatchContext } from "../../reducers/MyUserReducer";
import { useNavigate } from "react-router-dom";
import logo from "../../asset/img/logo.png";
import "../../static/header.css";
import Login from "../../components/Login";
import SignUp from "../../components/SignUp";
import { FaBell, FaUserCircle } from "react-icons/fa";
import Notification from "../../components/Notification";
import cookie from 'react-cookies';
import Apis, { endpoints } from "../../configs/Apis";

const Header = () => {
  const user = useContext(MyUserContext);
  const dispatch = useContext(MyUserDispatchContext);
  const nav = useNavigate();

  const [showLogin, setShowLogin] = useState(false);
  const [showSignUp, setShowSignUp] = useState(false);

  // Notification (dropdown)
  const [showNotif, setShowNotif] = useState(false);
  const [notifItems, setNotifItems] = useState([]);
  const [notifLoading, setNotifLoading] = useState(false);
  const [badgeCount, setBadgeCount] = useState(0); // ✅ đếm dựa API isReaded=false
  const wrapRef = useRef(null); // click outside

  const handleCloseModal = () => {
    setShowLogin(false);
    setShowSignUp(false);
  };
  const handleLogout = () => {
    try {
      // dọn local token (phòng hờ — reducer cũng đã làm)
      localStorage.removeItem("token");
      cookie.remove("token", { path: "/" });
      // gỡ header Authorization khỏi axios instance
      if (Apis?.defaults?.headers?.common?.Authorization) {
        delete Apis.defaults.headers.common.Authorization;
      }
    } catch (_) { }
    // xóa state đăng nhập
    dispatch({ type: "logout" });
    nav("/");
    setShowLogin(false);
    setShowSignUp(false);
    setShowNotif(false);
    setNotifItems([]);
    setBadgeCount(0);
  };
  // Lấy role hiện tại (STUDENT/TEACHER)
  const getRole = () => {
    const u = (user && (user.user || user)) || {};
    return (u.role || u.roles || '').toString().toUpperCase();
  };

  // Lấy cặp {key, id, role} cho truy vấn thông báo
  //  - Teacher: key='teacherId', type=EXERCISE
  //  - Student: key='studentId', type=SUBMISSION
  const getViewerKeyAndId = () => {
    const u = (user && (user.user || user)) || {};
    const id = u.userId ?? u.id ?? null;
    const role = getRole();
    const isTeacher = role.includes('TEACHER');
    return { key: isTeacher ? 'teacherId' : 'studentId', id, role };
  };
  //Profile
  const handleUserProfile = () => {
    const id = user?.id || null;
    if (id) nav(`/profile/${id}`);
  };
  // ✅ Đếm số thông báo CHƯA ĐỌC (isReaded=false)
  const fetchUnreadCount = async () => {
    const { key, id, role } = getViewerKeyAndId();
    if (!id) { setBadgeCount(0); return; }
    try {
      let url = `${endpoints.notifications}?${key}=${id}&isReaded=false&limit=50&order=desc&sortBy=id`;
      url += role.includes('TEACHER') ? `&type=EXERCISE` : `&type=SUBMISSION`;
      const res = await Apis.get(url);
      const data = Array.isArray(res.data) ? res.data : [];
      setBadgeCount(data.length);
    } catch (e) {
      console.error("Fetch unread-count error:", e);
    }
  };
  // Lấy danh sách khi mở dropdown
  const fetchNotifications = async () => {
    const { key, id, role } = getViewerKeyAndId();
    if (!id) { setNotifItems([]); return; }
    try {
      setNotifLoading(true);
      let url = `${endpoints.notifications}?${key}=${id}&limit=10&order=desc&sortBy=id`;
      url += role.includes('TEACHER') ? `&type=EXERCISE` : `&type=SUBMISSION`;
      const res = await Apis.get(url);
      setNotifItems(Array.isArray(res.data) ? res.data : []);
    } catch (e) {
      console.error("Fetch notifications error:", e);
      setNotifItems([]);
    } finally {
      setNotifLoading(false);
    }
  };

  const toggleNotif = async () => {
    const nowOpen = !showNotif;
    setShowNotif(nowOpen);
    if (nowOpen) {
      await fetchNotifications();
      // Notification component sẽ gọi onMarkAllRead() khi open ⇒ sẽ chạy markAllRead()
    }
  };
  const markAllRead = useCallback(async () => {
    // UI trước để badge=0 ngay
    setNotifItems(prev => prev.map(n => ({ ...n, isReaded: true })));
    setBadgeCount(0);

    const { key, id, role } = getViewerKeyAndId();
    if (!id) return;
    try {
      let readAllUrl = `${endpoints.notifications}/read-all?${key}=${id}`;
      readAllUrl += role.includes('TEACHER') ? `&type=EXERCISE` : `&type=SUBMISSION`;
      await Apis.put(readAllUrl);
    } catch (e) {
      console.warn("Mark all read API failed:", e?.response?.status || e);
    }
  }, [user, endpoints.notifications]);

  // Đóng dropdown khi click ra ngoài
  useEffect(() => {
    if (!showNotif) return;
    const onDocClick = (e) => {
      if (wrapRef.current && !wrapRef.current.contains(e.target)) {
        setShowNotif(false);
      }
    };
    document.addEventListener("mousedown", onDocClick);
    return () => document.removeEventListener("mousedown", onDocClick);
  }, [showNotif]);

  // ✅ Khởi động & polling badge user thay đổi
  useEffect(() => {
    fetchUnreadCount();
  }, [user]);

  return (
    <>
      <nav className="navbar navbar-expand-lg navbar-light bg-light shadow-sm custom-header">
        <div className="header-container d-flex w-100 align-items-center justify-content-between">
          <div className="d-flex align-items-center">
            <img src={logo} alt="StudySmart Logo" className="header-logo" />
            <span className="navbar-brand mb-0 h1">StudySmart</span>
          </div>

          <div className="d-flex ms-auto">
            {user ? (
              <div className="d-flex align-items-center gap-3">
                {/* Bell + dropdown */}
                <div className="position-relative" ref={wrapRef}>
                  <FaBell
                    className="header-icon-notice"
                    style={{ cursor: "pointer" }}
                    onClick={toggleNotif}
                    title="Thông báo"
                  />
                  {badgeCount > 0 && (
                    <span
                      className="position-absolute translate-middle badge rounded-pill bg-danger"
                      style={{ top: 0, right: -6 }}
                    >
                      {badgeCount}
                    </span>
                  )}

                  {/* Dropdown Notifications */}
                  <Notification
                    open={showNotif}
                    items={notifItems}
                    loading={notifLoading}
                    onClose={() => setShowNotif(false)}
                    onRefresh={async () => {
                      await fetchNotifications();
                      await fetchUnreadCount(); // refresh badge sau khi làm mới danh sách
                    }}
                    onMarkAllRead={markAllRead} // ✅ khi mở dropdown => đánh dấu đã đọc tất cả
                  />
                </div>

                <FaUserCircle className="header-icon" onClick={handleUserProfile} />
                <button className="btn btn-outline-danger" onClick={handleLogout}>
                  Đăng xuất
                </button>
              </div>
            ) : (
              <>
                <button className="btn btn-outline-primary me-2" onClick={() => setShowLogin(true)}>
                  Đăng nhập
                </button>
                <button className="btn btn-primary" onClick={() => setShowSignUp(true)}>
                  Đăng ký
                </button>
              </>
            )}
          </div>
        </div>
      </nav>

      {/* Modal Login */}
      {showLogin && (
        <div className="custom-modal" onClick={handleCloseModal}>
          <div className="custom-modal-content" onClick={(e) => e.stopPropagation()}>
            <button className="modal-close-btn" onClick={handleCloseModal}>
              &times;
            </button>
            <Login onLoginSuccess={() => setShowLogin(false)} />
          </div>
        </div>
      )}

      {/* Modal Sign Up */}
      {showSignUp && (
        <div className="custom-modal" onClick={handleCloseModal}>
          <div className="custom-modal-content" onClick={(e) => e.stopPropagation()}>
            <button className="modal-close-btn" onClick={handleCloseModal}>
              &times;
            </button>
            <SignUp />
          </div>
        </div>
      )}
    </>
  );
};
export default Header;
