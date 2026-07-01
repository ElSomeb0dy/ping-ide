import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function RequireAdmin() {
  const auth = useAuth();

  if (!auth.isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (!auth.isAdmin) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
}
