package com.tutorial.CalendarAPI.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="CALENDAR_EVENTS")
public class CalendarEvents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVENT_ID")
    private Integer id;
    private String title;
    @JsonFormat(pattern = "MM/dd/yyyy hh:mm:ss")
    private Date eventDate;
    private String location;
    @ElementCollection
    private Set<String> attendeeList = new HashSet<>();
    private Date reminderTime;
    private boolean reminderSent;

    @ManyToOne (fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "CAL_ID")
    @JsonBackReference
    private Calendar calendar;

    public CalendarEvents() {
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Set<String> getAttendeeList() {
        return attendeeList;
    }

    public void setAttendeeList(Set<String> attendeeList) {
        this.attendeeList = attendeeList;
    }

    public Date getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(Date reminderTime) {
        this.reminderTime = reminderTime;
    }

    public boolean isReminderSent() {
        return reminderSent;
    }

    public void setReminderSent(boolean reminderSent) {
        this.reminderSent = reminderSent;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CalendarEvents{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", eventDate=" + eventDate +
                ", location='" + location + '\'' +
                ", attendeeList=" + attendeeList +
                ", reminderTime=" + reminderTime +
                ", reminderSent=" + reminderSent +
                '}';
    }
}
