import { createContext } from "react";
import cookie from "react-cookies";

export const MyUserContext = createContext();
export const MyUserDispatchContext = createContext();

const MyUserReducer = (current, action) => {
    switch (action.type) {
        case "login":
            localStorage.setItem("token", action.payload.token); // Lưu token
            cookie.save("token", action.payload.token, { path: "/" }); // Lưu cookie token
            return {
                ...action.payload,
                isAuthenticated: true,
            };

        case "loginGoogle":
            localStorage.setItem("token", action.payload.token); // Lưu token từ Google
            cookie.save("token", action.payload.token, { path: "/" }); // Lưu cookie token
            return {
                ...action.payload,
                isAuthenticated: true,
            };

        case "logout":
            localStorage.removeItem("token"); // Xóa token khi logout
            cookie.remove("token", { path: "/" }); // Xóa cookie token
            return null;

        default:
            return current;
    }
};

export default MyUserReducer;