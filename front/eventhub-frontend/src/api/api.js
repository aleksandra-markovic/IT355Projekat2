const API_URL = "http://localhost:8080/api";

export const loginUser = async (data) => {
  const response = await fetch(`${API_URL}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
  });

  if (!response.ok) throw new Error("Pogrešno korisničko ime ili lozinka");

  return response.json();
};

export const registerUser = async (data) => {
  const response = await fetch(`${API_URL}/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
  });

  if (!response.ok) throw new Error("Registracija nije uspela");

  return response.json();
};

export const getToken = () => localStorage.getItem("token");

export const authHeaders = () => ({
  "Content-Type": "application/json",
  Authorization: `Bearer ${getToken()}`
});

// EVENTS
export const getAllEvents = async () => {
  const response = await fetch(`${API_URL}/events`, {
    headers: authHeaders()
  });

  if (!response.ok) throw new Error("Greška pri učitavanju događaja");

  return response.json();
};

export const getEventById = async (id) => {
  const response = await fetch(`${API_URL}/events/${id}`, {
    headers: authHeaders()
  });

  if (!response.ok) throw new Error("Događaj nije pronađen");

  return response.json();
};

export const searchEvents = async (keyword) => {
  const response = await fetch(`${API_URL}/events/search?keyword=${keyword}`, {
    headers: authHeaders()
  });

  if (!response.ok) throw new Error("Pretraga nije uspela");

  return response.json();
};

export const getEventsByCategory = async (category) => {
  const response = await fetch(`${API_URL}/events/category/${category}`, {
    headers: authHeaders()
  });

  if (!response.ok) throw new Error("Filtriranje nije uspelo");

  return response.json();
};

export const deleteEvent = async (id) => {
  const response = await fetch(`${API_URL}/events/${id}`, {
    method: "DELETE",
    headers: authHeaders()
  });

  if (!response.ok) throw new Error("Brisanje nije uspelo");
};

export const addEvent = async (event) => {
  const response = await fetch(`${API_URL}/events`, {
    method: "POST",
    headers: authHeaders(),
    body: JSON.stringify(event)
  });

  if (!response.ok) throw new Error("Dodavanje događaja nije uspelo");

  return response.json();
};

export const updateEvent = async (id, event) => {
  const response = await fetch(`${API_URL}/events/${id}`, {
    method: "PUT",
    headers: authHeaders(),
    body: JSON.stringify(event)
  });

  if (!response.ok) throw new Error("Izmena događaja nije uspela");

  return response.json();
};

export const getAllUsers = async () => {
  const response = await fetch(`${API_URL}/users`, {
    headers: authHeaders()
  });

  if (!response.ok) throw new Error("Greška pri učitavanju korisnika");

  return response.json();
};

export const getAllLocations = async () => {
  const response = await fetch(`${API_URL}/locations`, {
    headers: authHeaders()
  });

  if (!response.ok) throw new Error("Greška pri učitavanju lokacija");

  return response.json();
};

export const createReservation = async (eventId) => {
  const response = await fetch(`${API_URL}/reservations/event/${eventId}`, {
    method: "POST",
    headers: authHeaders()
  });

  if (!response.ok) throw new Error("Već imaš rezervaciju za ovaj događaj");

  return response.json();
};

export const getMyReservations = async () => {
  const response = await fetch(`${API_URL}/reservations/my`, {
    headers: authHeaders()
  });

  if (!response.ok) throw new Error("Greška pri učitavanju rezervacija");

  return response.json();
};

export const cancelReservation = async (reservationId) => {
  const response = await fetch(`${API_URL}/reservations/${reservationId}`, {
    method: "DELETE",
    headers: authHeaders()
  });

  if (!response.ok) throw new Error("Otkazivanje rezervacije nije uspelo");
};

export const getReservationsByEvent = async (eventId) => {
  const response = await fetch(`${API_URL}/reservations/event/${eventId}`, {
    headers: authHeaders()
  });

  if (!response.ok) {
    throw new Error("Greška pri učitavanju rezervacija za događaj");
  }

  return response.json();
};

export const getCommentsByEvent = async (eventId) => {
  const response = await fetch(`${API_URL}/comments/event/${eventId}`, {
    headers: authHeaders()
  });

  if (!response.ok) throw new Error("Greška pri učitavanju komentara");

  return response.json();
};

export const addComment = async (eventId, content) => {
  const response = await fetch(`${API_URL}/comments/event/${eventId}`, {
    method: "POST",
    headers: authHeaders(),
    body: JSON.stringify({ content })
  });

  if (!response.ok) throw new Error("Dodavanje komentara nije uspelo");

  return response.json();
};

export const deleteComment = async (commentId) => {
  const response = await fetch(`${API_URL}/comments/${commentId}`, {
    method: "DELETE",
    headers: authHeaders()
  });

  if (!response.ok) throw new Error("Brisanje komentara nije uspelo");
};

export const searchUsers = async (username) => {
  const response = await fetch(`${API_URL}/users/search?username=${username}`, {
    headers: authHeaders()
  });

  if (!response.ok) throw new Error("Pretraga korisnika nije uspela");

  return response.json();
};

export const updateUserRole = async (userId, role) => {
  const response = await fetch(`${API_URL}/users/${userId}/role`, {
    method: "PUT",
    headers: authHeaders(),
    body: JSON.stringify({ role })
  });

  if (!response.ok) throw new Error("Promena role nije uspela");

  return response.json();
};

export const deleteUser = async (userId) => {
  const response = await fetch(`${API_URL}/users/${userId}`, {
    method: "DELETE",
    headers: authHeaders()
  });

  if (!response.ok) throw new Error("Brisanje korisnika nije uspelo");
};