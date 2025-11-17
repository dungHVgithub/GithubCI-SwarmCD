import React, { useState, useContext } from 'react';
import '../static/login.css';
import Apis, { endpoints, authApis } from '../configs/Apis';
import { MyUserDispatchContext } from '../reducers/MyUserReducer';
import { MyUserContext } from '../reducers/MyUserReducer';
import { useNavigate } from 'react-router-dom';
import { FaEye, FaEyeSlash } from "react-icons/fa";
import cookie from 'react-cookies';
import AuthGoogle from '../configs/AuthGoogle';
import ForgetPass from './ForgetPass';
import { signInWithFirebaseCustomToken, auth } from '../configs/Firebase';

const Login = ({ onLoginSuccess }) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [showPassword, setShowPassword] = useState(false); // 👈 state ẩn/hiện
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const dispatch = useContext(MyUserDispatchContext);
    const user = useContext(MyUserContext);
    const role = user?.role || 'STUDENT'; // mặc định readonly nếu chưa đăng nhập
    const nav = useNavigate();
    const [showForget, setShowForget] = useState(false); // 👈 state mở modal

    const initializeChapterProgress = async (studentId) => {
        try {
            const url = endpoints['chapter-progress'] + `/initialize/${studentId}`;
            console.log('Calling API:', url);
            await authApis().post(url);
            console.log('ChapterProgress initialized successfully');
        } catch (err) {
            console.error('Error initializing ChapterProgress:', err);
            setError('Không thể khởi tạo tiến độ học tập. Vui lòng thử lại.');
        }
    };
    // Đăng nhập Firebase bằng custom token từ backend
    const attachFirebaseSession = async () => {
        try {
            // gọi backend lấy custom token (đã có JWT nhờ authApis)
            const fbRes = await authApis().get(endpoints.firebase);
            const fbToken = fbRes?.data?.token;
            if (!fbToken) throw new Error("Không nhận được Firebase custom token");

            // đăng nhập Firebase
            await signInWithFirebaseCustomToken(fbToken);

            // Thông báo test: Firebase OK
            console.log("Firebase sign-in OK. uid =", auth.currentUser?.uid);
            return true;
        } catch (e) {
            console.error("Firebase sign-in failed:", e);
            setError('Xác thực firebase thất bại');
            return false;
        }
    };
    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setSuccess('');

        // Validate input
        if (!email || !password) {
            setError('Vui lòng nhập email và mật khẩu.');
            setLoading(false);
            return;
        }

        try {
            const res = await Apis.post(endpoints['login'], {
                email,
                password,
            });
            const token = res.data.token;
            if (!token) {
                throw new Error('Không có token trong phản hồi');
            } else {
                cookie.save("token", token);
                localStorage.setItem('token', token);
            }
            const currentUser = await authApis().get(endpoints.auth);
            const userInfo = currentUser.data;
            if (res.data && res.data.token) {
                setSuccess('Đăng nhập thành công!');
                setError('');
                dispatch({
                    type: "login",
                    payload: {
                        token: token,
                        email: userInfo.email,
                        name: userInfo.name,
                        role: userInfo.role,
                        id: userInfo.id,
                        avatar: userInfo.avatar
                    }
                });
                attachFirebaseSession(); // Đăng nhập Firebases
                if (onLoginSuccess) onLoginSuccess(); // Tắt modal khi login thành công!
                if (userInfo.role === "STUDENT") {
                    await initializeChapterProgress(userInfo.id);
                    nav('/studentDashboard');
                }
                else if (userInfo.role === "TEACHER") {
                    nav('/teacherDashboard');
                }
            } else {
                setError('Đăng nhập thất bại. Vui lòng kiểm tra lại.');
                setSuccess('');
            }
        } catch (err) {
            if (err.response && err.response.status === 400) {
                setError('Email hoặc mật khẩu không đúng!');
            } else {
                setError('Email hoặc mật khẩu không đúng!');
            }
            setSuccess('');
        } finally {
            setLoading(false);
        }
    };
    const handleGoogleLoginSuccess = async (token) => {
        setLoading(true);
        setError('');
        setSuccess('');
        try {
            console.log("JWT token for profile call: ", token); // Log JWT trước khi gọi profile
            const currentUser = await authApis().get(endpoints.auth); // Sửa endpoint thành endpoints.auth (/auth/user)
            const userInfo = currentUser.data;
            dispatch({
                type: "loginGoogle",
                payload: {
                    token: token,
                    email: userInfo.email,
                    name: userInfo.name,
                    role: userInfo.role,
                    id: userInfo.id,
                    avatar: userInfo.avatar
                }
            });
            attachFirebaseSession(); // Đăng nhập Firebases
            setSuccess('Đăng nhập bằng Google thành công!');
            setError('');
            if (onLoginSuccess) onLoginSuccess(); // Tắt modal khi login thành công
            await initializeChapterProgress(userInfo.id);
            nav('/studentDashboard');
        } catch (err) {
            console.error("Profile fetch error after Google login: ", err); // Log lỗi gọi profile
            setError('Có lỗi xảy ra sau khi đăng nhập bằng Google. Vui lòng thử lại.');
            setSuccess('');
        } finally {
            setLoading(false);
        }
    };
    const handleForgetPass = () => {
        setShowForget(true);
    };
    return (
        <div className="login-container">
            <div className="login-title">Đăng nhập</div>
            <form className="login-form" onSubmit={handleSubmit}>
                <div className="input-group">
                    <label htmlFor="email">Email</label>
                    <input
                        type="email"
                        id="email"
                        className="login-input"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                        required
                        autoFocus
                    />
                </div>
                <div className="input-group password-group">
                    <label htmlFor="password">Mật khẩu</label>
                    <div className="password-wrapper">
                        <input
                            type={showPassword ? "text" : "password"}
                            id="password"
                            className="login-input"
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                            required
                        />
                        <span
                            className="toggle-password"
                            onClick={() => setShowPassword(!showPassword)}
                        >
                            {showPassword ? <FaEyeSlash /> : <FaEye />}
                        </span>
                    </div>
                    <span className="forget-pass" onClick={handleForgetPass}> Quên mật khẩu ? </span>
                </div>
                {error && <div className="login-error">{error}</div>}
                {success && <div className="login-success">{success}</div>}
                <AuthGoogle onLoginSuccess={handleGoogleLoginSuccess} />
                <div className="login-actions">
                    <button type="submit" className="login-btn" disabled={loading}>
                        {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
                    </button>
                </div>
            </form>
            {showForget && (
                <div className="custom-modal" onClick={() => setShowForget(false)}>
                    <div className="custom-modal-content" onClick={(e) => e.stopPropagation()}>
                        <button className="modal-close-btn" onClick={() => setShowForget(false)}>×</button>
                        <ForgetPass onClose={() => setShowForget(false)} />
                    </div>
                </div>
            )}
        </div>
    );
};
export default Login;