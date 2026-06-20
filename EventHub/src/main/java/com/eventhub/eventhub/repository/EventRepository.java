package com.eventhub.eventhub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eventhub.eventhub.model.Event;
import com.eventhub.eventhub.model.Location;
import com.eventhub.eventhub.model.User;
import com.eventhub.eventhub.model.enums.Category;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByLocation(Location location);

    List<Event> findByCategory(Category category);

    List<Event> findByEventNameContainingIgnoreCase(String name);

    List<Event> findByOrganizer (User organizer);
}
