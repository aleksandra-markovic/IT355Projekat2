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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    private User user;
    private User admin;
    private Event event;
    private Comment comment;
    private CommentRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRole(Role.ROLE_USER);

        admin = new User();
        admin.setId(2L);
        admin.setUsername("admin");
        admin.setRole(Role.ROLE_ADMIN);

        event = new Event();
        event.setId(1L);
        event.setEventName("Utakmica");

        comment = new Comment();
        comment.setId(1L);
        comment.setUser(user);
        comment.setEvent(event);
        comment.setContent("Super event");

        request = new CommentRequest();
        request.setContent("Super event");
    }

    @Test
    void getCommentsByEvent_shouldReturnComments() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(commentRepository.findByEvent(event)).thenReturn(List.of(comment));

        List<Comment> result = commentService.getCommentsByEvent(1L);

        assertEquals(1, result.size());
        assertEquals("Super event", result.get(0).getContent());

        verify(eventRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).findByEvent(event);
    }

    @Test
    void getCommentsByEvent_shouldThrowException_whenEventNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () ->
                commentService.getCommentsByEvent(1L)
        );

        verify(commentRepository, never()).findByEvent(any(Event.class));
    }

    @Test
    void addComment_shouldSaveComment() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = commentService.addComment(1L, "testuser", request);

        assertNotNull(result);
        assertEquals("Super event", result.getContent());
        assertEquals(user, result.getUser());
        assertEquals(event, result.getEvent());

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(eventRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addComment_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                commentService.addComment(1L, "testuser", request)
        );

        verify(eventRepository, never()).findById(anyLong());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void addComment_shouldThrowException_whenEventNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () ->
                commentService.addComment(1L, "testuser", request)
        );

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void deleteComment_shouldDeleteComment_whenUserIsOwner() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        commentService.deleteComment(1L, "testuser");

        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteComment_shouldDeleteComment_whenUserIsAdmin() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

        commentService.deleteComment(1L, "admin");

        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteComment_shouldThrowException_whenCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () ->
                commentService.deleteComment(1L, "testuser")
        );

        verify(userRepository, never()).findByUsername(anyString());
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    void deleteComment_shouldThrowException_whenUserNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                commentService.deleteComment(1L, "testuser")
        );

        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    void deleteComment_shouldThrowException_whenUserIsNotOwnerOrAdmin() {
        User anotherUser = new User();
        anotherUser.setId(3L);
        anotherUser.setUsername("anotheruser");
        anotherUser.setRole(Role.ROLE_USER);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findByUsername("anotheruser")).thenReturn(Optional.of(anotherUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                commentService.deleteComment(1L, "anotheruser")
        );

        assertEquals("You can delete only your own comment", exception.getMessage());

        verify(commentRepository, never()).delete(any(Comment.class));
    }
}