import  { useEffect, useState } from "react";
import { api } from "../api";
import Loading from "../components/Loading";
import PageHeader from "../components/PageHeader";
import Select from "../components/Select";
import Button from "../components/Button";
import Card from "../components/Card";

/**
 * Displays the course schedule for a given semester.
 * 
 * The page displays the following information:
 * - A dropdown to select the semester
 * - A button to reload the course schedule
 * - A table containing the course schedule for the selected semester
 * 
 * The table contains the following columns:
 * - Course Code
 * - Course Name
 * - Teacher
 * - Email
 * - Room
 * - Schedule
 * - Enrolled and Available Students
 */
export default function CourseSchedulePage() {
  const [semesterId, setSemesterId] = useState<number | null>(null);
  const [semesters, setSemesters] = useState<{ id: number; label: string }[]>(
    []
  );
  const [loading, setLoading] = useState(false);
  const [courses, setCourses] = useState<any>({});

  useEffect(() => {
    api
      .generateSemester()
      .then((res) => {
        const sems = res.data.result ?? [];
        setSemesters(sems);

        if (sems.length > 0) setSemesterId(sems[0].id);
      })
      .catch(() => alert("Failed to load semesters"));
  }, []);

  /**
   * Loads the course schedules for a given semester.
   * 
   * @return {Promise<void>} a promise that resolves when the course schedules are loaded
   */
  const loadData = async () => {
    if (!semesterId) return;

    setLoading(true);
    try {
      const res = await api.getCourseSchedules(semesterId);
      const courseData = res.data?.result?.courses ?? {};
      setCourses(courseData);
    } catch {
      alert("Failed to load course schedules");
    }
    setLoading(false);
  };

  useEffect(() => {
    if (semesterId) loadData();
  }, [semesterId]);

  return (
    <div>
      <PageHeader
        title="Course Schedule"
        right={
          <div className="d-flex align-items-center">
            <Select
              options={semesters.map((s) => ({ value: s.id, label: s.label }))}
              value={semesterId ?? ""}
              onChange={(v) => setSemesterId(v === "" ? null : Number(v))}
              placeholder="Select Semester"
              className="me-2"
            />

            <Button variant="secondary" size="md" onClick={loadData}>
              Reload
            </Button>
          </div>
        }
      />

      {loading && <Loading />}

      {!loading && Object.keys(courses).length === 0 && (
        <Card>
          <div className="text-center text-muted">
            No course schedule available for this semester.
          </div>
        </Card>
      )}

      {!loading && Object.keys(courses).length > 0 && (
        <div>
          {Object.entries(courses).map(
            ([courseCode, sections]: any, idx: number) => (
              <Card key={idx} className="mb-4">
                <div className="p-3 border-bottom">
                  <strong className="h5">{courseCode}</strong>
                </div>

                <div className="p-3">
                  {(sections as any[]).map((sec, i) => (
                    <div key={i} className="border rounded p-3 mb-3 bg-light">
                      <div className="row">
                        {/* LEFT COLUMN */}
                        <div className="col-md-6 mb-3">
                          <div>
                            <strong>Course:</strong> {sec.course}
                          </div>
                          <div>
                            <strong>Teacher:</strong> {sec.teacher}
                          </div>
                          <div>
                            <strong>Email:</strong> {sec.email}
                          </div>
                          <div>
                            <strong>Room:</strong> {sec.room}
                          </div>
                        </div>

                        {/* RIGHT COLUMN */}
                        <div className="col-md-6">
                          <strong>Schedule:</strong>
                          <ul className="mt-1">
                            {sec.schedule.map((s: string, idx2: number) => (
                              <li key={idx2}>{s}</li>
                            ))}
                          </ul>

                          <div className="mt-3">
                            <span
                              className="px-3 py-2 rounded d-inline-block"
                              style={{
                                backgroundColor: "#cfe2ff",
                                color: "#084298",
                                fontWeight: "bold",
                                border: "1px solid #b6d4fe",
                              }}
                            >
                              {sec.students}
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </Card>
            )
          )}
        </div>
      )}
    </div>
  );
}
