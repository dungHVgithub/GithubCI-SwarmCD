// src/components/student/StudyPlans.js
import React, { useContext, useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
import { SidebarContext } from "../reducers/SidebarContext";
import { authApis, endpoints } from "../configs/Apis";
import AddSchedule from "./AddSchedule";
import "../static/studyPlans.css";
import { showSuccess, showWarning, showError } from '../utils/toast'

const HEADER_HEIGHT = 80;
const SIDEBAR_W_OPEN = 220;
const SIDEBAR_W_COLLAPSED = 60;

// ========= Helpers chuyển DTO -> Date =========
const fromDateTuple = (t) => {
    if (!Array.isArray(t) || t.length < 3) return null; // [yyyy, mm, dd]
    const [y, m, d] = t;
    return new Date(y, (m ?? 1) - 1, d ?? 1);
};
const fromTimeTuple = (t) => {
    if (!Array.isArray(t) || t.length < 2) return [0, 0, 0]; // [HH, mm, ss?]
    const [h, m, s] = t;
    return [h ?? 0, m ?? 0, s ?? 0];
};
const toDateSafe = (dateField, timeField) => {
    if (Array.isArray(dateField)) {
        const base = fromDateTuple(dateField);
        const [h, m, s] = Array.isArray(timeField) ? fromTimeTuple(timeField) : [0, 0, 0];
        if (!base) return null;
        base.setHours(h, m, s || 0, 0);
        return base;
    }
    if (typeof dateField === "string") {
        const timeStr =
            typeof timeField === "string"
                ? timeField
                : Array.isArray(timeField)
                    ? `${String(timeField[0] ?? 0).padStart(2, "0")}:${String(timeField[1] ?? 0).padStart(2, "0")}:${String(timeField[2] ?? 0).padStart(2, "0")}`
                    : "00:00:00";
        return new Date(`${dateField}T${timeStr}`);
    }
    return null;
};

// ========= Helpers format khi PUT/POST =========
const pad = (n) => String(n).padStart(2, "0");
const toISODate = (d) => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
const toTime = (d) => `${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`;
const toISODateOnly = (d) => d.toISOString().slice(0, 10);

const StudyPlans = () => {
    const { studentId } = useParams(); // /studyPlans/:studentId
    const { collapsed } = useContext(SidebarContext) || { collapsed: false };

    // Giữ padding động theo trạng thái sidebar (phần này khó đẩy ra css thuần)
    const contentStyle = useMemo(
        () => ({
            paddingTop: HEADER_HEIGHT + 16,
            paddingLeft: (collapsed ? SIDEBAR_W_COLLAPSED : SIDEBAR_W_OPEN) + 16,
            paddingRight: 16,
            paddingBottom: 16,
        }),
        [collapsed]
    );

    const calendarHeight = `calc(100vh - ${HEADER_HEIGHT + 32}px)`;
    const api = authApis();

    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(false);
    const [range, setRange] = useState({ from: null, to: null });
    const [showAdd, setShowAdd] = useState(false);
    const [selectedIds, setSelectedIds] = useState(new Set());

    // ========== Load schedules theo from/to ==========
    const loadSchedules = async (sid, from, to) => {
        if (!sid || !from || !to) return;
        setLoading(true);
        try {
            const { data } = await api.get(endpoints.schedulesByStudent(sid), {
                params: { from, to }, // theo API hiện tại
            });
            const mapped = (data || []).map((it) => {
                const start = toDateSafe(it.studyDate, it.startTime);
                const end = toDateSafe(it.studyDate, it.endTime) || start;
                return {
                    id: String(it.id),
                    title: it.subject?.title || `Môn ${it.subject?.id ?? ""}`,
                    start,
                    end,
                    extendedProps: { subjectId: it.subject?.id, note: it.note },
                };
            });
            setEvents(mapped);
            setSelectedIds(new Set()); // reset lựa chọn khi reload
        } catch (e) {
            console.error("Load schedules failed", e);
            setEvents([]);
            setSelectedIds(new Set());
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (!studentId || !range.from || !range.to) return;
        loadSchedules(studentId, range.from, range.to);
    }, [studentId, range.from, range.to]);

    // ========== PUT update khi drag/resize ==========
    const putUpdateSchedule = async ({ event, oldEventState }) => {
        const id = event.id;
        const subjectId = event.extendedProps?.subjectId;
        const note = event.extendedProps?.note ?? "";
        const studyDate = toISODate(event.start);
        const startTime = toTime(event.start);
        const endTime = toTime(event.end || new Date(event.start.getTime() + 3600000));
        const body = { subjectId, studyDate, startTime, endTime, note };
        const path = `/schedules/student/${studentId}/${id}`;

        try {
            await api.put(path, body);
        } catch (e) {
            if (oldEventState) {
                event.setStart(oldEventState.start);
                event.setEnd(oldEventState.end);
            }
            console.error("Update schedule failed", e);
            showError("Cập nhật lịch thất bại.");
        }
    };

    const handleEventChange = async (info) => {
        const oldEventState = { start: info.oldEvent.start, end: info.oldEvent.end };
        await putUpdateSchedule({ event: info.event, oldEventState });
    };

    const fmtHM = (d) => {
        if (!(d instanceof Date)) return "";
        const hh = String(d.getHours()).padStart(2, "0");
        const mm = String(d.getMinutes()).padStart(2, "0");
        return `${hh}:${mm}`;
    };

    // === Khi tạo mới từ AddSchedule thành công ===
    const handleCreated = (data) => {
        const start = toDateSafe(data.studyDate, data.startTime);
        const end = toDateSafe(data.studyDate, data.endTime) || start;
        const startStr = start ? toISODateOnly(start) : null;

        // Nếu BE chưa gắn subject.title trong response -> reload để có tên môn
        if (!data?.subject?.title) {
            if (range.from && range.to) {
                loadSchedules(studentId, range.from, range.to);
            }
            return;
        }

        const added = {
            id: String(data.id),
            title: data.subject?.title || `Môn ${data.subject?.id ?? ""}`,
            start,
            end,
            extendedProps: { subjectId: data.subject?.id, note: data.note },
        };

        if (start && range.from && range.to && startStr >= range.from && startStr < range.to) {
            setEvents((prev) => [...prev, added]);
        } else {
            loadSchedules(studentId, range.from, range.to);
        }
    };

    // ========== Render event: title + note (style bằng CSS) ==========
    const renderEventContent = (arg) => {
        const start = arg.event.start;
        const end =
            arg.event.end || new Date(arg.event.start.getTime() + 60 * 60 * 1000);
        const timeText = `${fmtHM(start)} - ${fmtHM(end)}`;
        const title = arg.event.title || "";
        const note = arg.event.extendedProps?.note;

        return (
            <div className="sp-event sp-event--stack">
                <div className="sp-event__time">{timeText}</div>
                <div className="sp-event__title">{title}</div>
                {note ? <div className="sp-event__note">{note}</div> : null}
            </div>
        );
    };
    // Toggle chọn event khi click
    const onEventClick = (clickInfo) => {
        const id = String(clickInfo.event.id);
        setSelectedIds((prev) => {
            const next = new Set(prev);
            if (next.has(id)) next.delete(id);
            else next.add(id);
            clickInfo.event.setProp("classNames", [
                ...(clickInfo.event.classNames || []),
                next.has(id) ? "is-selected" : "",
            ]);
            if (!next.has(id)) {
                const filtered = (clickInfo.event.classNames || []).filter((c) => c !== "is-selected");
                clickInfo.event.setProp("classNames", filtered);
            }
            return next;
        });
    };

    // Áp lớp CSS "is-selected" khi render
    const eventClassNames = (arg) => (selectedIds.has(String(arg.event.id)) ? ["is-selected"] : []);

    // Xóa nhiều event đã chọn
    const deleteSelectedSchedules = async () => {
        if (selectedIds.size === 0) return;
        try {
            await Promise.all(Array.from(selectedIds).map((id) => api.delete(`/schedules/${id}`)));
            setEvents((prev) => prev.filter((ev) => !selectedIds.has(String(ev.id))));
            setSelectedIds(new Set());
            showSuccess("Xóa lịch thành công");
        } catch (e) {
            console.error("Delete schedules failed", e);
            showError("Xóa lịch thất bại");
        }
    };

    return (
        <div className="studyplans-page" style={contentStyle}>
            <div className="sp-page-title sp-toolbar">
                <div>
                    <h3>Kế hoạch học tập</h3>
                    <small>Click để chọn nhiều lịch, kéo/giãn để cập nhật giờ</small>
                </div>

                <button
                    className="sp-btn sp-btn--danger"
                    onClick={deleteSelectedSchedules}
                    disabled={loading || selectedIds.size === 0}
                    title="Xóa các lịch đã chọn"
                    style={{ marginLeft: "auto" }}
                >
                    Xóa
                </button>

                <button
                    className="sp-btn sp-btn--primary"
                    onClick={() => setShowAdd(true)}
                    disabled={loading || !studentId}
                >
                    + Tạo lịch học
                </button>
            </div>

            <div className="sp-calendar-wrapper">
                <FullCalendar
                    plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
                    initialView="timeGridWeek"
                    headerToolbar={{
                        left: "prev,next today",
                        center: "title",
                        right: "dayGridMonth,timeGridWeek,timeGridDay",
                    }}
                    slotMinTime="06:00:00"
                    slotMaxTime="21:00:00"
                    allDaySlot={false}
                    height={calendarHeight}
                    expandRows={true}
                    editable={true}
                    droppable={true}
                    eventDurationEditable={true}
                    events={events}
                    eventContent={renderEventContent}
                    eventClassNames={eventClassNames}
                    datesSet={(arg) => {
                        const from = toISODateOnly(arg.start);
                        const to = toISODateOnly(arg.end); // end exclusive
                        setRange({ from, to });
                    }}
                    eventClick={onEventClick}
                    eventDrop={(info) => {
                        setEvents((prev) =>
                            prev.map((ev) =>
                                ev.id === info.event.id
                                    ? {
                                        ...ev,
                                        start: info.event.start,
                                        end:
                                            info.event.end || new Date(info.event.start.getTime() + 3600000),
                                    }
                                    : ev
                            )
                        );
                        setSelectedIds((prev) => {
                            const next = new Set(prev);
                            next.delete(String(info.event.id));
                            return next;
                        });
                        handleEventChange(info);
                    }}
                    eventResize={(info) => {
                        setEvents((prev) =>
                            prev.map((ev) =>
                                ev.id === info.event.id
                                    ? { ...ev, start: info.event.start, end: info.event.end }
                                    : ev
                            )
                        );
                        setSelectedIds((prev) => {
                            const next = new Set(prev);
                            next.delete(String(info.event.id));
                            return next;
                        });
                        handleEventChange(info);
                    }}
                    nowIndicator={true}
                    dayMaxEventRows={3}
                    firstDay={1}
                    slotEventOverlap={true}
                    stickyHeaderDates={true}
                    locale="vi"
                />
            </div>

            {showAdd && (
                <AddSchedule
                    studentId={studentId}
                    onClose={() => setShowAdd(false)}
                    onCreated={handleCreated}
                />
            )}
        </div>
    );
};

export default StudyPlans;
