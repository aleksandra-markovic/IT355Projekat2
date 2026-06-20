import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getAllEvents, deleteEvent } from "../api/api";

function AdminEvents() {
  const [events, setEvents] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    loadEvents();
  }, []);

  const loadEvents = async () => {
    try {
      const data = await getAllEvents();
      setEvents(data);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleDelete = async (id) => {
    const confirmDelete = window.confirm("Da li sigurno želiš da obrišeš događaj?");

    if (!confirmDelete) return;

    try {
      await deleteEvent(id);
      loadEvents();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="page">
      <h1>Upravljanje događajima</h1>

      <Link className="add-btn" to="/admin/events/new">
        + Dodaj događaj
      </Link>

      {error && <p className="error">{error}</p>}

      <table className="admin-table">
        <thead>
          <tr>
            <th>Naziv</th>
            <th>Datum</th>
            <th>Kategorija</th>
            <th>Lokacija</th>
            <th>Akcije</th>
          </tr>
        </thead>

        <tbody>
          {events.map((event) => (
            <tr key={event.id}>
              <td>{event.eventName}</td>
              <td>{event.dateTime?.replace("T", " ")}</td>
              <td>{event.category}</td>
              <td>
                {event.location?.locationName}, {event.location?.city}
              </td>
              <td>
                <Link to={`/admin/events/${event.id}`}>Izmeni</Link>

                <Link to={`/admin/events/${event.id}/reservations`}>
                    Rezervacije
                </Link>

                <button
                  className="delete-btn"
                  onClick={() => handleDelete(event.id)}
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

export default AdminEvents;