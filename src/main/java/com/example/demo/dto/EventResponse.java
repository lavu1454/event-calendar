package com.example.demo.dto;

import com.example.demo.entity.Category;
import com.example.demo.entity.CalendarType;
import java.time.LocalDateTime;

public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String calendarType;
    private String start;
    private String end;

    public EventResponse(Long id, String title, String description,
                         Category category, CalendarType calendarType,
                         LocalDateTime start, LocalDateTime end) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category != null ? category.name() : null;
        this.calendarType = calendarType != null ? calendarType.name() : null;
        this.start = start != null ? start.toString() : null;
        this.end = end != null ? end.toString() : null;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getCalendarType() { return calendarType; }
    public String getStart() { return start; }
    public String getEnd() { return end; }
}
