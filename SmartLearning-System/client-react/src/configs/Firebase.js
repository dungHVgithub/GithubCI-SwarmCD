// Cấu hình & export Firebase (App, Auth, Firestore)
import { initializeApp, getApps } from "firebase/app";
import { getAuth, setPersistence, browserLocalPersistence } from "firebase/auth";
import { getFirestore } from "firebase/firestore";

// Ưu tiên .env; nếu chưa có .env, fallback sang config dev của bạn
const firebaseConfig = {
    apiKey: import.meta?.env?.VITE_FB_API_KEY || "AIzaSyA6Z2-RZBI1xMXV2QYJYuKsxNAZiupGO4Q",
    authDomain: import.meta?.env?.VITE_FB_AUTH_DOMAIN || "chat-study-7e1b9.firebaseapp.com",
    projectId: import.meta?.env?.VITE_FB_PROJECT_ID || "chat-study-7e1b9",
    storageBucket: import.meta?.env?.VITE_FB_STORAGE_BUCKET || "chat-study-7e1b9.firebasestorage.app",
    messagingSenderId: import.meta?.env?.VITE_FB_MESSAGING_SENDER_ID || "1059587639892",
    appId: import.meta?.env?.VITE_FB_APP_ID || "1:1059587639892:web:be307c28ba5115c59ffbc1",
    // measurementId là tùy chọn, không cần cho chat
};

// Tránh init trùng khi HMR (Vite) hoặc render lại
const app = getApps().length ? getApps()[0] : initializeApp(firebaseConfig);

// Auth
const auth = getAuth(app);
// Giữ phiên sau khi reload tab
setPersistence(auth, browserLocalPersistence).catch(() => { /* ignore dev */ });

// Firestore
const db = getFirestore(app);
// (Tuỳ chọn) Hàm đăng nhập bằng Custom Token – sẽ dùng ở bước đồng bộ với login cũ
export async function signInWithFirebaseCustomToken(customToken) {
    // import động để tránh circular khi tree-shaking
    const { signInWithCustomToken } = await import("firebase/auth");
    return signInWithCustomToken(auth, customToken);
}

export { app, auth, db };
export default app;
