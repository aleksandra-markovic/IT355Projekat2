import { Link, useNavigate } from "react-router-dom";

function Navbar() {
  const navigate = useNavigate();

  const token = localStorage.getItem("token");
  const username = localStorage.getItem("username");
  const role = localStorage.getItem("role");

  const logout = () => {
    localStorage.clear();
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <Link to="/">EventHub</Link>

      <div>
        {!token && (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}

        {token && (
          <>
            <Link to="/events">Događaji</Link>
            {role === "ROLE_ADMIN" && <Link to="/admin">Admin</Link>}
            {role === "ROLE_USER" && (
                <Link to="/my-reservations">
                    Moje rezervacije
                </Link>
            )}
            <span>{username}</span>

            <button onClick={logout}>Logout</button>
          </>
        )}
      </div>
    </nav>
  );
}

export default Navbar;