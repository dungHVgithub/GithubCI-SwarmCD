// src/pages/Message.js
import React, { useContext, useEffect, useMemo, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import "../static/message.css";
import { SidebarContext } from "../reducers/SidebarContext";
// Firebase
import { auth, db } from "../configs/Firebase";
import { onAuthStateChanged } from "firebase/auth";
import {
    collection, query, where, orderBy, limit, onSnapshot,
    doc, getDoc, setDoc, addDoc, serverTimestamp
} from "firebase/firestore";
// API nội bộ
import { authApis, endpoints } from "../configs/Apis"; // dùng endpoints.students, endpoints.teachers, endpoints.auth  :contentReference[oaicite:2]{index=2}
import { MyUserContext } from "../reducers/MyUserReducer";

//--Helper--//
// Chuẩn hoá 1 record user API: { userId, user: { name, mail, avatar }, subjectList: [ {id, ...} ] }
const simplifyUser = (u) => ({
    id: String(u.userId),
    name: u.user?.name || "",
    mail: u.user?.mail || "",
    avatar: u.user?.avatar || "",
    subjectIds: (u.subjectList || []).map(s => String(s.id)),
});

// Kiểm tra 2 tập subject có giao nhau không
const hasSubjectOverlap = (aIds = [], bIds = []) => {
    const setA = new Set(aIds.map(String));
    return bIds.some(x => setA.has(String(x)));
};


/** convId direct định danh duy nhất giữa 2 user */
function makeDirectConvId(a, b) {
    const [x, y] = [String(a), String(b)].sort((m, n) => (m < n ? -1 : 1));
    return `message_${x}_${y}`;
}



/** Đảm bảo tồn tại conversation direct; trả về convId */
async function ensureDirectConversation(currentUid, peerUid) {
    const convId = makeDirectConvId(currentUid, peerUid);
    const cRef = doc(db, "conversations", convId);
    const snap = await getDoc(cRef);
    if (!snap.exists()) {
        await setDoc(
            cRef,
            {
                type: "direct",
                memberIds: [String(currentUid), String(peerUid)],
                createdAt: serverTimestamp(),
                lastMessageAt: serverTimestamp(),
                lastMessageText: "",
            },
            { merge: true }
        );
    }
    return convId;
}

/** Format giờ phút đơn giản cho list */
const fmtTime = (ts) => {
    try {
        if (!ts?.toDate) return "";
        const d = ts.toDate();
        const hh = String(d.getHours()).padStart(2, "0");
        const mm = String(d.getMinutes()).padStart(2, "0");
        return `${hh}:${mm}`;
    } catch {
        return "";
    }
};
async function markConversationRead(convId, currentUid) {
    if (!convId || !currentUid) return;
    await setDoc(
        doc(db, "conversations", convId),
        { lastReadAt: { [String(currentUid)]: serverTimestamp() } },
        { merge: true }
    );
}


// Layout tránh Header + Sidebar
const HEADER_HEIGHT = 80;
const SIDEBAR_W_OPEN = 220;
const SIDEBAR_W_COLLAPSED = 60;

const Message = () => {
    const { selfId } = useParams();
    const { collapsed } = useContext(SidebarContext) || { collapsed: false };

    const pageStyle = useMemo(
        () => ({
            paddingTop: HEADER_HEIGHT + 16,
            paddingLeft: (collapsed ? SIDEBAR_W_COLLAPSED : SIDEBAR_W_OPEN) + 16,
            paddingRight: 16,
            paddingBottom: 16,
        }),
        [collapsed]
    );

    // ----- STATE -----
    const [peerIdInput, setPeerIdInput] = useState(""); // mở chat theo id thủ công
    const [convId, setConvId] = useState(null);
    const [messages, setMessages] = useState([]);
    const [text, setText] = useState("");

    // Danh sách conv (đang tham gia)
    const [convs, setConvs] = useState([]);
    const [activeConv, setActiveConv] = useState(null);

    // Danh sách "người phù hợp môn" (students+teachers cùng subject)
    const [suggestedUsers, setSuggestedUsers] = useState([]);
    const [loadingUsers, setLoadingUsers] = useState(false);
    const bottomRef = useRef(null);
    const [currentUid, setCurrentUid] = useState(null);
    const currentUser = useContext(MyUserContext);
    const [activePeerId, setActivePeerId] = useState(null);
    const [activePeerProfile, setActivePeerProfile] = useState(null); // { id, name, mail, avatar }


    useEffect(() => {
        const unsub = onAuthStateChanged(auth, (user) => {
            setCurrentUid(user ? String(user.uid) : null);
        });
        return () => unsub();
    }, []);


    useEffect(() => {
        // Cần đợi đăng nhập Firebase xong để có currentUid
        if (!currentUid) {
            setSuggestedUsers([]);
            return
        }
        const load = async () => {
            setLoadingUsers(true);
            try {
                const api = authApis();

                // 1) Lấy toàn bộ students + teachers (coi là danh sách)
                const [rsStudents, rsTeachers] = await Promise.all([
                    api.get(endpoints.students),
                    api.get(endpoints.teachers),
                ]);

                const listStudents = Array.isArray(rsStudents?.data) ? rsStudents.data : (rsStudents?.data ? [rsStudents.data] : []);
                const listTeachers = Array.isArray(rsTeachers?.data) ? rsTeachers.data : (rsTeachers?.data ? [rsTeachers.data] : []);
                const everyoneRaw = [...listStudents, ...listTeachers];

                // 2) Chuẩn hoá
                const everyone = everyoneRaw.map(simplifyUser);

                // 3) Tìm “mình” theo userId == currrentUid
                const currentUidStr = String(currentUid);
                const me = everyone.find(u => u.id === currentUidStr);
                if (!me) {
                    // Nếu không khớp, nhiều khả năng bạn chưa map Firebase UID = userId backend.
                    // Trong trường hợp này, bạn có thể tạm gỡ chặn để test:
                    // setSuggestedUsers(everyone.filter(u => u.id !== currentUidStr));
                    setSuggestedUsers([]);
                    setLoadingUsers(false);
                    console.warn("Không tìm thấy profile hiện tại trong students/teachers theo userId =", currentUidStr);
                    return;
                }

                // 4) Lọc người có giao môn với mình & khác chính mình
                const filtered = everyone
                    .filter(u => u.id !== me.id && hasSubjectOverlap(me.subjectIds, u.subjectIds))
                    .sort((a, b) => (a.name).localeCompare(b.name));

                setSuggestedUsers(filtered);
            } catch (err) {
                console.error("Tải danh sách users thất bại:", err);
                setSuggestedUsers([]);
            } finally {
                setLoadingUsers(false);
            }
        };

        load();
    }, [currentUid]);
    async function fetchPeerProfileById(peerId) {
        try {
            const api = authApis();
            const [rsStu, rsTea] = await Promise.allSettled([
                api.get(endpoints.students),
                api.get(endpoints.teachers),
            ]);
            const asList = (res) =>
                res.status === "fulfilled"
                    ? (Array.isArray(res.value?.data) ? res.value.data : (res.value?.data ? [res.value.data] : []))
                    : [];
            const everyone = [...asList(rsStu), ...asList(rsTea)].map(simplifyUser);
            return everyone.find(u => u.id === String(peerId)) || null;
        } catch (e) {
            console.error("fetchPeerProfileById failed:", e);
            return null;
        }
    }

    useEffect(() => {
        let alive = true;
        (async () => {
            if (!activePeerId) { setActivePeerProfile(null); return; }
            const prof = await fetchPeerProfileById(activePeerId);
            if (alive) setActivePeerProfile(prof);
        })();
        return () => { alive = false; };
    }, [activePeerId]);


    // =============== CONVERSATIONS (đang tham gia) ===============
    useEffect(() => {
        if (!currentUid) return;
        const q = query(
            collection(db, "conversations"),
            where("memberIds", "array-contains", currentUid),
            orderBy("lastMessageAt", "desc"),
            limit(50)
        );
        const unsub = onSnapshot(q, (snap) => {
            const rows = snap.docs.map((d) => ({ id: d.id, ...d.data() }));
            setConvs(rows);
            if (!activeConv && rows.length > 0) {
                setActiveConv(rows[0].id);
                setConvId(rows[0].id);
                const p = getPeerIdFromConv(rows[0].memberIds);
                setActivePeerId(p || null);
                markConversationRead(rows[0].id, currentUid).catch(() => { });
            }

        });
        return () => unsub();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [currentUid]);

    // =============== MESSAGES (subscribe theo conv) ===============
    useEffect(() => {
        if (!convId) return;
        const q = query(
            collection(db, "conversations", convId, "messages"),
            orderBy("createdAt", "asc"),
            limit(500)
        );
        const unsub = onSnapshot(q, (snap) => {
            const rows = snap.docs.map((d) => ({ id: d.id, ...d.data() }));
            setMessages(rows);
            setTimeout(
                () => bottomRef.current?.scrollIntoView({ behavior: "smooth" }),
                50
            );
        });
        return () => unsub();
    }, [convId]);

    // =============== HANDLERS ===============
    async function handleOpenChatWith(peerUserId) {
        try {
            if (!currentUid) {
                alert("Bạn chưa đăng nhập Firebase (custom token).");
                return;
            }
            const peer = String(peerUserId);
            if (!peer || peer === currentUid) return;
            const id = await ensureDirectConversation(currentUid, peer);
            setActiveConv(id);
            setConvId(id);
            setActivePeerId(peer || null);
            await markConversationRead(id, currentUid);
        } catch (err) {
            console.error("Open chat failed:", err);
            alert("Không mở được cuộc trò chuyện.");
        }
    }

    async function handleOpenChatManual() {
        await handleOpenChatWith(peerIdInput);
    }

    async function handleSend() {
        try {
            if (!convId || !text.trim()) return;
            const body = text.trim();

            await addDoc(collection(db, "conversations", convId, "messages"), {
                senderId: currentUid,
                text: body,
                createdAt: serverTimestamp(),

            });
            await setDoc(
                doc(db, "conversations", convId),
                { lastMessageAt: serverTimestamp(), lastMessageText: body, lastMessageBy: currentUid },
                { merge: true }
            );
            setText("");
            setTimeout(
                () => bottomRef.current?.scrollIntoView({ behavior: "smooth" }),
                50
            );
        } catch (err) {
            console.error("Send failed:", err);
            alert("Gửi tin nhắn thất bại.");
        }
    }

    // Lấy peerId từ memberIds để hiển thị ở danh sách conv
    const getPeerIdFromConv = (memberIds = []) => {
        const a = String(memberIds?.[0] ?? "");
        const b = String(memberIds?.[1] ?? "");
        if (a === currentUid) return b;
        if (b === currentUid) return a;
        return memberIds?.find((m) => String(m) !== String(currentUid)) || "";
    };


    return (
        <div className="msg-page" style={pageStyle}>
            {/* ============ SIDEBAR ============ */}
            <aside className="msg-sidebar">
                <h3 className="msg-sidebar-title">Direct Messages</h3>
                {/* Danh sách conversations (đã từng chat) */}
                <div className="msg-conversation-list" style={{ marginTop: 8 }}>
                    {convs.map((c) => {
                        const active = c.id === activeConv;
                        const peer = getPeerIdFromConv(c.memberIds);
                        return (
                            <div
                                key={c.id}
                                className={`msg-conversation-item ${active ? "active" : ""}`}
                                onClick={async () => {
                                    setActiveConv(c.id);
                                    setConvId(c.id);
                                    setActivePeerId(getPeerIdFromConv(c.memberIds) || null);
                                    await markConversationRead(c.id, currentUid);
                                }}
                            >
                                {/* Hiển thị tin nhắn chưa đọc */}
                                <div className="msg-conv-title">
                                    {peer ? `User ${peer}` : c.id}
                                    {c.lastMessageAt &&
                                        c.lastMessageBy !== currentUid &&
                                        (!c.lastReadAt || !c.lastReadAt?.[currentUid] ||
                                            c.lastMessageAt.toMillis() > c.lastReadAt[currentUid]?.toMillis?.()) && (
                                            <span className="msg-unread-dot" />)}
                                </div>
                                <div className="msg-conv-sub">{c.lastMessageText || "(chưa có tin nhắn)"}</div>
                                <div className="msg-conv-time">{c.lastMessageAt ? fmtTime(c.lastMessageAt) : ""}</div>
                            </div>
                        );
                    })}
                </div>

                {/* Danh sách gợi ý theo subject (students + teachers có môn trùng) */}
                <h4 className="msg-sidebar-title" style={{ marginTop: 12 }}>Người cùng môn</h4>
                <div className="msg-conversation-list">
                    {loadingUsers && <div className="msg-conv-sub">Đang tải...</div>}
                    {!loadingUsers && suggestedUsers.length === 0 && (
                        <div className="msg-conv-sub">(Không tìm thấy)</div>
                    )}
                    {!loadingUsers && suggestedUsers.map(u => (
                        <div
                            key={u.id}
                            className="msg-conversation-item"
                            onClick={() => handleOpenChatWith(u.id)}
                        >
                            <div className="msg-conv-title">{u.name || `User #${u.id}`}</div>
                        </div>
                    ))}
                </div>
            </aside>

            {/* ============ KHUNG CHAT ============ */}
            <main className="msg-chat-main">
                <header className="msg-chat-header">
                    <div className="msg-chat-title">
                        {convId ? `Chat: ${convId}` : "Chưa chọn cuộc trò chuyện"}
                    </div>
                </header>

                <section className="msg-chat-body">
                    {!convId ? (
                        <div className="msg-chat-empty">
                            Chọn một cuộc trò chuyện ở bên trái hoặc bấm vào người “cùng môn” để mở.
                        </div>
                    ) : (
                        <div className="msg-messages">
                            {messages.map((m) => {
                                const mine = String(m.senderId) === String(currentUid);
                                const avatarUrl = mine
                                    ? (currentUser?.avatar || "/asset/img/humanStudying.png")
                                    : (activePeerProfile?.avatar || "/asset/img/humanStudying.png");

                                return (
                                    <div key={m.id} className={`msg-row ${mine ? "mine" : "other"}`}>
                                        {!mine && (
                                            <img src={avatarUrl} alt="avatar" className="msg-avatar" />
                                        )}
                                        <div className="msg-bubble">
                                            <div className="msg-text">{m.text}</div>
                                            {m.createdAt && (
                                                <div className="msg-time">{fmtTime(m.createdAt)}</div>
                                            )}
                                        </div>
                                        {mine && (
                                            <img src={avatarUrl} alt="avatar" className="msg-avatar" />
                                        )}
                                    </div>
                                );
                            })}
                            <div ref={bottomRef} />
                        </div>
                    )}
                </section>

                <footer className="msg-chat-input-bar">
                    <input
                        className="msg-chat-input"
                        placeholder={convId ? "Nhập tin nhắn..." : "Chọn cuộc trò chuyện trước"}
                        value={text}
                        disabled={!convId}
                        onChange={(e) => setText(e.target.value)}
                        onKeyDown={(e) => e.key === "Enter" && handleSend()}
                    />
                    <button
                        className="msg-btn-send"
                        disabled={!convId || !text.trim()}
                        onClick={handleSend}
                    >
                        Gửi
                    </button>
                </footer>
            </main>
        </div>
    );
};

export default Message;
