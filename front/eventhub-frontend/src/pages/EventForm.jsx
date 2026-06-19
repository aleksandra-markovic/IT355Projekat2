import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  addEvent,
  updateEvent,
  getEventById,
  getAllLocations,
  getAllUsers
} from "../api/api";

function EventForm() {
  const navigate = useNavigate();
  const { id } = useParams();

  const isEdit = Boolean(id);

  const [locations, setLocations] = useState([]);
  const [users, setUsers] = useState([]);

  const [formData, setFormData] = useState({
    eventName: "",
    description: "",
    dateTime: "",
    category: "KONCERT",
    locationId: ""
  });

  const [error, setError] = useState("");

  const categories = [
    "BIOSKOP",
    "ŽURKA",
    "KONCERT",
    "RADIONICA",
    "SEMINAR",
    "SPORT",
    "FITNESS",
    "UMETNOST",
    "KULTURA",
    "DRUGO",
    "KNJIGE"
  ];

  useEffect(() => {
    loadLocations();
    loadUsers();

    if (isEdit) {
      loadEvent();
    }
  }, [id]);

  const loadLocations = async () => {
    try {
      const data = await getAllLocations();
      setLocations(data);
    } catch (err) {
      setError(err.message);
    }
  };

  const loadUsers = async () => {
    try {
      const data = await getAllUsers();
      setUsers(data);
    } catch (err) {
      setError(err.message);
    }
  };

  const loadEvent = async () => {
    try {
      const event = await getEventById(id);

      setFormData({
        eventName: event.eventName || "",
        description: event.description || "",
        dateTime: event.dateTime
          ? event.dateTime.slice(0, 16)
          : "",
        category: event.category || "KONCERT",
        locationId: event.location?.id || ""
      });
    } catch (err) {
      setError(err.message);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const username = localStorage.getItem("username");

      const currentUser = users.find(
        (u) => u.username === username
      );

      if (!currentUser) {
        setError("Nije pronađen trenutno ulogovani korisnik.");
        return;
      }

      const eventToSend = {
        eventName: formData.eventName,
        description: formData.description,
        dateTime: formData.dateTime,
        category: formData.category,

        location: {
          id: Number(formData.locationId)
        },

        organizer: {
          id: currentUser.id
        }
      };

      if (isEdit) {
        await updateEvent(id, eventToSend);
      } else {
        await addEvent(eventToSend);
      }

      navigate("/admin/events");
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="form-container">
      <h2>
        {isEdit
          ? "Izmena događaja"
          : "Dodavanje događaja"}
      </h2>

      {error && <p className="error">{error}</p>}

      <form onSubmit={handleSubmit}>
        <input
          type="text"
          name="eventName"
          placeholder="Naziv događaja"
          value={formData.eventName}
          onChange={handleChange}
          required
        />

        <textarea
          name="description"
          placeholder="Opis događaja"
          value={formData.description}
          onChange={handleChange}
          required
        />

        <input
          type="datetime-local"
          name="dateTime"
          value={formData.dateTime}
          onChange={handleChange}
          required
        />

        <select
          name="category"
          value={formData.category}
          onChange={handleChange}
        >
          {categories.map((category) => (
            <option
              key={category}
              value={category}
            >
              {category}
            </option>
          ))}
        </select>

        <select
          name="locationId"
          value={formData.locationId}
          onChange={handleChange}
          required
        >
          <option value="">
            Izaberi lokaciju
          </option>

          {locations.map((location) => (
            <option
              key={location.id}
              value={location.id}
            >
              {location.locationName} ({location.city})
            </option>
          ))}
        </select>

        <button type="submit">
          {isEdit
            ? "Sačuvaj izmene"
            : "Dodaj događaj"}
        </button>
      </form>
    </div>
  );
}

export default EventForm;