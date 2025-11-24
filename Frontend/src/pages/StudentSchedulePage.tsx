import { useState, useEffect } from "react";
import { api } from "../api";
import Loading from "../components/Loading";
import VerticalTimetable from "../components/VerticalTimetable";
import PageHeader from "../components/PageHeader";
import Input from "../components/Input";
import Select from "../components/Select";
import Button from "../components/Button";
import Card from "../components/Card";
import { getUser } from "../utils/auth";

/**
 * StudentSchedulePage is a React component that displays the course schedule for a given student.
 * The component takes in a student ID and semester ID as props.
 * The component loads the semesters available for the current user and allows the user to select a semester.
 * Once a semester is selected, the component loads the existing schedule for the selected semester.
 * If no schedule exists for the selected semester, the component allows the user to generate a new master schedule.
 * The component displays the loaded schedule in a vertical timetable format.
 */
export default function StudentSchedulePage() {
  const user = getUser();

  const isAdmin = user?.role === "ADMIN";
  const isStudent = user?.role === "STUDENT";

  if (!user || (!isAdmin && !isStudent)) {
    return <div>Not allowed</div>;
  }

  const [studentId, setStudentId] = useState<number | undefined>(
    isStudent ? user.userId : undefined
  );

  const [semesterId, setSemesterId] = useState<number | undefined>(undefined);
  const [semesters, setSemesters] = useState<{ id: number; label: string }[]>(
    []
  );

  const [loading, setLoading] = useState(false);
  const [schedule, setSchedule] = useState<any[]>([]);
  const [student, setStudent] = useState<any | null>(null);

  useEffect(() => {
    api
      .generateSemester()
      .then((res) => {
        const sems = res.data.result ?? [];
        setSemesters(sems);

        if (sems.length > 0) {
          setSemesterId(sems[0].id);
        }
      })
      .catch(() => alert("Failed to load semesters"));
  }, []);

  useEffect(() => {
    if (isStudent) {
      setStudentId(user.userId);
    }
  }, [isStudent, user]);

  /**
   * Loads the student's schedule for a given semester.
   * 
   * @throws {alert} if student ID is not entered (Admin only) or semester is not selected
   */
  const handleLoadSchedule = async () => {
    if (!studentId) {
      alert("Please enter a student ID (Admin only)");
      return;
    }
    if (!semesterId) {
      alert("Please select a semester");
      return;
    }

    setLoading(true);

    try {
      const res = await api.getStudentSchedule(studentId, semesterId);
      const result = res.data.result;

      const studentData = result.student;

      setStudent({
        id: studentData.id,
        name: studentData.name,
        email: studentData.email,
        gradeLevel: studentData["Grade level"],
      });

      setSchedule(result.sections ?? []);
    } catch (err) {
      console.error("getStudentSchedule error:", err);
      alert("Failed to load student schedule");
    }

    setLoading(false);
  };

  useEffect(() => {
    if (isStudent && studentId && semesterId) {
      handleLoadSchedule();
    }
  }, [isStudent, studentId, semesterId]);

  return (
    <div>
      <PageHeader title="Student Schedule" />

      {/* Controls - Modern UI */}
      <div className="d-flex align-items-stretch mb-3 gap-2">
        {isAdmin && (
          <Input
            type="number"
            placeholder="Student ID"
            value={studentId ?? ""}
            onChange={(e: any) =>
              setStudentId(
                e.target.value === "" ? undefined : Number(e.target.value)
              )
            }
          />
        )}

        <Select
          options={semesters.map((s) => ({
            value: s.id,
            label: s.label,
          }))}
          value={semesterId ?? ""}
          onChange={(v) => setSemesterId(v === "" ? undefined : Number(v))}
          placeholder="Select Semester"
        />

        {isAdmin && (
          <Button variant="primary" size="md" onClick={handleLoadSchedule}>
            Load Schedule
          </Button>
        )}
      </div>

      {loading && <Loading />}

      {/* Student Info */}
      {student && (
        <Card className="mb-3">
          <div>
            <strong>Student:</strong> {student.name} <br />
            <strong>Email:</strong> {student.email} <br />
            <strong>ID:</strong> {student.id} <br />
            <strong>Grade Level:</strong> {student.gradeLevel}
          </div>
        </Card>
      )}

      {/* Schedule */}
      {!loading && schedule.length > 0 && (
        <Card title="Weekly Schedule">
          <VerticalTimetable sections={schedule} />
        </Card>
      )}

      {!loading && schedule.length === 0 && student && (
        <p className="text-muted">No classes assigned.</p>
      )}
    </div>
  );
}
