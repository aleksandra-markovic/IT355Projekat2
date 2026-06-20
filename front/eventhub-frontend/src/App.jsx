import { Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import AdminDashboard from "./pages/AdminDashboard";
import ProtectedRoute from "./components/ProtectedRoute";
import Events from "./pages/Events";
import EventDetails from "./pages/EventDetails";
import AdminEvents from "./pages/AdminEvents";
import EventForm from "./pages/EventForm";
import MyReservations from "./pages/MyReservations";
import EventReservations from "./pages/EventReservations";
import AdminUsers from "./pages/AdminUsers";
import AdminLocations from "./pages/AdminLocations";

function App() {

  return (
    <>
      
      <Navbar />

      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        <Route
          path="/admin"
          element={
            <ProtectedRoute adminOnly={true}>
              <AdminDashboard />
            </ProtectedRoute>
          }
        />

        <Route
          path="/events"
          element={
            <ProtectedRoute>
              <Events />
            </ProtectedRoute>
          }
        />

        <Route
          path="/events/:id"
          element={
            <ProtectedRoute>
              <EventDetails />
            </ProtectedRoute>
          }
        />

        <Route
          path="/admin/events"
          element={
            <ProtectedRoute adminOnly={true}>
              <AdminEvents />
            </ProtectedRoute>
          }
        />

        <Route
          path="/admin/events/new"
          element={
            <ProtectedRoute adminOnly={true}>
              <EventForm />
            </ProtectedRoute>
          }
        />

        <Route
          path="/admin/events/:id"
          element={
            <ProtectedRoute adminOnly={true}>
              <EventForm />
            </ProtectedRoute>
          }
        />

        <Route
          path="/my-reservations"
          element={
            <ProtectedRoute>
              <MyReservations />
            </ProtectedRoute>
          }
        />

        <Route
          path="/admin/events/:id/reservations"
          element={
            <ProtectedRoute adminOnly={true}>
              <EventReservations />
            </ProtectedRoute>
          }
        />

        <Route
          path="/admin/users"
          element={
            <ProtectedRoute adminOnly={true}>
              <AdminUsers />
            </ProtectedRoute>
          }
        />

        <Route
          path="/admin/locations"
          element={
            <ProtectedRoute adminOnly={true}>
              <AdminLocations />
            </ProtectedRoute>
          }
        />

      </Routes>
    </>
  )
}

export default App
