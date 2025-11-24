import React from "react";

interface Section {
  course: string;
  teacher: string;
  room: string;
  schedule: string[];
}

interface Props {
  sections: Section[];
}

const DAYS = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"];

const TIME_SLOTS = [
  { label: "9AM–11AM", key: "9-11" },
  { label: "11AM–12PM", key: "11-12" },
  { label: "12–1PM (LUNCH)", key: "12-1" },
  { label: "1PM–3PM", key: "13-15" },
  { label: "3PM–5PM", key: "15-17" },
];

/**
 * Parse a meeting string and return an object containing the day and time key.
 * 
 * The meeting string should be in the format "DAY TIME", where DAY is one of the days of the week
 * and TIME is one of the time slots defined in TIME_SLOTS.
 * 
 * @param {string} meeting the meeting string to parse
 * @returns {Object} an object containing the day and time key
 * @example
 * parseSchedule("MONDAY 9AM-11AM") // { day: "MONDAY", key: "9-11" }
 */
function parseSchedule(meeting: string) {
  const parts = meeting.split(" ");
  const day = parts[0];
  const time = parts[1];

  let key = "";

  if (time.includes("9AM-11AM")) key = "9-11";
  if (time.includes("11AM-12PM")) key = "11-12";
  if (time.includes("1PM-3PM")) key = "13-15";
  if (time.includes("3PM-5PM")) key = "15-17";

  return { day, key };
}

/**
 * A vertical timetable component for displaying course sections.
 * 
 * @param {Props} props - an object containing the sections to display
 * @returns {JSX.Element} a JSX element representing the vertical timetable
 * 
 * @example
 * <VerticalTimetable sections={sections} />
 */
export default function VerticalTimetable({ sections }: Props) {
  const grid: any = {};
  DAYS.forEach((d) => {
    grid[d] = {
      "9-11": null,
      "11-12": null,
      "12-1": "LUNCH",
      "13-15": null,
      "15-17": null,
    };
  });

  sections.forEach((sec) => {
    sec.schedule.forEach((meeting) => {
      const parsed = parseSchedule(meeting);
      if (parsed.key && parsed.day && parsed.key !== "12-1") {
        grid[parsed.day][parsed.key] = sec;
      }
    });
  });

  const cellStyle: React.CSSProperties = {
    height: "110px",
    padding: 0,
    verticalAlign: "middle",
  };

  return (
    <div className="table-responsive">
      <table
        className="table table-bordered table-fixed text-center shadow-sm"
        style={{
          borderRadius: 12,
          overflow: "hidden",
          tableLayout: "fixed",
          width: "100%",
        }}
      >
        <thead className="bg-primary text-white">
          <tr>
            <th style={{ width: "120px", ...cellStyle }}>Day</th>
            {TIME_SLOTS.map((slot) => (
              <th key={slot.key} style={cellStyle}>
                {slot.label}
              </th>
            ))}
          </tr>
        </thead>

        <tbody>
          {DAYS.map((day) => (
            <tr key={day}>
              {/* DAY NAME */}
              <th
                className="bg-light fw-semibold"
                style={{ width: "120px", fontSize: "0.9rem", ...cellStyle }}
              >
                {day}
              </th>

              {TIME_SLOTS.map((slot) => {
                const cell = grid[day][slot.key];

                // LUNCH CELL (fixed-size)
                if (slot.key === "12-1") {
                  return (
                    <td
                      key={slot.key}
                      className="bg-warning bg-opacity-25 fw-bold"
                      style={cellStyle}
                    >
                      LUNCH
                    </td>
                  );
                }

                // EMPTY CELL
                if (!cell) {
                  return (
                    <td key={slot.key} style={cellStyle}>
                      <span className="text-muted small"></span>
                    </td>
                  );
                }

                // CLASS CELL
                return (
                  <td key={slot.key} style={cellStyle}>
                    <div
                      className="h-100 d-flex flex-column justify-content-center"
                      style={{
                        background: "#f8fafc",
                        borderRadius: 8,
                        border: "1px solid #e5e7eb",
                        margin: 6,
                        padding: "4px",
                      }}
                    >
                      <div className="fw-bold" style={{ fontSize: "0.9rem" }}>
                        {cell.course}
                      </div>
                      <div className="small">{cell.teacher}</div>
                      <div className="text-muted small">{cell.room}</div>
                    </div>
                  </td>
                );
              })}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
