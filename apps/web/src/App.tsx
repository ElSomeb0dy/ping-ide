import { Routes, Route } from "react-router-dom";
import Layout from "./components/Layout";
import RequireAdmin from "./components/RequireAdmin";
import RequireAuth from "./components/RequireAuth";
import Dashboard from "./pages/Dashboard";
import Lessons from "./pages/Lessons";
import LessonDetail from "./pages/LessonDetail";
import ExerciseIDE from "./pages/ExerciseIDE";
import Achievements from "./pages/Achievements";
import Profile from "./pages/Profile";
import Settings from "./pages/Settings";
import Login from "./pages/Login";
import Register from "./pages/Register";

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route element={<RequireAuth />}>
        <Route element={<Layout />}>
          <Route path="/" element={<Dashboard />} />
          <Route path="/lessons" element={<Lessons />} />
          <Route path="/lessons/:lessonId" element={<LessonDetail />} />
          <Route path="/lessons/:lessonId/exercises/:exerciseId" element={<ExerciseIDE />} />
          <Route path="/achievements" element={<Achievements />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/settings" element={<Settings />} />
          <Route element={<RequireAdmin />}>
            <Route path="/admin" element={<Lessons />} />
          </Route>
        </Route>
      </Route>
    </Routes>
  );
}
