import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getAllEvents, searchEvents, getEventsByCategory } from "../api/api";

function Events() {
  const [events, setEvents] = useState([]);
  const [keyword, setKeyword] = useState("");
  const [category, setCategory] = useState("");
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

  const handleSearch = async (e) => {
    e.preventDefault();

    if (keyword.trim() === "") {
      loadEvents();
      return;
    }

    try {
      const data = await searchEvents(keyword);
      setEvents(data);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleCategoryChange = async (e) => {
    const selectedCategory = e.target.value;
    setCategory(selectedCategory);

    if (selectedCategory === "") {
      loadEvents();
      return;
    }

    try {
      const data = await getEventsByCategory(selectedCategory);
      setEvents(data);
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="page">
      <h1>Događaji</h1>

      {error && <p className="error">{error}</p>}

      <form className="search-form" onSubmit={handleSearch}>
        <input
          type="text"
          placeholder="Pretraži događaje..."
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
        />

        <button type="submit">Pretraži</button>
      </form>

      <select value={category} onChange={handleCategoryChange}>
        <option value="">Sve kategorije</option>
        {categories.map((cat) => (
          <option key={cat} value={cat}>
            {cat}
          </option>
        ))}
      </select>

      <div className="event-grid">
        {events.map((event) => (
          <div className="event-card" key={event.id}>
            <h3>{event.eventName}</h3>
            <p>{event.description}</p>
            <p>
              <b>Datum:</b> {event.dateTime?.replace("T", " ")}
            </p>
            <p>
              <b>Kategorija:</b> {event.category}
            </p>
            <p>
              <b>Lokacija:</b>{" "}
              {event.location?.locationName}, {event.location?.city}
            </p>

            <Link to={`/events/${event.id}`}>Detalji</Link>
          </div>
        ))}
      </div>
    </div>
  );
}

export default Events;