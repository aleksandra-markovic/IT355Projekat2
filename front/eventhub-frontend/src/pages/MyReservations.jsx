import { useEffect, useState } from "react";
import { cancelReservation, getMyReservations } from "../api/api";

function MyReservations() {
  const [reservations, setReservations] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    loadReservations();
  }, []);

  const loadReservations = async () => {
    try {
      const data = await getMyReservations();
      setReservations(data);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleCancel = async (id) => {
    const confirmCancel = window.confirm("Da li želiš da otkažeš rezervaciju?");

    if (!confirmCancel) return;

    try {
      await cancelReservation(id);
      loadReservations();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="page">
      <h1>Moje rezervacije</h1>

      {error && <p className="error">{error}</p>}

      {reservations.length === 0 ? (
        <p>Nemaš rezervacije.</p>
      ) : (
        <div className="event-grid">
          {reservations.map((reservation) => (
            <div className="event-card" key={reservation.id}>
              <h3>{reservation.event?.eventName}</h3>

              <p>
                <b>Datum događaja:</b>{" "}
                {reservation.event?.dateTime?.replace("T", " ")}
              </p>

              <p>
                <b>Lokacija:</b>{" "}
                {reservation.event?.location?.locationName},{" "}
                {reservation.event?.location?.city}
              </p>

              <p>
                <b>Datum rezervacije:</b>{" "}
                {reservation.reservationDate?.replace("T", " ")}
              </p>

              <button
                className="delete-btn"
                onClick={() => handleCancel(reservation.id)}
              >
                Otkaži rezervaciju
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default MyReservations;