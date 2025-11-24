import { useState } from "react";
import { api } from "../api";
import Input from "../components/Input";
import Button from "../components/Button";

/**
 * A login page for the Maplewood School Portal.
 * The page contains a left split with a welcome message and a right split with a login form.
 * The login form takes in a username and password, and upon submission, attempts to log the user in
 * using the API. If the login is successful, the user is redirected to their respective page.
 * If the login fails, an alert is shown to the user with an error message.
 */
export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  /**
   * Handles login submission.
   * Attempts to log the user in using the API, and upon successful login, redirects the user to their respective page.
   * If the login fails, an alert is shown to the user with an error message.
   */
  const handleLogin = async () => {
    try {
      const res = await api.login({ username, password });
      const user = res.data;

      if (!user.role) {
        alert("Invalid login response.");
        return;
      }

      localStorage.setItem("user", JSON.stringify(user));

      if (user.role === "ADMIN") window.location.href = "/master";
      if (user.role === "TEACHER") window.location.href = "/teachers";
      if (user.role === "STUDENT") window.location.href = "/student";
    } catch {
      alert("Invalid username or password");
    }
  };

  return (
    <div className="container-fluid vh-100">
      <div className="row h-100">
        {/* LEFT SPLIT */}
        <div
          className="col-md-6 d-none d-md-flex flex-column justify-content-center align-items-center text-white"
          style={{
            background: "linear-gradient(135deg, #2563EB, #1D4ED8)",
            padding: "40px",
          }}
        >
          <h1 className="fw-bold mb-3" style={{ fontSize: "2.2rem" }}>
            Maplewood School Portal
          </h1>
          <p className="text-white-50" style={{ maxWidth: 360 }}>
            Welcome back! Please sign in to access your schedule, courses,
            enrollment, and resources.
          </p>
        </div>

        {/* RIGHT SPLIT (LOGIN FORM) */}
        <div className="col-md-6 d-flex justify-content-center align-items-center bg-light">
          <div
            className="mw-card p-4 w-100"
            style={{
              maxWidth: 380,
              borderRadius: 12,
            }}
          >
            <h3 className="text-center mb-4 fw-bold">Login</h3>

            <div className="mb-3">
              <Input
                label="Email / Username"
                placeholder="Enter your username"
                value={username}
                onChange={(e: any) => setUsername(e.target.value)}
              />
            </div>

            <div className="mb-3">
              <Input
                type="password"
                label="Password"
                placeholder="Enter your password"
                value={password}
                onChange={(e: any) => setPassword(e.target.value)}
              />
            </div>

            <Button
              variant="primary"
              className="w-100 mt-2"
              onClick={handleLogin}
            >
              Sign In
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}
