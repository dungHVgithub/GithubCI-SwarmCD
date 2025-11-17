// src/components/AuthGoogle.js
import React, { useEffect } from "react";
import axios from "axios";
import Apis, { apiUrl, authApis, endpoints } from "../configs/Apis"; // Đảm bảo đường dẫn đúng
import '../static/login.css';
import cookie from "react-cookies";
import { useContext } from "react";
import { MyUserDispatchContext } from "../reducers/MyUserReducer";
import { showError } from "../utils/toast";

/* global google */ // Thêm comment này để thông báo ESLint rằng 'google' là global từ SDK

const AuthGoogle = ({ onLoginSuccess }) => {
    const dispatch = useContext(MyUserDispatchContext);

    useEffect(() => {
        const clientId = "1025697872094-s8go4slmfh2l1am2hlc7aoodiur5a13d.apps.googleusercontent.com";

        const load = () => {
            if (window.google?.accounts) { // Kiểm tra an toàn với optional chaining
                console.log("Google SDK loaded successfully");
                window.google.accounts.id.initialize({
                    client_id: clientId,
                    callback: handleCredentialResponse,
                });
                window.google.accounts.id.renderButton(
                    document.getElementById("google-signin-btn"),
                    { theme: "outline", size: "large", width: 60 }
                );
            } else {
                console.error("Google SDK not loaded or accounts API not available");
            }
        };

        const existing = document.querySelector('script[src="https://accounts.google.com/gsi/client"]');
        if (existing) {
            load();
        } else {
            const script = document.createElement("script");
            script.src = "https://accounts.google.com/gsi/client";
            script.async = true;
            script.defer = true;
            script.onload = () => {
                console.log("Google SDK script loaded");
                load();
            };
            script.onerror = () => console.error("Error loading Google SDK script");
            document.body.appendChild(script);
        }
    }, []);

    const handleCredentialResponse = async (response) => {
        try {
            const res = await Apis.post(apiUrl(endpoints.authGoogle), { idToken: response.credential });
            const token = res.data.token;
            localStorage.setItem('token', token);
            cookie.save('token', token, { path: '/' });
            if (onLoginSuccess) onLoginSuccess(token);
        } catch (error) {
            console.error("Google login failed - Full error:", error.response?.data || error.message, error.response?.status);
            showError("Đăng nhập bằng Google thất bại. Vui lòng thử lại! Chi tiết: " + (error.response?.data || error.message));
        }
    };
    return <div id="google-signin-btn" className="google-signin-btn" />;
};

export default AuthGoogle;