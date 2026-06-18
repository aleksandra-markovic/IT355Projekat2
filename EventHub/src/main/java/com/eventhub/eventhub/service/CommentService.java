package com.eventhub.eventhub.service;

import com.eventhub.eventhub.dto.request.CommentRequest;
import com.eventhub.eventhub.exceptions.CommentNotFoundException;
import com.eventhub.eventhub.exceptions.EventNotFoundException;
import com.eventhub.eventhub.exceptions.UserNotFoundException;
import com.eventhub.eventhub.model.Comment;
import com.eventhub.eventhub.model.Event;
import com.eventhub.eventhub.model.User;
import com.eventhub.eventhub.model.enums.Role;
import com.eventhub.eventhub.repository.CommentRepository;
import com.eventhub.eventhub.repository.EventRepository;
import com.eventhub.eventhub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          EventRepository eventRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new EventNotFoundException("Event not found with id: " + eventId));

        return commentRepository.findByEvent(event);
    }

    public Comment addComment(Long eventId, String username, CommentRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found: " + username));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new EventNotFoundException("Event not found with id: " + eventId));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setEvent(event);
        comment.setContent(request.getContent());

        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() ->
                        new CommentNotFoundException("Comment not found with id: " + commentId));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found: " + username));

        boolean isOwner = comment.getUser().getUsername().equals(username);
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("You can delete only your own comment");
        }

        commentRepository.delete(comment);
    }
}
