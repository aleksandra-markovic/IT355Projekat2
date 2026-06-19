import { Link } from "react-router-dom";

function AdminDashboard() {
  return (
    <div className="page">
      <h1>Admin panel</h1>

      <div className="admin-links">
        <Link to="/admin/events">Upravljanje događajima</Link>
        <Link to="/admin/users">Upravljanje korisnicima</Link>
      </div>
    </div>
  );
}

export default AdminDashboard;