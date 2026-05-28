package com.eventhub.eventhub.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.eventhub.eventhub.model.Comment;
import com.eventhub.eventhub.model.Event;

@Repository
public interface CommentRepository {

    List<Comment> findByEvent(Event event);

    List<Comment> findByUserUsername(String username);
}
