import React, { useRef, useState } from 'react';
import '../static/signUp.css';
import Apis, { endpoints } from '../configs/Apis';
import { useNavigate } from 'react-router-dom';
import Login from './Login';
import '../static/login.css'; // Import your login.css
import { FaEye, FaEyeSlash } from "react-icons/fa";


const SignUp = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [name, setName] = useState('');
    const [role, setRole] = useState('student');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);
    const [showLogin, setShowLogin] = useState(false);
    const [showSignUp, setShowSignUp] = useState(true);
    const [avatarURL, setAvatarURL] = useState(null);
    const [showPassword, setShowPassword] = useState(false);
    const nav = useNavigate();

    // Dùng useRef cho input file
    const avatarRef = useRef();

    // Xử lý preview ảnh khi chọn file
    const handleAvatarChange = e => {
        if (!e.target.files || e.target.files.length === 0) {
            setAvatarURL(null);
            return;
        }
        const file = e.target.files[0];
        setAvatarURL(URL.createObjectURL(file));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Validate dữ liệu
        if (!name.trim()) {
            setError("Vui lòng nhập họ và tên.");
            return;
        }
        if (!email.trim()) {
            setError("Vui lòng nhập email.");
            return;
        }
        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            setError("Email không hợp lệ.");
            return;
        }
        if (!password) {
            setError("Vui lòng nhập mật khẩu.");
            return;
        }
        if (password.length < 6) {
            setError("Mật khẩu phải có ít nhất 6 ký tự.");
            return;
        }
        if (!role) {
            setError("Vui lòng chọn vai trò.");
            return;
        }
        if (!avatarRef.current || avatarRef.current.files.length === 0) {
            setError("Vui lòng chọn ảnh đại diện!");
            return;
        }

        setError("");
        setSuccess("");
        setLoading(true);

        try {
            // Tạo form-data
            const formData = new FormData();
            formData.append("name", name);
            formData.append("email", email);
            formData.append("password", password);
            formData.append("role", role);
            formData.append("avatar", avatarRef.current.files[0]);

            const res = await Apis.post(endpoints.signup, formData, {
                headers: {
                    "Content-Type": "multipart/form-data"
                }
            });

            setSuccess("Đăng ký thành công! Vui lòng đăng nhập.");
            setError("");
            setName("");
            setEmail("");
            setPassword("");
            setRole("student");
            avatarRef.current.value = null;
            setAvatarURL(null);
            setTimeout(() => {
                setShowSignUp(false);
                setShowLogin(true);
            }, 1000);

        } catch (err) {
            if (err.response) {
                if (err.response.status === 500) {
                    setError("Đã có lỗi vui lòng thử lại sau.");
                } else if (err.response.data && err.response.data.message) {
                    setError(err.response.data.message);
                } else {
                    setError("Đăng ký thất bại.");
                }
            } else {
                setError("Có lỗi xảy ra. Vui lòng thử lại sau.");
            }
            setSuccess("");
        }
        finally {
            setLoading(false);
        }
    };
    return (
        <div className="signup-container">
            <div className="signup-title">Đăng ký tài khoản mới</div>
            {/* Chỉ render form đăng ký nếu showSignUp là true và showLogin là false */}
            {showSignUp && !showLogin && (
                <form className="signup-form" onSubmit={handleSubmit}>
                    <div className="signup-row">
                        <div className="input-group flex-1">
                            <label htmlFor="signup-name">Họ và tên</label>
                            <input
                                type="text"
                                id="signup-name"
                                className="signup-input"
                                value={name}
                                onChange={e => setName(e.target.value)}
                                required
                            />
                        </div>
                        <div className="input-group flex-1">
                            <label htmlFor="signup-email">Email</label>
                            <input
                                type="email"
                                id="signup-email"
                                className="signup-input"
                                value={email}
                                onChange={e => setEmail(e.target.value)}
                                required
                            />
                        </div>
                    </div>
                    <div className="input-group password-group">
                        <label htmlFor="signup-password">Mật khẩu</label>
                        <div className="password-wrapper">
                            <input
                                type={showPassword ? "text" : "password"}
                                id="signup-password"
                                className="signup-input"
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
                    </div>
                    <div className="input-group">
                        <label htmlFor="signup-role">Vai trò</label>
                        <select
                            id="signup-role"
                            className="signup-input"
                            value={role}
                            onChange={e => setRole(e.target.value)}
                            required
                        >
                            <option value="student">Học sinh</option>
                            <option value="teacher">Giáo viên</option>
                        </select>
                    </div>

                    <div className="signup-row align-center">
                        <div className="input-group flex-2">
                            <label htmlFor="signup-avatar">Ảnh đại diện</label>
                            <input
                                type="file"
                                id="signup-avatar"
                                className="signup-input"
                                accept="image/*"
                                ref={avatarRef}
                                onChange={handleAvatarChange}
                            />
                        </div>
                        <div className="avatar-preview">
                            {avatarURL ? (
                                <img src={avatarURL} alt="Avatar Preview" className="avatar-img" />
                            ) : (
                                <div className="avatar-placeholder" />
                            )}
                        </div>
                    </div>

                    {error && <div className="signup-error">{error}</div>}
                    {success && <div className="signup-success">{success}</div>}
                    <button type="submit" className="signup-btn" disabled={loading}>
                        {loading ? "Đang đăng ký..." : "Đăng ký"}
                    </button>
                </form>
            )}

            {/* Khi showLogin là true thì hiện modal login, ẩn hẳn form đăng ký */}
            {showLogin && (
                <div className="custom-modal">
                    <div className="custom-modal-content">
                        <button
                            className="modal-close-btn"
                            onClick={() => setShowLogin(false)}
                        >
                            ×
                        </button>
                        <Login />
                    </div>
                </div>
            )}
        </div>
    );
};
export default SignUp;
