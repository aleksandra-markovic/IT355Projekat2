import { useEffect, useState } from "react";
import {
  addComment,
  deleteComment,
  getCommentsByEvent
} from "../api/api";

function Comments({ eventId }) {
  const [comments, setComments] = useState([]);
  const [content, setContent] = useState("");
  const [message, setMessage] = useState("");

  const username = localStorage.getItem("username");
  const role = localStorage.getItem("role");

  useEffect(() => {
    loadComments();
  }, [eventId]);

  const loadComments = async () => {
    try {
      const data = await getCommentsByEvent(eventId);
      setComments(data);
    } catch (err) {
      setMessage(err.message);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");

    if (content.trim().length < 2) {
      setMessage("Komentar mora imati bar 2 karaktera.");
      return;
    }

    try {
      await addComment(eventId, content);
      setContent("");
      loadComments();
    } catch (err) {
      setMessage(err.message);
    }
  };

  const handleDelete = async (commentId) => {
    const confirmDelete = window.confirm("Da li želiš da obrišeš komentar?");

    if (!confirmDelete) return;

    try {
      await deleteComment(commentId);
      loadComments();
    } catch (err) {
      setMessage(err.message);
    }
  };

  return (
    <div className="comments-section">
      <h2>Komentari</h2>

      {message && <p className="error">{message}</p>}

      <form onSubmit={handleSubmit} className="comment-form">
        <textarea
          placeholder="Unesi komentar..."
          value={content}
          onChange={(e) => setContent(e.target.value)}
          required
        />

        <button type="submit">Dodaj komentar</button>
      </form>

      {comments.length === 0 ? (
        <p>Nema komentara za ovaj događaj.</p>
      ) : (
        comments.map((comment) => {
          const canDelete =
            role === "ROLE_ADMIN" ||
            comment.user?.username === username;

          return (
            <div className="comment-card" key={comment.id}>
              <p>{comment.content}</p>

              <small>
                Autor: {comment.user?.username}
              </small>

              {canDelete && (
                <button
                  className="delete-btn"
                  onClick={() => handleDelete(comment.id)}
                >
                  Obriši
                </button>
              )}
            </div>
          );
        })
      )}
    </div>
  );
}

export default Comments;