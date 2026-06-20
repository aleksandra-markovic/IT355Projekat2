import { Navigate } from "react-router-dom";

function ProtectedRoute({ children, adminOnly }) {
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");

  if (!token) {
    return <Navigate to="/login" />;
  }

  if (adminOnly && role !== "ROLE_ADMIN") {
    return <Navigate to="/" />;
  }

  return children;
}

export default ProtectedRoute;