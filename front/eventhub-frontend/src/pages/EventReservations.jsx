import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getReservationsByEvent } from "../api/api";

function EventReservations() {
  const { id } = useParams();

  const [reservations, setReservations] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    loadReservations();
  }, [id]);

  const loadReservations = async () => {
    try {
      const data = await getReservationsByEvent(id);
      setReservations(data);
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="page">
      <h1>Rezervacije za događaj</h1>

      {error && <p className="error">{error}</p>}

      {reservations.length === 0 ? (
        <p>Nema rezervacija za ovaj događaj.</p>
      ) : (
        <table className="admin-table">
          <thead>
            <tr>
              <th>Korisnik</th>
              <th>Email</th>
              <th>Datum rezervacije</th>
            </tr>
          </thead>

          <tbody>
            {reservations.map((reservation) => (
              <tr key={reservation.id}>
                <td>{reservation.user?.username}</td>
                <td>{reservation.user?.email}</td>
                <td>{reservation.reservationDate?.replace("T", " ")}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default EventReservations;