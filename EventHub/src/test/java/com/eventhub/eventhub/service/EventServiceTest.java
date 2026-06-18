package com.eventhub.eventhub.service;

import com.eventhub.eventhub.exceptions.EventNotFoundException;
import com.eventhub.eventhub.model.Event;
import com.eventhub.eventhub.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    void getAllEvents_shouldReturnEvents() {

        Event event = new Event();
        event.setId(1L);
        event.setEventName("Koncert");
        event.setDescription("Opis koncerta");
        event.setDateTime(LocalDateTime.now());

        when(eventRepository.findAll()).thenReturn(List.of(event));

        List<Event> result = eventService.getAllEvents();

        assertEquals(1, result.size());
        assertEquals("Koncert", result.get(0).getEventName());

        verify(eventRepository).findAll();
    }

    @Test
    void getEventById_shouldReturnEvent() {

        Event event = new Event();
        event.setId(1L);
        event.setEventName("Festival");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Event result = eventService.getEventById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Festival", result.getEventName());

        verify(eventRepository).findById(1L);
    }

    @Test
    void getEventById_whenEventDoesNotExist_shouldThrowException() {

        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> {
            eventService.getEventById(1L);
        });

        verify(eventRepository).findById(1L);
    }

    @Test
    void addEvent_shouldSaveEvent() {

        Event event = new Event();
        event.setEventName("Utakmica");

        when(eventRepository.save(event)).thenReturn(event);

        Event result = eventService.addEvent(event);

        assertEquals("Utakmica", result.getEventName());

        verify(eventRepository).save(event);
    }

    @Test
    void deleteEvent_shouldDeleteEvent() {

        when(eventRepository.existsById(1L)).thenReturn(true);

        eventService.deleteEvent(1L);

        verify(eventRepository).existsById(1L);
        verify(eventRepository).deleteById(1L);
    }

    @Test
    void deleteEvent_whenEventDoesNotExist_shouldThrowException() {

        when(eventRepository.existsById(1L)).thenReturn(false);

        assertThrows(EventNotFoundException.class, () -> {
            eventService.deleteEvent(1L);
        });

        verify(eventRepository).existsById(1L);
        verify(eventRepository, never()).deleteById(1L);
    }
}