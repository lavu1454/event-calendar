package com.example.demo.service;

import com.example.demo.dto.EventRequest;
import com.example.demo.dto.EventResponse;
import com.example.demo.entity.Event;
import com.example.demo.entity.User;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private EventResponse toResponse(Event e) {
        return new EventResponse(e.getId(), e.getTitle(), e.getDescription(),
                e.getCategory(), e.getCalendarType(), e.getStart(), e.getEnd());
    }

    public EventResponse create(EventRequest request, String email) {
        if (request.getStartTime().isAfter(request.getEndTime()))
            throw new RuntimeException("Start time must be before end time");
        User user = getUser(email);
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setCalendarType(request.getCalendarType());
        event.setStart(request.getStartTime());
        event.setEnd(request.getEndTime());
        event.setUser(user);
        Event saved = eventRepository.save(event);
        log.info("User {} created event id={} title='{}'", email, saved.getId(), saved.getTitle());
        return toResponse(saved);
    }

    public List<EventResponse> getAll(String email) {
        return eventRepository.findByUser(getUser(email)).stream().map(this::toResponse).toList();
    }

    public List<EventResponse> getByRange(String email, LocalDateTime start, LocalDateTime end, String search) {
        return eventRepository.searchByUserAndRange(getUser(email), start, end, search)
                .stream().map(this::toResponse).toList();
    }

    public EventResponse getById(Long id, String email) {
        return toResponse(eventRepository.findByIdAndUser(id, getUser(email))
                .orElseThrow(() -> new RuntimeException("Event not found")));
    }

    public EventResponse update(Long id, EventRequest request, String email) {
        if (request.getStartTime().isAfter(request.getEndTime()))
            throw new RuntimeException("Start time must be before end time");
        Event event = eventRepository.findByIdAndUser(id, getUser(email))
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setCalendarType(request.getCalendarType());
        event.setStart(request.getStartTime());
        event.setEnd(request.getEndTime());
        Event saved = eventRepository.save(event);
        log.info("User {} updated event id={}", email, id);
        return toResponse(saved);
    }

    public void delete(Long id, String email) {
        Event event = eventRepository.findByIdAndUser(id, getUser(email))
                .orElseThrow(() -> new RuntimeException("Event not found"));
        eventRepository.delete(event);
        log.info("User {} deleted event id={}", email, id);
    }
}
