package com.eventhub.eventhub.controller;

import com.eventhub.eventhub.model.enums.Category;
import com.eventhub.eventhub.model.Event;
import com.eventhub.eventhub.model.Location;
import com.eventhub.eventhub.model.User;
import com.eventhub.eventhub.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Vraća sve događaje
     */
    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    /**
     * Vraća jedan događaj po ID-u
     */
    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    /**
     * Dodavanje novog događaja
     */
    @PostMapping
    public Event addEvent(@RequestBody Event event) {
        return eventService.addEvent(event);
    }

    /**
     * Izmena postojećeg događaja
     */
    @PutMapping("/{id}")
    public Event updateEvent(@PathVariable Long id,
                             @RequestBody Event event) {
        return eventService.updateEvent(id, event);
    }

    /**
     * Brisanje događaja
     */
    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {

        System.out.println("USAO U DELETE CONTROLLER");
        eventService.deleteEvent(id);
    }

    /**
     * Pretraga događaja po nazivu
     */
    @GetMapping("/search")
    public List<Event> searchEventsByName(@RequestParam String keyword) {
        return eventService.searchByName(keyword);
    }

    /**
     * Događaji po kategoriji
     */
    @GetMapping("/category/{category}")
    public List<Event> getEventsByCategory(@PathVariable Category category) {
        return eventService.getEventsByCategory(category);
    }

    /**
     * Događaji po organizatoru
     */
    @PostMapping("/organizer")
    public List<Event> getEventsByOrganizer(@RequestBody User organizer) {
        return eventService.getEventsByOrganizer(organizer);
    }

    /**
     * Događaji po lokaciji
     */
    @PostMapping("/location")
    public List<Event> getEventsByLocation(@RequestBody Location location) {
        return eventService.getEventsByLocation(location);
    }
}
