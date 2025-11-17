import React, { useState, useContext, useEffect } from "react";
import cookie from "react-cookies";
import Apis, { endpoints } from "../configs/Apis";
import { MyUserContext } from "../reducers/MyUserReducer";
import { useNavigate } from "react-router-dom";
import "../static/profile.css";

const Profile = () => {
    const user = useContext(MyUserContext);
    const nav = useNavigate();

    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");
    const [ok, setOk] = useState("");
    const [preview, setPreview] = useState("");

    const [form, setForm] = useState({
        name: "",
        email: "",
        password: "",
        avatarFile: null,
    });

    // Khởi tạo từ context
    useEffect(() => {
        if (!user) {
            nav("/");
            return;
        }
        setForm({
            name: user.name || "",
            email: user.email || "",
            password: "",
            avatarFile: null,
        });
        setPreview(user.avatar || "");
    }, [user, nav]);

    const onChange = (e) => {
        const { name, value } = e.target;
        setForm((f) => ({ ...f, [name]: value }));
    };

    const onPickAvatar = (e) => {
        const file = e.target.files?.[0];
        setForm((f) => ({ ...f, avatarFile: file || null }));
        if (file) setPreview(URL.createObjectURL(file));
    };

    const submit = async (e) => {
        e.preventDefault();
        if (!user?.id) {
            setError("Không xác định được người dùng đang đăng nhập.");
            return;
        }
        setSaving(true);
        setError("");
        setOk("");

        try {
            const fd = new FormData();
            fd.append("name", form.name);
            fd.append("email", form.email);
            fd.append("role", user.role);
            if (form.password && form.password.trim() !== "") {
                fd.append("password", form.password.trim());
            }
            if (form.avatarFile) {
                fd.append("avatar", form.avatarFile);
            }

            const token = localStorage.getItem("token") || cookie.load("token");
            const headers = token ? { Authorization: `Bearer ${token}` } : {};
            const res = await Apis.put(`${endpoints.users}/${user.id}`, fd, { headers });

            setOk("Cập nhật thành công!");
            if (res?.data?.avatar) setPreview(res.data.avatar);
        } catch (err) {
            setError(err?.response?.data || "Cập nhật thất bại");
        } finally {
            setSaving(false);
        }
    };

    if (!user) return null;

    return (
        <div className="profile-container">
            <div className="profile-card shadow-sm">
                <h2 className="profile-title">✨ Hồ sơ cá nhân - {user.role}</h2>

                {error && <div className="alert alert-danger">{String(error)}</div>}
                {ok && <div className="alert alert-success">{ok}</div>}

                <form onSubmit={submit} className="profile-form">

                    {/* Email + Họ tên */}
                    <div className="row-line-1">
                        <div className="form-group">
                            <label className="form-label fw-bold">Họ tên</label>
                            <input
                                type="text"
                                name="name"
                                value={form.name}
                                onChange={onChange}
                                className="form-control modern-input"
                                placeholder="Nhập họ tên"
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label fw-bold">Email</label>
                            <input
                                type="email"
                                name="email"
                                value={form.email}
                                onChange={onChange}
                                className="form-control modern-input"
                                placeholder="name@example.com"
                                required
                            />
                        </div>
                    </div>

                    {/* Role + Password */}
                    <div className="row-line-2">
                        <div className="form-group">
                            <label className="form-label fw-bold">Role</label>
                            <input
                                type="text"
                                className="form-control modern-input"
                                value={user.role || "STUDENT"}
                                disabled
                                readOnly
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label fw-bold">Mật khẩu mới</label>
                            <input
                                type="password"
                                name="password"
                                value={form.password}
                                onChange={onChange}
                                className="form-control modern-input"
                                placeholder="Để trống nếu không đổi"
                            />
                        </div>
                    </div>

                    {/* Avatar + Preview */}
                    <div className="row-line-3">
                        <div className="form-group">
                            <label className="form-label fw-bold">Ảnh đại diện</label>
                            <input
                                type="file"
                                accept="image/*"
                                onChange={onPickAvatar}
                                className="form-control modern-input"
                            />
                        </div>

                        {preview && (
                            <img
                                src={preview}
                                alt="avatar-preview"
                                className="avatar-preview"
                            />
                        )}
                    </div>

                    <div className="mt-4 text-end">
                        <button type="submit" className="btn btn-primary modern-btn" disabled={saving}>
                            {saving ? "Đang lưu..." : "💾 Lưu thay đổi"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Profile;
