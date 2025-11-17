import React, { useState } from 'react';
import { FaEye, FaEyeSlash } from "react-icons/fa";
import '../static/login.css';
import Apis, { endpoints } from '../configs/Apis';

const ForgetPass = ({ onClose }) => {
    const [email, setEmail] = useState('');
    const [otp, setOtp] = useState('');
    const [pw, setPw] = useState('');
    const [cf, setCf] = useState('');

    const [showPw, setShowPw] = useState(false);
    const [showCf, setShowCf] = useState(false);

    const [sending, setSending] = useState(false);   // gửi OTP
    const [reseting, setReseting] = useState(false); // reset pass
    const [ok, setOk] = useState('');
    const [err, setErr] = useState('');

    const sendOtp = async () => {
        setOk(''); setErr('');
        if (!email.trim()) {
            setErr('Vui lòng nhập email trước khi gửi OTP.');
            return;
        }
        try {
            setSending(true);
            await Apis.patch(endpoints['forgot-pass'], { email }); // PATCH /auth/forgot-password
            setOk('Nếu tài khoản tồn tại, OTP đã được gửi tới email. Vui lòng kiểm tra hộp thư.');
        } catch (e) {
            // backend luôn trả message chung, nên mình cũng chỉ báo chung
            setErr('Không thể gửi OTP. Vui lòng thử lại sau.');
        } finally {
            setSending(false);
        }
    };

    const submitReset = async (e) => {
        e.preventDefault();
        setOk(''); setErr('');

        if (!email.trim()) return setErr('Vui lòng nhập email.');
        if (!otp.trim()) return setErr('Vui lòng nhập mã OTP.');
        if (!pw) return setErr('Vui lòng nhập mật khẩu mới.');
        if (pw.length < 6) return setErr('Mật khẩu phải có ít nhất 6 ký tự.');
        if (cf !== pw) return setErr('Mật khẩu xác nhận không khớp.');

        try {
            setReseting(true);
            await Apis.patch(endpoints['reset-pass'], {
                email,
                otp,
                newPassword: pw,
                confirmNewPassword: cf
            }); // PATCH /auth/reset-password
            setOk('Đặt lại mật khẩu thành công! Bạn có thể đăng nhập với mật khẩu mới.');
            setTimeout(() => onClose?.(), 1500);
        } catch (e2) {
            setErr('OTP không hợp lệ hoặc đã hết hạn, hoặc có lỗi khi đặt lại mật khẩu.');
        } finally {
            setReseting(false);
        }
    };

    return (
        <div className="login-container" style={{ minWidth: 360 }}>
            <div className="login-title">Đặt lại mật khẩu</div>

            <form className="forgot-form" onSubmit={submitReset}>
                {/* Email + nút Gửi OTP trên cùng 1 dòng */}
                {/* Email + nút Gửi OTP trên cùng 1 dòng */}
                <div className="email-otp-row">
                    <div className="email-col">
                        <label htmlFor="fp-email">Email</label>
                        <input
                            type="email"
                            id="fp-email"
                            className="email-otp-input"
                            value={email}
                            onChange={e => setEmail(e.target.value)}
                            required
                        />
                    </div>
                    <button
                        type="button"
                        className="btn-send-otp"
                        onClick={sendOtp}
                        disabled={sending || !email.trim()}
                        title="Gửi mã OTP tới email"
                    >
                        {sending ? 'Đang gửi...' : 'Gửi OTP'}
                    </button>
                </div>
                {/* OTP */}
                <div className="input-group">
                    <label htmlFor="fp-otp">Mã OTP</label>
                    <input
                        type="text"
                        id="fp-otp"
                        className="login-input"
                        value={otp}
                        onChange={e => setOtp(e.target.value)}
                        placeholder="Nhập mã 6 chữ số"
                        required
                        inputMode="numeric"
                        maxLength={6}
                    />
                </div>

                {/* Mật khẩu mới */}
                <div className="input-group password-group">
                    <label htmlFor="fp-pw">Mật khẩu mới</label>
                    <div className="password-wrapper">
                        <input
                            type={showPw ? "text" : "password"}
                            id="fp-pw"
                            className="login-input"
                            value={pw}
                            onChange={e => setPw(e.target.value)}
                            required
                            placeholder="Ít nhất 6 ký tự"
                        />
                        <span
                            className="toggle-password"
                            onClick={() => setShowPw(!showPw)}
                            title={showPw ? "Ẩn mật khẩu" : "Hiện mật khẩu"}
                        >
                            {showPw ? <FaEyeSlash /> : <FaEye />}
                        </span>
                    </div>
                </div>

                {/* Xác nhận mật khẩu */}
                <div className="input-group password-group">
                    <label htmlFor="fp-cf">Xác nhận mật khẩu</label>
                    <div className="password-wrapper">
                        <input
                            type={showCf ? "text" : "password"}
                            id="fp-cf"
                            className="login-input"
                            value={cf}
                            onChange={e => setCf(e.target.value)}
                            required
                        />
                        <span
                            className="toggle-password"
                            onClick={() => setShowCf(!showCf)}
                            title={showCf ? "Ẩn mật khẩu" : "Hiện mật khẩu"}
                        >
                            {showCf ? <FaEyeSlash /> : <FaEye />}
                        </span>
                    </div>
                </div>

                {err && <div className="login-error">{err}</div>}
                {ok && <div className="login-success">{ok}</div>}

                <div className="login-actions" style={{ display: 'flex', gap: 12 }}>
                    <button type="submit" className="login-btn" disabled={reseting}>
                        {reseting ? 'Đang đặt lại...' : 'Xác nhận'}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default ForgetPass;
