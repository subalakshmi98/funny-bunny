# 📘 Academic Scheduling System

## 🚀 Overview

The **Academic Scheduling System** is a full‑stack platform designed to modernize academic scheduling, automate course/teacher/classroom assignments, and provide student‑friendly tools for enrollment and progress tracking.

This project includes:

* A **Spring Boot backend** responsible for schedule generation, enrollment logic, transcript building, academic progress analysis, and resource utilization (teacher/room).
* A **React + TypeScript frontend**, providing interfaces for administrators, teachers, and students.

This document serves as the technical overview of the system, derived from backend and frontend documentation.

# 🏗️ System Architecture

## 🔧 Backend Architecture (Spring Boot)

The backend follows a **layered architecture**:

* **Controller Layer** – REST API endpoints
* **Service Layer** – Business logic (scheduling, enrollment, progress, transcripts)
* **Repository Layer** – DB interaction via Spring Data JPA
* **Persistence Layer** – SQLite relational database

### 📁 Backend Folder Structure

* `config/` – CORS, Swagger, initial setup
* `controller/` – API controllers
* `dto/` – Request/response objects
* `entity/` – JPA entities
* `repository/` – Database interaction layer
* `service/` – Core business services
* `exception/` – Custom exceptions
* `resources/` – App configs


## 🎨 Frontend Architecture (React + TypeScript)

* **React Functional Components + Hooks**
* **React Router** for navigation
* **API abstraction layer** (`api.ts`)
* **Reusable UI components** (Button, Input, Card, PageHeader, Timetable)
* **Role-based rendering** (Admin / Teacher / Student)

### 📁 Frontend Folder Structure

components/
  layout/Navbar.tsx
  Button.tsx
  Card.tsx
  Input.tsx
  Select.tsx
  PageHeader.tsx
  VerticalTimetable.tsx
  WeeklyCalendar.tsx
pages/
  LoginPage.tsx
  EnrollmentPage.tsx
  CourseSchedulePage.tsx
  MasterSchedulePage.tsx
  StudentSchedulePage.tsx
  TeacherSchedulePage.tsx
  ResourcePage.tsx
  ProgressPage.tsx
utils/
  auth.ts
  api.ts

# 🗄️ Database Summary

The system uses an SQLite database containing:

* Classrooms & Room Types
* Courses & Course Sections
* Section Meetings
* Semesters & Specializations
* Students & Teachers
* Student Course History
* Enrollments & Grades

An ERD diagram (System Design folder) visualizes relationships among these entities.


# 🧠 Backend Service Summary

# 🧩✨ System Design Principles, Patterns & Algorithms (Fun + Friendly Edition!)

Welcome to the *brain* of the Maplewood Scheduling System! 🧠⚙️
Here’s a fun, emoji-filled breakdown of how the system thinks, plans, validates, and automates your academic world. 🎓💡

## 🎯 Design Principles (The "Rules We Live By")

* **🎈 Single Responsibility Principle (SRP)** – Every service does *one thing amazingly well*.
* **🧩 Separation of Concerns** – Keep things tidy! Each layer has its own job.
* **🪄 Dependency Injection** – Magic wiring so everything stays flexible.
* **🛡️ Transactional Integrity** – All-or-nothing safety net.
* **🚦 Fail‑Fast Validation** – Stop bad actions before they cause trouble.
* **🔁 DRY** – No copy‑paste madness; reuse is king.

## 🏛️ Design Patterns (Our System’s Secret Superpowers)

* **📦 Service Layer Pattern** – Business logic stays clean and organized.
* **📚 Repository Pattern** – Easy, safe database access.
* **🔀 Mapper Pattern** – Converts database entities → friendly DTOs.
* **✔️ Validation Pattern** – A pipeline of checks to keep data clean.
* **🌀 Recursive Pattern** – Perfect for prerequisite chains.
* **📊 Aggregator Pattern** – Rolls up stats for teachers & rooms.
* **🧠 Strategy‑like Delegation** – Smart assignment selection.
* **📅 Comparator Pattern** – Weekday sorting like a pro.

## 🧮 Algorithms (How the System "Thinks")

### 🕒 Scheduling Algorithms (ScheduleGeneratorService)

* **🔁 Course Scheduling Loop** – Builds sections one by one.
* **🧠 Constraint‑based Assignment** – Picks the best teacher & room.
* **⚡ Greedy Time Scheduling** – Finds the earliest valid time.
* **📅 Sorting Algorithm** – Keeps meetings in clean weekday order.
* **📦 Grouping** – Organizes sections by course.

### 📝 Enrollment Algorithms (EnrollmentService)

* **🚦 Validation Pipeline** – Every rule checked step‑by‑step.
* **🌀 Recursive Prerequisite Checker** – Deep prerequisite verification.
* **⏱️ Conflict Detection** – No double‑booking allowed!
* **🎯 Smart Section Selector** – Chooses the best available seat.

### 🔍 Eligibility Algorithms (EligibilityService)

* **🚫 Fail‑Fast Filters** – Quickly remove invalid options.
* **🕒 Conflict Detection** – Finds overlapping meetings.
* **📏 Capacity Checks** – Makes sure there’s room.

### 📅 Schedule Rendering Algorithms (ScheduleService)

* **🔍 Enrollment Lookup** – Finds all sections per student.
* **🎨 DTO Mapping** – Builds clean schedule items.
* **📊 Capacity Calculator** – Remaining seats count.

### 🎓 Academic Progress Algorithms (AcademicProgressService)

* **🎯 Credit Summation** – Earned vs. needed credits.
* **📘 GPA Calculation** – Weighted score math.
* **📚 Core Completion** – Tracks mandatory course progress.
* **🗓️ Graduation Projection** – Estimates time left.

### 📜 Transcript Algorithms (AcademicTranscriptService)

* **📂 History Iteration** – Loops through all course history.
* **🧾 Mapping** – Formats transcript rows.
* **🏷️ Semester Labeling** – Pretty names like “Fall 2024”.

### 🏫 Resource Utilization Algorithms (ResourceUtilizationService)

* **⏳ Weekly Hours Calculation** – Sum of teaching/room hours.
* **📅 Daily Load** – Workload per weekday.
* **📈 Usage Percentage** – Utilization math.
* **⚠️ Conflict Detection** – Finds overlapping assignments.
* **📦 Grouping** – Organizes by teacher or room.

# 🧠 Backend Service Summary

## 1️⃣ ScheduleGeneratorService

Generates semester schedules by assigning:

* Teachers
* Classrooms
* Meeting times
* Course sections

Uses **constraint-based assignment**, **greedy scheduling**, and **sorting/grouping algorithms**.

## 2️⃣ EnrollmentService

Validates and enrolls students based on:

* Capacity
* Prerequisites
* Schedule conflicts
* Rules & limits

Implements **validation pipelines**, **recursive prerequisite checks**, and **conflict detection**.

## 3️⃣ EligibilityService

Filters available course sections for students using eligibility algorithms and fail‑fast filtering.

## 4️⃣ ScheduleService

Builds formatted schedule responses for students and teachers.

## 5️⃣ AcademicProgressService

Calculates:

* GPA
* Earned credits
* Remaining core courses
* Predicted time to graduate

## 6️⃣ AcademicTranscriptService

Generates formatted transcripts from historical course records.

## 7️⃣ ResourceUtilizationService

Analyzes:

* Teacher workload
* Classroom usage
* Daily/weekly load charts

Uses aggregation, conflict detection, and utilization percentage formulas.

# 💻 Frontend Page & Component Summary

## Key Pages

* **LoginPage** – Role‑based authentication
* **MasterSchedulePage** – Admin schedule generation
* **CourseSchedulePage** – Course → section → timetable view
* **EnrollmentPage** – Students enroll in eligible sections
* **StudentSchedulePage** – Visual weekly student schedule
* **TeacherSchedulePage** – Teacher workload view
* **ResourcePage** – Analytics (charts)
* **ProgressPage** – GPA & credit progress

## Key Components

* **Button** – Standardized UI button
* **Input / Select** – Form controls
* **Card** – UI grouping container
* **VerticalTimetable** – Weekly time-block grid
* **PageHeader** – Page top banner + actions
* **Loading** – Spinner for async calls

# 🧪 Error Handling

* Centralized API error responses using `ApiResponse` DTO
* Custom exceptions such as `EnrollmentException`
* Consistent validation, mapping, and HTTP responses

# 🔐 Security

* Authentication via LoginService
* Role-based authorization (student, teacher, admin)
* Input validation
* ORM‑based SQL injection prevention

# ▶️ Getting Started

## 🚀 Backend — Local Run
### Prerequisites

- Java 11+
- Spring Boot
- Maven
- SQLite

### Installation

1. Clone the repo:
    ```
    https://github.com/subalakshmi98/funny-bunny/
    ```

2. Build the project:
    ```
    mvn clean install
    ```

3. Configure your database connection in `src/main/resources/application.properties`.

4. Run the project:
    ```
    mvn spring-boot:run
    ```

The server will start, and you can access the API at `http://localhost:8080`.

## API Documentation

You can find the API documentation at `http://localhost:8080/swagger-ui.html` when the server is running.


## 💻 Frontend — Local Run
### Prerequisites

- React + Typescript
- Bootstrap

## Available Scripts

In the project directory, you can run:

    ```
    npm install
    npm run build
    npm run preview
    ```

Runs the app in the development mode.
Open [http://localhost:4173/](http://localhost:4173/) to view it in your browser.

The page will reload when you make changes.
You may also see any lint errors in the console.

```
npm test
```

Launches the test runner in the interactive watch mode.

```
npm run build
```

Builds the app for production to the `build` folder.
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.
Your app is ready to be deployed!

## 🐳 Docker — Build & Run
This project contains a Spring Boot backend and a React + Vite frontend, both fully containerized using Docker and runnable with Docker Compose.

## 🐳 Running With Docker Compose (Recommended)

From the project root, run:
```
docker compose up --build
```

After startup:
Frontend: http://localhost:3000
Backend: http://localhost:8080

Stop containers:
```
docker compose down
```

## 🟦 Run Frontend Docker Only
```
docker compose up --build frontend
```

Visit:
👉 http://localhost:3000

Login Creds:
admin: admin123
<email>: teacher123
<email>: student123

## 🟩 Run Backend Docker Only
```
docker compose up --build backend
```

Visit API:
👉 http://localhost:8080

## Rebuild containers 
```
docker compose down
docker compose build backend
docker compose build frontend
```

## Start all services
```
docker compose up
```

## ⚙️ Environment Notes
Backend

Uses SQLite file stored at:
Backend/src/main/db/maplewood_school.sqlite

Frontend
Calls backend using:
VITE_API_URL=http://localhost:8080

## ✅ Requirements

Docker
Docker Compose

No other local setup is required.

## 🎓 Final Notes

This system provides a robust foundation for automated scheduling, realistic academic progress analysis, and modern school administration workflows.
