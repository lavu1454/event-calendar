package com.example.demo.repository;

import com.example.demo.entity.CalendarType;
import com.example.demo.entity.Event;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByUser(User user);

    Optional<Event> findByIdAndUser(Long id, User user);

    List<Event> findByUserAndStartBetween(User user, LocalDateTime start, LocalDateTime end);

    List<Event> findByUserAndCalendarType(User user, CalendarType calendarType);

    // Search by title OR description — category search removed (enum CAST is unreliable across DBs)
    // Use COALESCE to safely handle null description
    @Query("SELECT e FROM Event e WHERE e.user = :user " +
           "AND e.start BETWEEN :start AND :end " +
           "AND (:search IS NULL OR :search = '' OR " +
           "     LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "     LOWER(COALESCE(e.description, '')) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Event> searchByUserAndRange(@Param("user") User user,
                                     @Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end,
                                     @Param("search") String search);
}
