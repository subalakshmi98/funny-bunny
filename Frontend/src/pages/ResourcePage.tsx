import { useEffect, useState } from "react";
import { api } from "../api";
import { Bar } from "react-chartjs-2";
import {
  Chart as ChartJS,
  ArcElement,
  CategoryScale,
  LinearScale,
  BarElement,
  Tooltip,
  Legend,
} from "chart.js";

import Loading from "../components/Loading";
import PageHeader from "../components/PageHeader";
import Button from "../components/Button";
import Card from "../components/Card";

ChartJS.register(
  ArcElement,
  CategoryScale,
  LinearScale,
  BarElement,
  Tooltip,
  Legend
);

/**
 * Page for displaying resource utilizationization data.
 *
 * This page displays a bar chart for each teacher's workload and a bar chart for each room's usage.
 */
export default function ResourcePage() {
  const [teachers, setTeachers] = useState<any[]>([]);
  const [rooms, setRooms] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);

  /**
   * Loads the teacher and room analytics data.
   * Sets the teachers and rooms states with the loaded data.
   * If there is an error, an alert is shown.
   * Sets the loading state to true while loading and false afterwards.
   */
  const loadData = async () => {
    setLoading(true);
    try {
      const t = await api.getTeachersAnalytics();
      const r = await api.getRoomsAnalytics();
      setTeachers(t.data.result);
      setRooms(r.data.result);
    } catch {
      alert("Failed loading analytics");
    }
    setLoading(false);
  };

  useEffect(() => {
    loadData();
  }, []);

/**
 * Exports the teacher and room analytics data to a CSV file.
 * 
 * The CSV file will have the following columns: Type, Name, Load, Utilization.
 * Type is either "Teacher" or "Room".
 * Name is the name of the teacher or room.
 * Load is the weekly load of the teacher or room.
 * Utilization is the utilizationization percentage of the teacher or room.
 */
  const exportCSV = () => {
    let csv = "Type,Name,Load,Utilization\n";

    teachers.forEach((t) => {
      csv += `Teacher,${t.teacherName},${t.weeklyHours},${t.utilizationPercent}%\n`;
    });

    rooms.forEach((r) => {
      csv += `Room,${r.roomName},${r.weeklyHoursUsed},${r.utilizationPercent}%\n`;
    });

    const blob = new Blob([csv], { type: "text/csv" });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = "resource-utilization.csv";
    link.click();
  };

  return (
    <div>
      {/* HEADER */}
      <PageHeader
        title="Resource Utilization"
        right={
          <Button variant="secondary" size="md" onClick={exportCSV}>
            Export CSV
          </Button>
        }
      />

      {loading && <Loading />}

      {!loading && (
        <>
          {/* Teacher Workload */}
          <Card title="Teacher Workload">
            <div className="d-flex justify-content-center p-2">
              <div style={{ width: "70%", height: "100%" }}>
                <Bar
                  data={{
                    labels: teachers.map((t) => t.teacherName),
                    datasets: [
                      {
                        label: "Weekly Hours",
                        data: teachers.map((t) => t.weeklyHours),
                        backgroundColor: "rgba(54,162,235,0.5)",
                      },
                    ],
                  }}
                  options={{
                    maintainAspectRatio: false,
                  }}
                />
              </div>
            </div>
          </Card>

          {/* Room Usage */}
          <Card title="Room Usage" className="mt-4">
            <div className="d-flex justify-content-center p-2">
              <div style={{ width: "70%", height: "100%" }}>
                <Bar
                  data={{
                    labels: rooms.map((r) => r.roomName),
                    datasets: [
                      {
                        label: "Weekly Hours Used",
                        data: rooms.map((r) => r.weeklyHoursUsed),
                        backgroundColor: "rgba(255,99,132,0.5)",
                      },
                    ],
                  }}
                  options={{
                    maintainAspectRatio: false,
                  }}
                />
              </div>
            </div>
          </Card>
        </>
      )}
    </div>
  );
}
