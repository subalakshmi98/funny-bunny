import { useState, useEffect } from "react";
import { api } from "../api";
import Loading from "../components/Loading";
import PageHeader from "../components/PageHeader";
import Button from "../components/Button";
import Card from "../components/Card";
import { getUser } from "../utils/auth";

/**
 * Page for displaying academic progress.
 *
 * Only accessible by admins and students.
 * 
 * For admins, allows selecting a student ID to view their progress.
 * For students, automatically loads their own progress.
 *
 * Displays a table of the student's progress, including their credits earned, credits remaining, core courses passed, GPA, and predicted semesters left to graduate.
 */
export default function ProgressPage() {
  const user = getUser();
  const isAdmin = user?.role === "ADMIN";
  const isStudent = user?.role === "STUDENT";

  if (!user || (!isAdmin && !isStudent)) {
    return <div>Not allowed</div>;
  }

  const [studentId, setStudentId] = useState<number | undefined>(undefined);

  const [loadingProgress, setLoadingProgress] = useState(false);
  const [progressData, setProgressData] = useState<any | null>(null);

  const [loadingTranscript, setLoadingTranscript] = useState(false);
  const [transcript, setTranscript] = useState<any[] | null>(null);
  const [transcriptStudent, setTranscriptStudent] = useState<any | null>(null);

  const [showProgress, setShowProgress] = useState(false);
  const [showTranscript, setShowTranscript] = useState(false);

  useEffect(() => {
    if (isStudent) {
      setStudentId(user.userId);
    }
  }, [isStudent]);

  /**
   * Loads the academic progress of the student with the given ID.
   * 
   * Shows a loading indicator while the progress is being loaded.
   * If the progress is successfully loaded, sets the progress data to the response.
   * If there is an error loading the progress, alerts the user with a failure message.
   * Hides the transcript section and shows the progress section.
   */
  const loadProgress = async () => {
    if (!studentId) return;

    setShowProgress(true);
    setShowTranscript(false);

    setLoadingProgress(true);
    try {
      const res = await api.getProgress(studentId);
      setProgressData(res.data.result);
    } catch {
      alert("Failed to load progress");
    }
    setLoadingProgress(false);
  };

  /**
   * Loads the academic transcript of the student with the given ID.
   * 
   * Shows a loading indicator while the transcript is being loaded.
   * If the transcript is successfully loaded, sets the transcript data to the response.
   * If there is an error loading the transcript, alerts the user with a failure message.
   * Hides the progress section and shows the transcript section.
   */
  const loadTranscript = async () => {
    if (!studentId) return;

    setShowProgress(false);
    setShowTranscript(true);

    setLoadingTranscript(true);
    try {
      const res = await api.getTranscript(studentId);
      const s = res.data.result.student;

      setTranscriptStudent({
        id: s.id,
        name: s.name,
        email: s.email,
        grade: s.gradeLevel,
      });

      setTranscript(
        Array.isArray(res.data.result.transcript)
          ? res.data.result.transcript
          : []
      );
    } catch {
      alert("Failed to load transcript");
    }
    setLoadingTranscript(false);
  };

  return (
    <div>
      <PageHeader
        title="Academic Progress"
        right={
          <div className="d-flex align-items-center">
            {isAdmin && (
              <input
                type="number"
                className="form-control w-auto me-2"
                placeholder="Student ID"
                value={studentId ?? ""}
                onChange={(e) =>
                  setStudentId(
                    e.target.value === "" ? undefined : Number(e.target.value)
                  )
                }
              />
            )}

            <Button
              variant="primary"
              size="md"
              className="me-2"
              onClick={loadProgress}
            >
              Load Progress
            </Button>

            <Button variant="secondary" size="md" onClick={loadTranscript}>
              Load Transcript
            </Button>
          </div>
        }
      />

      {/* PROGRESS SECTION */}
      {showProgress && (
        <Card className="mt-3">
          <h4 className="mb-3">Progress</h4>

          {loadingProgress && <Loading />}

          {!loadingProgress && progressData && (
            <>
              {/* Student Info */}
              <Card className="p-3 mb-3 bg-light">
                <h5 className="mb-2">Student Info</h5>
                <div>
                  <strong>ID:</strong> {progressData.student.id}
                </div>
                <div>
                  <strong>Name:</strong> {progressData.student.name}
                </div>
                <div>
                  <strong>Email:</strong> {progressData.student.email}
                </div>
                <div>
                  <strong>Grade Level:</strong>{" "}
                  {progressData.student.gradeLevel}
                </div>
              </Card>

              {/* Summary */}
              <Card className="p-3 mb-3">
                <h5 className="mb-2">Progress Summary</h5>
                <div>
                  <strong>Credits:</strong>{" "}
                  {progressData.progress.creditsEarned} /{" "}
                  {progressData.progress.creditsRequired}
                </div>
                <div>
                  <strong>Credits Remaining:</strong>{" "}
                  {progressData.progress.creditsRemaining}
                </div>
                <div>
                  <strong>Core Passed:</strong>{" "}
                  {progressData.progress.corePassed} /{" "}
                  {progressData.progress.coreRequired}
                </div>
                <div>
                  <strong>GPA:</strong> {progressData.progress.gpa}
                </div>
                <div>
                  <strong>Predicted Semesters Left:</strong>{" "}
                  {progressData.progress.predictedSemestersToGraduate}
                </div>
              </Card>

              {/* Remaining Core Courses */}
              <h5 className="mt-3">Remaining Core Courses</h5>

              {progressData.remainingCoreCourses?.length > 0 ? (
                <Card className="p-3 mb-3">
                  {progressData.remainingCoreCourses.map((c: any) => (
                    <div key={c.courseId} className="mb-2">
                      <strong>{c.code}</strong> — {c.name}{" "}
                      <span className="text-muted">
                        (Semester {c.semesterOrder})
                      </span>
                    </div>
                  ))}
                </Card>
              ) : (
                <Card className="p-3 text-muted">
                  All core courses completed.
                </Card>
              )}
            </>
          )}
        </Card>
      )}

      {/* TRANSCRIPT SECTION */}
      {showTranscript && (
        <Card className="mt-3">
          <h4 className="mb-3">Transcript</h4>

          {loadingTranscript && <Loading />}

          {!loadingTranscript && transcriptStudent && (
            <Card className="p-3 mb-3 bg-light">
              <h5 className="mb-2">Student Info</h5>
              <div>
                <strong>Name:</strong> {transcriptStudent.name}
              </div>
              <div>
                <strong>Email:</strong> {transcriptStudent.email}
              </div>
              <div>
                <strong>ID:</strong> {transcriptStudent.id}
              </div>
              <div>
                <strong>Grade Level:</strong> {transcriptStudent.grade}
              </div>
            </Card>
          )}

          {!loadingTranscript && transcript && (
            <>
              {transcript.length === 0 ? (
                <Card className="p-3 text-muted">
                  No transcript records found.
                </Card>
              ) : (
                <Card className="p-3">
                  {transcript.map((t, idx) => (
                    <div key={idx} className="mb-3 border-bottom pb-2">
                      <strong>{t.courseCode}</strong> — {t.courseName}
                      <br />
                      <span className="text-muted">
                        Credits: {t.credits} | Semester: {t.semester} | Status:{" "}
                        {t.status}
                      </span>
                    </div>
                  ))}
                </Card>
              )}
            </>
          )}
        </Card>
      )}
    </div>
  );
}
