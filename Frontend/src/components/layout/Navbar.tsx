import { Link, useLocation } from "react-router-dom";
import { getUser } from "../../utils/auth";

/**
 * A React component for the navbar.
 * This component takes in no props and returns a navbar element with links to various pages.
 * The links are conditionally rendered based on the user's role.
 * If the user is an admin, links to the master, courses, teachers, enroll, student, progress, and resource pages are rendered.
 * If the user is a student, links to the student, enroll, progress, and resource pages are rendered.
 * If the user is a teacher, links to the teachers and resource pages are rendered.
 * The component also includes a logout button that removes the user from local storage and redirects to the login page.
 */
export default function Navbar() {
  const user = getUser();
  const location = useLocation();

  if (!user) return null;
  
  /**
   * Returns a CSS class string for a navbar link based on whether the current location matches the given path.
   * If the paths match, the link will have bold and primary text color, otherwise it will have dark text color.
   * @param {string} path - The path to check against the current location.
   * @returns {string} A CSS class string for the navbar link.
   */
  const linkClass = (path: string) =>
    "nav-link px-3 " +
    (location.pathname === path ? "fw-bold text-primary" : "text-dark");

  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-white shadow-sm mb-4 py-2">
      <div className="container-fluid">
        <span
          className="navbar-brand fw-bold text-primary"
          style={{ fontSize: "1.2rem" }}
        >
          Maplewood
        </span>

        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#mw-navbar"
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        <div className="collapse navbar-collapse" id="mw-navbar">
          <ul className="navbar-nav me-auto mb-2 mb-lg-0">
            {user.role === "ADMIN" && (
              <>
                <li className="nav-item">
                  <Link className={linkClass("/master")} to="/master">
                    Master
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className={linkClass("/courses")} to="/courses">
                    Courses
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className={linkClass("/teachers")} to="/teachers">
                    Teachers
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className={linkClass("/enroll")} to="/enroll">
                    Enroll
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className={linkClass("/student")} to="/student">
                    Student
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className={linkClass("/progress")} to="/progress">
                    Progress
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className={linkClass("/resource")} to="/resource">
                    Resource
                  </Link>
                </li>
              </>
            )}

            {user.role === "STUDENT" && (
              <>
                <li className="nav-item">
                  <Link className={linkClass("/student")} to="/student">
                    My Schedule
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className={linkClass("/enroll")} to="/enroll">
                    Enroll
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className={linkClass("/progress")} to="/progress">
                    Progress
                  </Link>
                </li>
              </>
            )}

            {user.role === "TEACHER" && (
              <>
                <li className="nav-item">
                  <Link className={linkClass("/teachers")} to="/teachers">
                    My Schedule
                  </Link>
                </li>
              </>
            )}
          </ul>

          <div className="d-flex">
            <span
              className="text-danger fw-semibold"
              style={{ cursor: "pointer" }}
              onClick={() => {
                localStorage.removeItem("user");
                window.location.href = "/login";
              }}
            >
              Logout
            </span>
          </div>
        </div>
      </div>
    </nav>
  );
}
