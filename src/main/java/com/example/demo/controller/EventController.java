package com.example.demo.controller;

import com.example.demo.dto.EventRequest;
import com.example.demo.service.EventService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody EventRequest request,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        var created = eventService.create(request, userDetails.getUsername());
        return ResponseEntity.created(URI.create("/api/events/" + created.getId())).body(created);
    }

    @GetMapping
    public ResponseEntity<?> getEvents(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        if (start != null && end != null) {
            return ResponseEntity.ok(eventService.getByRange(email, start, end, search));
        }
        return ResponseEntity.ok(eventService.getAll(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(eventService.getById(id, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody EventRequest request,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(eventService.update(id, request, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        eventService.delete(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
