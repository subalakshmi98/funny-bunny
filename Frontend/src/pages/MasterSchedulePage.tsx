import { useEffect, useState } from "react";
import { api } from "../api";
import Loading from "../components/Loading";
import PageHeader from "../components/PageHeader";
import Select from "../components/Select";
import Button from "../components/Button";
import Card from "../components/Card";
import VerticalTimetable from "../components/VerticalTimetable";

/**
 * MasterSchedulePage is a React component that displays the master schedule for a given semester.
 * The component loads the semesters available for the current user and allows the user to select a semester.
 * Once a semester is selected, the component loads the existing schedule for the selected semester.
 * If no schedule exists for the selected semester, the component allows the user to generate a new master schedule.
 * The component displays the loaded schedule in a vertical timetable format.
 */
export default function MasterSchedulePage() {
  const [semesterId, setSemesterId] = useState<number | null>(null);
  const [semesters, setSemesters] = useState<{ id: number; label: string }[]>(
    []
  );
  const [schedule, setSchedule] = useState<any[]>([]);
  const [scheduleExists, setScheduleExists] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    let mounted = true;

    api
      .generateSemester()
      .then((res) => {
        const sems = res.data.result ?? [];

        if (!mounted) return;
        setSemesters(sems);

        if (sems.length > 0) {
          setSemesterId(sems[0].id);
        }
      })
      .catch(() => alert("Failed to load semesters"));

    return () => {
      mounted = false;
    };
  }, []);

  const loadSchedule = async (sid?: number | null) => {
    const useSid = sid ?? semesterId;
    if (!useSid) return;

    setLoading(true);

    try {
      const result = await api.getMasterSchedule(useSid);
      const sections = result.data.result?.sections ?? [];

      setSchedule(sections);
      setScheduleExists(sections.length > 0); 
    } catch {
      alert("Failed to load schedule");
      setSchedule([]);
      setScheduleExists(false);
    }

    setLoading(false);
  };

  /**
   * Generates a master schedule for the currently selected semester.
   * If a schedule already exists for the selected semester, the function does nothing.
   * If no semester is selected, the function prompts the user to select a semester.
   * The function sets the loading state to true and attempts to generate a master schedule.
   * If the generation fails, the function alerts the user with an error message.
   * After generation, the function auto-loads the new schedule.
   */
    const handleGenerate = async () => {
    if (scheduleExists) return; 

    if (!semesterId) return alert("Select a semester");

    setLoading(true);
    try {
      await api.generateMaster(semesterId);
      await loadSchedule(semesterId); 
    } catch {
      alert("Failed to generate master schedule");
    }
    setLoading(false);
  };

  useEffect(() => {
    if (semesterId) {
      loadSchedule(semesterId);
    }
  }, [semesterId]);

  return (
    <div>
      <PageHeader
        title="Master Schedule"
        right={
          <div className="d-flex align-items-center">
            <Select
              options={semesters.map((s) => ({ value: s.id, label: s.label }))}
              value={semesterId ?? ""}
              onChange={(v) => setSemesterId(v === "" ? null : Number(v))}
              placeholder="Select Semester"
              className="me-2"
            />

            <Button
              variant="primary"
              size="md"
              onClick={handleGenerate}
              className="me-2"
              disabled={scheduleExists} // <— disable when schedule exists
            >
              Generate
            </Button>

            <Button
              variant="secondary"
              size="md"
              onClick={() => loadSchedule()}
            >
              Reload
            </Button>
          </div>
        }
      />

      {loading && <Loading />}

      {!loading && schedule.length === 0 && (
        <Card>
          <div className="text-center text-muted">
            No schedule available for this semester.
          </div>
        </Card>
      )}

      {!loading && schedule.length > 0 && (
        <Card>
          <VerticalTimetable sections={schedule} />
        </Card>
      )}
    </div>
  );
}
