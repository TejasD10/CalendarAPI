package com.tutorial.CalendarAPI.repositories;

import com.tutorial.CalendarAPI.model.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarRepository extends JpaRepository<Calendar, Integer> {
}
