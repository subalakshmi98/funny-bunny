import { useEffect, useState } from "react";
import { api } from "../api";
import Loading from "../components/Loading";
import VerticalTimetable from "../components/VerticalTimetable";
import PageHeader from "../components/PageHeader";
import Select from "../components/Select";
import Button from "../components/Button";
import Card from "../components/Card";
import { getUser } from "../utils/auth";

/**
 * Displays the schedule for a given teacher.
 * 
 * If the user is an admin, a dropdown to select the teacher is displayed.
 * If the user is a teacher, their own schedule is displayed by default.
 * 
 * The page displays the following information:
 * - The teacher's name, email, and ID
 * - The teacher's weekly teaching schedule
 */
export default function TeacherSchedulePage() {
  const user = getUser();
  const isAdmin = user?.role === "ADMIN";
  const isTeacher = user?.role === "TEACHER";

  const [teacherId, setTeacherId] = useState<number | undefined>(
    isTeacher ? user.userId : undefined
  );

  const [teachers, setTeachers] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [teacher, setTeacher] = useState<any | null>(null);
  const [schedule, setSchedule] = useState<any[]>([]);

  useEffect(() => {
    api
      .getTeachers()
      .then((res) => {
        const list = res.data.result ?? [];
        setTeachers(list);

        if (isAdmin && list.length > 0) {
          setTeacherId(list[0].id);
        }
      })
      .catch(() => alert("Failed to load teachers"));
  }, []);

    const loadTeacherSchedule = async () => {
    if (!teacherId) {
      alert("Please select a teacher.");
      return;
    }

    setLoading(true);
    try {
      const res = await api.getTeacherSchedule(teacherId);
      const result = res.data.result;

      setTeacher(result.teacher);
      setSchedule(result.schedule ?? []);
    } catch (err) {
      alert("Failed to load teacher schedule");
      setTeacher(null);
      setSchedule([]);
    }
    setLoading(false);
  };

  useEffect(() => {
    if (isTeacher && teacherId) {
      loadTeacherSchedule();
    }
  }, [isTeacher, teacherId]);

  return (
    <div>
      <PageHeader title="Teacher Schedule" />

      {/* Admin controls */}
      {isAdmin && (
        <div className="d-flex align-items-stretch mb-3 gap-2">
          <Select
            options={teachers.map((t) => ({
              value: t.id,
              label: t.email,
            }))}
            value={teacherId ?? ""}
            onChange={(v) => setTeacherId(v === "" ? undefined : Number(v))}
            placeholder="Select Teacher"
          />

          <Button variant="primary" size="md" onClick={loadTeacherSchedule}>
            Load
          </Button>
        </div>
      )}

      {loading && <Loading />}

      {/* Teacher info */}
      {teacher && !loading && (
        <Card className="mb-3">
          <div>
            <div>
              <strong>Teacher:</strong> {teacher.name}
            </div>
            <div className="text-muted small">
              <strong>Email:</strong> {teacher.email}
            </div>
            <div className="text-muted small">
              <strong>ID:</strong> {teacher.id}
            </div>
          </div>
        </Card>
      )}

      {/* Schedule */}
      {!loading && schedule.length > 0 && (
        <Card title="Weekly Teaching Schedule">
          <VerticalTimetable sections={schedule} />
        </Card>
      )}

      {!loading && schedule.length === 0 && teacher && (
        <p className="text-muted">No classes assigned.</p>
      )}
    </div>
  );
}
