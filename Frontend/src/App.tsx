import { Routes, Route } from "react-router-dom";
import MasterSchedulePage from "./pages/MasterSchedulePage";
import EnrollmentPage from "./pages/EnrollmentPage";
import StudentSchedulePage from "./pages/StudentSchedulePage";
import ProgressPage from "./pages/ProgressPage";
import CourseSchedulePage from "./pages/CourseSchedulePage";
import TeacherSchedulePage from "./pages/TeacherSchedulePage";
import ResourcePage from "./pages/ResourcePage";
import LoginPage from "./pages/LoginPage";
import Navbar from "./components/layout/Navbar";
import { getUser } from "./utils/auth";

export default function App() {
  const user = getUser();

  if (!user && window.location.pathname !== "/login") {
    window.location.href = "/login";
  }

  return (
    <div className="bg-light min-vh-100">
      {user && <Navbar />}

      <div className="container my-4">
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/master" element={<MasterSchedulePage />} />
          <Route path="/courses" element={<CourseSchedulePage />} />
          <Route path="/teachers" element={<TeacherSchedulePage />} />
          <Route path="/enroll" element={<EnrollmentPage />} />
          <Route path="/student" element={<StudentSchedulePage />} />
          <Route path="/progress" element={<ProgressPage />} />
          <Route path="/resource" element={<ResourcePage />} />
        </Routes>
      </div>
    </div>
  );
}
