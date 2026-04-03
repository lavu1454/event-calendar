package com.example.demo.repository;

import com.example.demo.entity.Event;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByUser(User user);
    Optional<Event> findByIdAndUser(Long id, User user);
}
