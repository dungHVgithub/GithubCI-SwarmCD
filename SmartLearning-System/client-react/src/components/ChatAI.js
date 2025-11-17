import React, { useState, useRef, useEffect, useContext } from "react";
import { MyUserContext } from "../reducers/MyUserReducer";
import { SidebarContext } from "../reducers/SidebarContext";
import botAvatar from "../asset/img/avatarChat.jpg";
import "../static/chatAI.css";

const API_BASE = process.env.REACT_APP_AI_BASE_URL || "http://localhost:5000";
const LS_KEY = "chatai_uploaded_files";

export default function ChatAI() {
    const [messages, setMessages] = useState([
        { role: "ai", text: "Chào bạn 👋. Mình là trợ lý ảo. Hãy đặt câu hỏi nhé!" },
    ]);
    const [input, setInput] = useState("");
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState("");
    const [uploading, setUploading] = useState(false);

    const [file, setFile] = useState(null);
    const [uploadedFiles, setUploadedFiles] = useState([]);
    const [isDragging, setIsDragging] = useState(false);

    const bottomRef = useRef(null);
    const user = useContext(MyUserContext);
    const { collapsed } = useContext(SidebarContext);

    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages, loading]);

    // lấy danh sách file đã up trước đó
    useEffect(() => {
        (async () => {
            try {
                const r = await fetch(`${API_BASE}/embeddings`, { method: "GET" });
                if (r.ok) {
                    const data = await r.json();
                    // chuẩn hóa vài field hay gặp
                    const list = (Array.isArray(data) ? data : []).map((x, i) => ({
                        id: x.id || x.articleId || x.source || i,
                        name: x.name || x.filename || x.source?.split(/[\\/]/).pop() || `file-${i}`,
                    }));
                    setUploadedFiles(list);
                    localStorage.setItem(LS_KEY, JSON.stringify(list));
                } else {
                    const cache = localStorage.getItem(LS_KEY);
                    if (cache) setUploadedFiles(JSON.parse(cache));
                }
            } catch {
                const cache = localStorage.getItem(LS_KEY);
                if (cache) setUploadedFiles(JSON.parse(cache));
            }
        })();
    }, []);

    async function sendMessage() {
        const question = input.trim();
        if (!question || loading) return;

        setMessages((prev) => [...prev, { role: "user", text: question }]);
        setInput("");
        setErr("");
        setLoading(true);

        try {
            const res = await fetch(`${API_BASE}/query`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ question, top_k: 3 }),
            });
            if (!res.ok) throw new Error((await res.text()) || `HTTP ${res.status}`);
            const data = await res.json();
            const answer = data?.answer ?? "Không có câu trả lời.";
            setMessages((prev) => [...prev, { role: "ai", text: answer }]);
        } catch (e) {
            console.error(e);
            setErr("Gọi AI-service lỗi. Kiểm tra API_BASE/CORS và server Flask.");
            setMessages((prev) => [...prev, { role: "ai", text: "⚠️ Có lỗi khi gọi AI-service." }]);
        } finally {
            setLoading(false);
        }
    }

    function onKeyDown(e) {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    }

    // ---- Upload helpers ----
    async function uploadOne(fileObj) {
        const form = new FormData();
        form.append("file", fileObj);
        const res = await fetch(`${API_BASE}/upsert`, { method: "POST", body: form });
        if (!res.ok) throw new Error((await res.text()) || `HTTP ${res.status}`);
        return res.json();
    }

    async function handleUpload(e) {
        e?.preventDefault?.();
        if (!file) return;
        setUploading(true);
        setErr("");
        try {
            await uploadOne(file);
            setMessages((prev) => [
                ...prev,
                { role: "ai", text: "✅ Đã tải tài liệu lên. Mình sẽ dùng để trả lời các câu hỏi tiếp theo." },
            ]);
            const newList = [
                ...uploadedFiles,
                { id: `${Date.now()}-${file.name}`, name: file.name },
            ];
            setUploadedFiles(newList);
            localStorage.setItem(LS_KEY, JSON.stringify(newList));
            setFile(null);
        } catch (err) {
            console.error(err);
            setErr("Upload thất bại. Vui lòng thử lại.");
        } finally {
            setUploading(false);
        }
    }

    // Drag & Drop
    function onDragOver(e) {
        e.preventDefault();
        e.stopPropagation();
        setIsDragging(true);
    }
    function onDragLeave(e) {
        e.preventDefault();
        e.stopPropagation();
        setIsDragging(false);
    }
    async function onDrop(e) {
        e.preventDefault();
        e.stopPropagation();
        setIsDragging(false);
        const files = Array.from(e.dataTransfer.files || []).filter((f) =>
            [".pdf", ".md", ".txt"].some((ext) => f.name.toLowerCase().endsWith(ext))
        );
        if (files.length === 0) return;
        setUploading(true);
        try {
            for (const f of files) {
                await uploadOne(f);
                const newItem = { id: `${Date.now()}-${f.name}`, name: f.name };
                setUploadedFiles((prev) => {
                    const next = [...prev, newItem];
                    localStorage.setItem(LS_KEY, JSON.stringify(next));
                    return next;
                });
            }
            setMessages((prev) => [
                ...prev,
                { role: "ai", text: "✅ Đã nhận tài liệu bạn kéo thả." },
            ]);
        } catch (err) {
            console.error(err);
            setErr("Upload thất bại khi kéo-thả.");
        } finally {
            setUploading(false);
        }
    }

    const userAvatar = user?.avatar || "/default-avatar.png";
    const userName = user?.name || "Bạn";

    return (
        <div className="main-content" style={{ paddingLeft: collapsed ? "80px" : "300px" }}>
            <div className="chat-wrap">
                <div className="chat-grid">
                    {/* LEFT: upload */}
                    <aside className="upload-panel">
                        <h4 className="upload-title">📎 Gửi tài liệu</h4>
                        <p className="upload-desc">Kéo-thả hoặc chọn tệp <b>.pdf</b>, <b>.md</b>, <b>.txt</b>.</p>

                        <div
                            className={`dropzone ${isDragging ? "dragging" : ""}`}
                            onDragOver={onDragOver}
                            onDragLeave={onDragLeave}
                            onDrop={onDrop}
                        >
                            <div className="dz-icon">⬆</div>
                            <div className="dz-text">Kéo tệp vào đây để tải lên</div>
                            <div className="dz-sub">hoặc</div>
                            <form className="upload-form" onSubmit={handleUpload}>
                                <input
                                    type="file"
                                    accept=".pdf,.md,.txt"
                                    onChange={(e) => setFile(e.target.files?.[0] || null)}
                                />
                                <button type="submit" disabled={!file || uploading} className="chat-btn full">
                                    {uploading ? "Đang gửi..." : "Tải lên"}
                                </button>
                            </form>
                        </div>

                        <div className="uploaded-list">
                            <div className="list-title">📚 Tài liệu đã tải lên</div>
                            {uploadedFiles.length === 0 ? (
                                <div className="empty">Chưa có tệp nào.</div>
                            ) : (
                                <ul>
                                    {uploadedFiles.map((f) => (
                                        <li key={f.id} title={f.name}>
                                            <span className="file-dot">•</span>
                                            <span className="file-name">{f.name}</span>
                                        </li>
                                    ))}
                                </ul>
                            )}
                        </div>
                        <div className="uploaded-actions">
                            <button
                                type="button"
                                className="chat-btn danger full"
                                onClick={() => {
                                    localStorage.removeItem(LS_KEY);
                                    setUploadedFiles([]);
                                }}
                            >
                                Xóa lịch sử
                            </button>
                        </div>
                        <div className="whoami">
                            <img src={userAvatar} alt="user" className="avatar lg" />
                            <div>
                                <div className="whoami-name">{userName}</div>
                                <div className="whoami-tip">Tệp bạn gửi sẽ được dùng cho phiên hỏi đáp.</div>
                            </div>
                        </div>
                    </aside>

                    {/* RIGHT: chat */}
                    <section className="chat-panel">
                        <div className="chat-box">
                            {messages.map((m, idx) => {
                                const isUser = m.role === "user";
                                const avatarSrc = isUser ? userAvatar : botAvatar;
                                const who = isUser ? userName : "AI";
                                return (
                                    <div key={idx} className={`chat-row ${isUser ? "right" : "left"}`}>
                                        {!isUser && <img src={avatarSrc} alt={who} className="avatar" />}
                                        <div className={`bubble ${isUser ? "user" : "ai"}`}>
                                            <div className="name">{who}</div>
                                            <div className="text" style={{ whiteSpace: "pre-wrap" }}>{m.text}</div>
                                        </div>
                                        {isUser && <img src={avatarSrc} alt={who} className="avatar" />}
                                    </div>
                                );
                            })}
                            {loading && <div className="typing">AI đang trả lời…</div>}
                            <div ref={bottomRef} />
                        </div>

                        {err && <div className="chat-error">{err}</div>}

                        <div className="input-row">
                            <textarea
                                rows={2}
                                value={input}
                                onChange={(e) => setInput(e.target.value)}
                                onKeyDown={onKeyDown}
                                placeholder="Nhập câu hỏi… (Enter để gửi, Shift+Enter để xuống dòng)"
                                className="chat-input"
                            />
                            <button onClick={sendMessage} disabled={loading || !input.trim()} className="chat-btn">
                                Gửi
                            </button>
                        </div>
                    </section>
                </div>
            </div>
        </div>
    );
}
