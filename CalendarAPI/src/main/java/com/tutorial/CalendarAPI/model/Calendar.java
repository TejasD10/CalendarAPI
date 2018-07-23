package com.tutorial.CalendarAPI.model;

import io.swagger.annotations.Api;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Entity
@Table(name = "CALENDAR")
@Api (tags = "Calendar API")
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CAL_ID")
    private Integer id;

    @NotNull
    @Size(max = 255)
    private String name;

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL)
    private List<CalendarEvents> events = new CopyOnWriteArrayList<>();

    public Calendar() {
    }

    public List<CalendarEvents> getEvents() {
        return events;
    }

    public void setEvents(List<CalendarEvents> events) {
        this.events = events;
    }

    public void addToEvents(CalendarEvents event){
        this.events.add(event);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Calendar{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", events=" + events +
                '}';
    }
}
