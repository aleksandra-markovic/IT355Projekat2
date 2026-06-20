import { useEffect, useState } from "react";

export default function AdminLocations() {
  const [locations, setLocations] = useState([]);
  const [formData, setFormData] = useState({
    city: "",
    adress: "",
    locationName: ""
  });

  const token = localStorage.getItem("token");

  useEffect(() => {
    fetchLocations();
  }, []);

  const fetchLocations = () => {
    fetch("http://localhost:8080/api/locations", {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
      .then(res => res.json())
      .then(data => setLocations(data))
      .catch(err => console.error(err));
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    fetch("http://localhost:8080/api/locations", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify(formData)
    })
      .then(res => res.json())
      .then(() => {
        setFormData({
          city: "",
          adress: "",
          locationName: ""
        });
        fetchLocations();
      })
      .catch(err => console.error(err));
  };

  const handleDelete = (id) => {
    fetch(`http://localhost:8080/api/locations/${id}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
      .then(() => fetchLocations())
      .catch(err => console.error(err));
  };

  return (
    <div>
      <h2>Lokacije</h2>

      <form onSubmit={handleSubmit}>
        <input
          type="text"
          name="city"
          placeholder="Grad"
          value={formData.city}
          onChange={handleChange}
          required
        />

        <input
          type="text"
          name="adress"
          placeholder="Adresa"
          value={formData.adress}
          onChange={handleChange}
          required
        />

        <input
          type="text"
          name="locationName"
          placeholder="Naziv lokacije"
          value={formData.locationName}
          onChange={handleChange}
          required
        />

        <button type="submit">Dodaj lokaciju</button>
      </form>

      <h3>Sve lokacije</h3>

      {locations.map(location => (
        <div key={location.id}>
          <p>
            {location.locationName} — {location.city}, {location.adress}
          </p>
          <button onClick={() => handleDelete(location.id)}>
            Obriši
          </button>
        </div>
      ))}
    </div>
  );
}