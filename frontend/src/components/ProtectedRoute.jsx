import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

// roles: list of allowed roles. Not signed in -> /login. Wrong role -> back to /app.
export default function ProtectedRoute({ roles, children }) {
  const { session } = useAuth();
  if (!session) return <Navigate to="/login" replace />;
  if (roles && !roles.includes(session.accountRole)) return <Navigate to="/app" replace />;
  return children;
}
