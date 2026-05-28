package com.eventhub.eventhub.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.eventhub.eventhub.model.Event;
import com.eventhub.eventhub.model.Location;
import com.eventhub.eventhub.model.enums.Category;

@Repository
public interface EventRepository {

    List<Event> findByLocation(Location location);

    List<Event> findByCategory(Category category);

    List<Event> findByNameContainingIgnoreCase(String name);
}
