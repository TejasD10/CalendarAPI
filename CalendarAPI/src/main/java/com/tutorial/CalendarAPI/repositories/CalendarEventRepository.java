package com.tutorial.CalendarAPI.repositories;

import com.tutorial.CalendarAPI.model.CalendarEvents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface CalendarEventRepository extends JpaRepository<CalendarEvents, Integer> {
    List<CalendarEvents> findByCalendarId(Integer id);
    List<CalendarEvents> findByEventDateBetween(Date startDate, Date endDate);
}

