import React, { useContext, useState } from 'react';
import '../../static/welcome.css';
import humanStudying from '../../asset/img/humanStudying.png';
import Login from '../Login'; // Adjust the import path to your login.js
import '../../static/login.css';    // Import your login.css
import { MyUserContext } from '../../reducers/MyUserReducer';
import { useNavigate } from 'react-router-dom';

const Welcome = () => {
    const [showLogin, setShowLogin] = useState(false);
    const user = useContext(MyUserContext);
    const nav = useNavigate();
    if (user != null) {
        user.role === "student" ? nav("/studentDashboard") : nav("/teacherDashboard");
    }

    return (
        <div className="welcome-container">
            <div className="welcome-content">
                <div className="welcome-left">
                    <span className="welcome-message">
                        Tối ưu hóa trải nghiệm học tập và giảng dạy của bạn
                    </span>
                    <button
                        className="explore-btn"
                        onClick={() => setShowLogin(true)}
                    >
                        Khám phá ngay
                    </button>
                </div>
                <div className="welcome-right">
                    <img src={humanStudying} alt="Human studying" className="welcome-img" />
                </div>
            </div>
            {showLogin && (
                <div className="custom-modal">
                    <div className="custom-modal-content">
                        <button className="modal-close-btn" onClick={() => setShowLogin(false)}>
                            &times;
                        </button>
                        <Login />
                    </div>
                </div>
            )}
        </div>
    );
};
export default Welcome;
