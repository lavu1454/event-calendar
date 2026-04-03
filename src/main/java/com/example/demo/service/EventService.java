package com.example.demo.service;

import com.example.demo.dto.EventRequest;
import com.example.demo.dto.EventResponse;
import com.example.demo.entity.Event;
import com.example.demo.entity.User;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

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
        return new EventResponse(e.getId(), e.getTitle(), e.getDescription(), e.getStartTime(), e.getEndTime());
    }

    public EventResponse create(EventRequest request, String email) {
        User user = getUser(email);
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setUser(user);
        return toResponse(eventRepository.save(event));
    }

    public List<EventResponse> getAll(String email) {
        return eventRepository.findByUser(getUser(email)).stream().map(this::toResponse).toList();
    }

    public EventResponse update(Long id, EventRequest request, String email) {
        Event event = eventRepository.findByIdAndUser(id, getUser(email))
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        return toResponse(eventRepository.save(event));
    }

    public void delete(Long id, String email) {
        Event event = eventRepository.findByIdAndUser(id, getUser(email))
                .orElseThrow(() -> new RuntimeException("Event not found"));
        eventRepository.delete(event);
    }
}
