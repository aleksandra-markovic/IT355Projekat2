package com.eventhub.eventhub.controller;

import com.eventhub.eventhub.dto.request.CommentRequest;
import com.eventhub.eventhub.model.Comment;
import com.eventhub.eventhub.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Svi ulogovani korisnici mogu da vide komentare za event.
     * Primer: GET /api/comments/event/1
     */
    @GetMapping("/event/{eventId}")
    public List<Comment> getCommentsByEvent(@PathVariable Long eventId) {
        return commentService.getCommentsByEvent(eventId);
    }

    /**
     * Korisnik dodaje komentar na event.
     * Primer: POST /api/comments/event/1
     */
    @PostMapping("/event/{eventId}")
    public Comment addComment(@PathVariable Long eventId,
                              @Valid @RequestBody CommentRequest request,
                              Principal principal) {
        return commentService.addComment(eventId, principal.getName(), request);
    }

    /**
     * Korisnik briše svoj komentar, admin može bilo koji.
     * Primer: DELETE /api/comments/5
     */
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId,
                              Principal principal) {
        commentService.deleteComment(commentId, principal.getName());
    }
}
