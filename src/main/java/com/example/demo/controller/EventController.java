package com.example.demo.controller;

import com.example.demo.dto.EventRequest;
import com.example.demo.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody EventRequest request,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(eventService.create(request, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<?> getAll(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(eventService.getAll(userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody EventRequest request,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        try {
            return ResponseEntity.ok(eventService.update(id, request, userDetails.getUsername()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        try {
            eventService.delete(id, userDetails.getUsername());
            return ResponseEntity.ok("Event deleted");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
