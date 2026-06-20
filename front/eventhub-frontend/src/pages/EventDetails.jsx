import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getEventById, createReservation } from "../api/api";
import Comments from "../components/Comments";

function EventDetails() {
  const { id } = useParams();

  const [event, setEvent] = useState(null);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    const loadEvent = async () => {
      try {
        const data = await getEventById(id);
        setEvent(data);
      } catch (err) {
        setError(err.message);
      }
    };

    loadEvent();
  }, [id]);

  const handleReservation = async () => {
    try {
      await createReservation(id);
      setMessage("Uspešno si se prijavila na događaj.");
    } catch (err) {
      setMessage(err.message);
    }
  };

  if (error) {
    return <p className="error">{error}</p>;
  }

  if (!event) {
    return <p className="page">Učitavanje...</p>;
  }

  return (
    <div className="page">
      <h1>{event.eventName}</h1>

      <p>{event.description}</p>

      <p>
        <b>Datum:</b> {event.dateTime?.replace("T", " ")}
      </p>

      <p>
        <b>Kategorija:</b> {event.category}
      </p>

      <h3>Lokacija</h3>
      <p>
        {event.location?.locationName}, {event.location?.city},{" "}
        {event.location?.adress}
      </p>

      <h3>Organizator</h3>
      <p>{event.organizer?.username}</p>

      <button className="add-btn" onClick={handleReservation}>
        Rezerviši mesto
      </button>

      <Comments eventId={id} />

      {message && <p>{message}</p>}
    </div>
  );
}

export default EventDetails;