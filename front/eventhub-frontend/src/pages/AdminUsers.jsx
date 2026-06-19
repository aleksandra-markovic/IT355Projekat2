import { useEffect, useState } from "react";
import {
  deleteUser,
  getAllUsers,
  searchUsers,
  updateUserRole
} from "../api/api";

function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [keyword, setKeyword] = useState("");
  const [error, setError] = useState("");

  const loggedUsername = localStorage.getItem("username");

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      const data = await getAllUsers();
      setUsers(data);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    setError("");

    if (keyword.trim() === "") {
      loadUsers();
      return;
    }

    try {
      const data = await searchUsers(keyword);
      setUsers(data);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleRoleChange = async (userId, newRole) => {
    try {
      await updateUserRole(userId, newRole);
      loadUsers();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleDelete = async (userId, username) => {
    if (username === loggedUsername) {
      setError("Ne možeš obrisati trenutno ulogovanog korisnika.");
      return;
    }

    const confirmDelete = window.confirm("Da li sigurno želiš da obrišeš korisnika?");

    if (!confirmDelete) return;

    try {
      await deleteUser(userId);
      loadUsers();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="page">
      <h1>Upravljanje korisnicima</h1>

      {error && <p className="error">{error}</p>}

      <form className="search-form" onSubmit={handleSearch}>
        <input
          type="text"
          placeholder="Pretraži po username..."
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
        />

        <button type="submit">Pretraži</button>
        <button type="button" onClick={loadUsers}>Prikaži sve</button>
      </form>

      <table className="admin-table">
        <thead>
          <tr>
            <th>Username</th>
            <th>Email</th>
            <th>Role</th>
            <th>Promena role</th>
            <th>Akcije</th>
          </tr>
        </thead>

        <tbody>
          {users.map((user) => (
            <tr key={user.id}>
              <td>{user.username}</td>
              <td>{user.email}</td>
              <td>{user.role}</td>

              <td>
                <select
                  value={user.role}
                  onChange={(e) => handleRoleChange(user.id, e.target.value)}
                  disabled={user.username === loggedUsername}
                >
                  <option value="ROLE_USER">ROLE_USER</option>
                  <option value="ROLE_ADMIN">ROLE_ADMIN</option>
                </select>
              </td>

              <td>
                <button
                  className="delete-btn"
                  onClick={() => handleDelete(user.id, user.username)}
                  disabled={user.username === loggedUsername}
                >
                  Obriši
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default AdminUsers;