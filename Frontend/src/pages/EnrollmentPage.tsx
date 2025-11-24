import { useState, useEffect } from "react";
import { api } from "../api";
import Loading from "../components/Loading";
import PageHeader from "../components/PageHeader";
import Input from "../components/Input";
import Select from "../components/Select";
import Button from "../components/Button";
import Card from "../components/Card";
import { getUser } from "../utils/auth";

/**
 * EnrollmentPage
 *
 * This page allows administrators to enroll students in courses.
 * Students can view their own eligible courses and enroll in them.
 *
 * @returns {JSX.Element} The EnrollmentPage component.
 */
export default function EnrollmentPage() {
  const user = getUser();
  const isAdmin = user?.role === "ADMIN";
  const isStudent = user?.role === "STUDENT";

  if (!user || (!isAdmin && !isStudent)) return <div>Not allowed</div>;

  const [studentId, setStudentId] = useState<number | undefined>(
    isStudent ? user?.userId : undefined
  );

  const [semesterId, setSemesterId] = useState<number | undefined>(undefined);
  const [semesters, setSemesters] = useState<{ id: number; label: string }[]>(
    []
  );

  const [loading, setLoading] = useState(false);
  const [eligible, setEligible] = useState<any[]>([]);
  const [message, setMessage] = useState("");

  useEffect(() => {
    api
      .generateSemester()
      .then((res) => {
        const arr = res.data.result ?? [];
        setSemesters(arr);
        if (arr.length > 0) setSemesterId(arr[0].id);
      })
      .catch(() => alert("Failed to load semesters"));
  }, []);

  /**
   * Fetches eligible sections for a student and semester.
   * 
   * @returns {Promise<void>} A promise that resolves when the eligible sections are fetched.
   */
  const handleFetchEligible = async () => {
    if (!studentId) return alert("Enter Student ID");
    if (!semesterId) return alert("Select a semester");

    setLoading(true);
    setMessage("");

    try {
      const res = await api.getEligible(studentId, semesterId);
      const result = res.data.result;

      setEligible(result.sections ?? []);
      setMessage(result.message ?? "");
    } catch {
      setMessage("Failed to load eligible sections.");
    }

    setLoading(false);
  };

  /**
   * Enrolls a student in a course.
   * 
   * @param {number} courseId the course's id
   * @returns {Promise<void>} A promise that resolves when the student is enrolled
   */
  const handleEnroll = async (courseId: number) => {
    if (!studentId || !semesterId) return;

    setLoading(true);
    try {
      await api.enroll({ studentId, courseId, semesterId });
      alert("Enrolled successfully!");
      handleFetchEligible();
    } catch (err: any) {
      alert(err.response?.data?.message ?? "Enrollment failed.");
    }
    setLoading(false);
  };

  return (
    <div>
      <PageHeader title="Enroll Student" />

      {/* Controls */}
      <div className="d-flex align-items-stretch gap-2 mb-3">
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
          options={semesters.map((s) => ({ value: s.id, label: s.label }))}
          value={semesterId ?? ""}
          onChange={(v) => setSemesterId(v === "" ? undefined : Number(v))}
          placeholder="Select Semester"
        />

        <Button variant="primary" onClick={handleFetchEligible}>
          Load Eligible
        </Button>
      </div>

      {/* Info message */}
      {message && <div className="alert alert-info">{message}</div>}

      {loading && <Loading />}

      {/* Eligible Sections */}
      {!loading && eligible.length > 0 && (
        <div className="mt-3">
          {eligible.map((sec) => (
            <Card key={sec.sectionId}>
              <div className="d-flex justify-content-between align-items-start">
                <div>
                  <strong>{sec.course}</strong>
                  <div className="text-muted small">
                    Teacher: {sec.teacher} <br />
                    Room: {sec.room}
                  </div>
                </div>

                <Button
                  variant="success"
                  size="sm"
                  onClick={() => handleEnroll(sec.courseId)}
                >
                  Enroll
                </Button>
              </div>

              <div className="mt-2">
                {sec.schedule.map((t: string, i: number) => (
                  <div key={i} className="text-muted small">
                    {t}
                  </div>
                ))}
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
