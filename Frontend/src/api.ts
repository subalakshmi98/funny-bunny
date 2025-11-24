import axios from "axios";

const BASE = import.meta.env.VITE_API_BASE ?? "";

export const api = {
  /**
   * Login to the application.
   * @param {Object} payload
   * @param {string} payload.username
   * @param {string} payload.password
   * @returns {Promise<AxiosResponse>}
   */
  login: (payload: { username: string; password: string }) =>
    axios.post(`${BASE}/api/v1/login`, payload),

  generateSemester: () => axios.get(`${BASE}/api/v1/semesters`),

  /**
   * Generates a master schedule for a given semester.
   * @param {number} semesterId the semester's id
   * @returns {Promise<AxiosResponse>}
   */
  generateMaster: (semesterId: number) =>
    axios.post(`${BASE}/api/v1/schedule/generate?semesterId=${semesterId}`),

  /**
   * Gets the master schedule for a given semester.
   * 
   * @param {number} semesterId the semester's id
   * @returns {Promise<AxiosResponse>}
   */
  getMasterSchedule: (semesterId: number) =>
    axios.get(`${BASE}/api/v1/schedule/${semesterId}`),

  /**
   * Gets the course schedules for a given semester.
   * 
   * @param {number} semesterId the semester's id
   * @returns {Promise<AxiosResponse>} containing the course schedules
   */
  getCourseSchedules: (semesterId: number) =>
    axios.get(`${BASE}/api/v1/schedule/courses/${semesterId}`),

  /**
   * Gets the schedule of a teacher.
   * 
   * @param {number} teacherId the teacher's id
   * @returns {Promise<AxiosResponse>} containing the teacher's schedule
   */
  getTeacherSchedule: (teacherId: number) =>
    axios.get(`${BASE}/api/v1/schedule/teacher/${teacherId}`),

  getTeachers: () => axios.get(`${BASE}/api/v1/teachers`),

  /**
   * Enrolls a student in a course.
   * 
   * @param {Object} payload The payload to send to the server.
   * @param {number} payload.studentId The student's ID.
   * @param {number} payload.courseId The course's ID.
   * @param {number} [payload.semesterId] The semester's ID (optional).
   * @returns {Promise<AxiosResponse>} The response from the server.
   */
  enroll: (payload: {
    studentId: number;
    courseId: number;
    semesterId?: number;
  }) => axios.post(`${BASE}/api/v1/enrollment`, payload),

  /**
   * Gets the eligible sections for a given student and semester.
   * 
   * @param {number} studentId the student's ID
   * @param {number} semesterId the semester's ID
   * @returns {Promise<AxiosResponse>} containing a list of eligible sections
   */
  getEligible: (studentId: number, semesterId: number) =>
    axios.get(
      `${BASE}/api/v1/enrollment/student/${studentId}/eligible?semesterId=${semesterId}`
    ),

  /**
   * Gets the student's schedule for a given semester.
   * 
   * @param {number} studentId the student's ID
   * @param {number} [semesterId] the semester's ID (optional)
   * @returns {Promise<AxiosResponse>} containing the student's schedule
   */
  getStudentSchedule: (studentId: number, semesterId?: number) =>
    axios.get(
      `${BASE}/api/v1/enrollment/student/${studentId}/schedule${
        semesterId ? `?semesterId=${semesterId}` : ""
      }`
    ),

  /**
   * Gets the academic progress of the student with the given ID.
   * 
   * @param {number} studentId The ID of the student
   * @returns {Promise<AxiosResponse>} containing the academic progress of the student
   */
  getProgress: (studentId: number) =>
    axios.get(`${BASE}/api/v1/student/academic/${studentId}/progress`),

  /**
   * Gets the academic transcript of the student with the given ID.
   * 
   * The transcript is a list of maps, each containing information about a course the student has taken.
   * The information includes the course ID, course code, course name, credits, semester, status, and recorded at date.
   * 
   * @param {number} studentId The ID of the student
   * @returns {Promise<AxiosResponse>} containing the academic transcript of the student
   */
  getTranscript: (studentId: number) =>
    axios.get(`${BASE}/api/v1/student/academic/${studentId}/transcript`),

  getTeachersAnalytics: () => axios.get(`${BASE}/api/v1/resource/teachers`),

  getRoomsAnalytics: () => axios.get(`${BASE}/api/v1/resource/rooms`),
};
