package com.tutorial.CalendarAPI.controllers;

import com.tutorial.CalendarAPI.model.Calendar;
import com.tutorial.CalendarAPI.model.CalendarEvents;
import com.tutorial.CalendarAPI.repositories.CalendarEventRepository;
import com.tutorial.CalendarAPI.repositories.CalendarRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/v1/calendars")
public class CalendarController {

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private CalendarEventRepository calendarEventRepository;

    private Logger log = LogManager.getLogger(CalendarController.class);

    @ApiOperation(value = "Get All Calendars", notes = "Get All existing calendars from the database", response = Calendar.class, responseContainer = "List" )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Operation", response = Calendar.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "No Calendars available", response = Calendar.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Internal server Error") })
    @GetMapping
    public ResponseEntity<?> getCalendars() {
        List<Calendar> returnList = calendarRepository.findAll();
        //Return a 404 for an empty list
        if (returnList == null || returnList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(returnList, HttpStatus.OK);
    }

    @ApiOperation(value = "Create a Calendar", notes = "Create a Calendar with events", response = Calendar.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful Operation", response = Calendar.class),
            @ApiResponse(code = 500, message = "Internal server Error") })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addCalendar(@ApiParam(name = "Calendar") @Valid @RequestBody Calendar calendar) {
        Calendar cal = calendarRepository.save(calendar);
        /*
         * Using a {@link CopyOnWriteArrayList } as there are concurrent modifications happening to the
         * event (event and eventFound objects are the same)
         */
        CopyOnWriteArrayList<CalendarEvents> calEvents = new CopyOnWriteArrayList<>(cal.getEvents());
        for(CalendarEvents event: calEvents){
            //Update the association - Associate the event with the calendar parent object.
            CalendarEvents eventFound = calendarEventRepository.findOne(event.getId());
            eventFound.setCalendar(cal);
            log.info("EventFound: " + eventFound);
            calendarEventRepository.save(eventFound);
        }
        return new ResponseEntity<>(cal,HttpStatus.CREATED);
    }
    @ApiOperation(value = "Get a Calendar by ID", notes = "Get a Calendar by ID", response = Calendar.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = Calendar.class),
            @ApiResponse(code = 404, message = "Not Found" ),
            @ApiResponse(code = 500, message = "Internal server Error") })
    @GetMapping("/{id}")
    public ResponseEntity<?> getCalendarById(@PathVariable Integer id) {
        Calendar cal = calendarRepository.findOne(id);
        //Return a 404 for a non-existent calendar
        if (cal == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(cal, HttpStatus.OK);
    }
    @ApiOperation(value = "Create an event for a Calendar ID", notes = "Create an event for a Calendar ID", response = Calendar.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success", response = CalendarEvents.class),
            @ApiResponse(code = 500, message = "Internal server Error")})
    @PostMapping(path = "{id}/events",consumes=MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createEvent(@PathVariable(name = "id") Integer calendarId, @RequestBody CalendarEvents calEvent){
        //Validate if the Calendar is present
        Calendar cal = calendarRepository.findOne(calendarId);

        if(cal == null){
            //Log and return a BAD_REQUEST
            log.info("Calendar " + calendarId + " Not Found");
            return new ResponseEntity<>("Calendar" + calendarId + "  not found", HttpStatus.BAD_REQUEST);
        }
        // Associate the Calendar with the Event
        calEvent.setCalendar(cal);
        calendarEventRepository.save(calEvent);
        return new ResponseEntity<>(calEvent, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Get all events for a Calendar", notes = "Get all events for a Calendar", response = CalendarEvents.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = CalendarEvents.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Internal server Error")})
    @GetMapping(value = "/{id}/events", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCalenderEventsById(@PathVariable(name = "id") Integer id){
        List<CalendarEvents> returnList = calendarEventRepository.findByCalendarId(id);
        //Return a 404 for an empty list
        if (returnList == null || returnList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(returnList, HttpStatus.OK);
    }

    /**
     * Update a Calendar with details
     */
    @ApiOperation(value = "Update a Calendar", notes = "Update a Calendar. This API will not update the Calendar Events")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success"),
            @ApiResponse(code = 400, message = "Validation Error"),
            @ApiResponse(code = 404, message = "Calendar Not Found"),
            @ApiResponse(code = 500, message = "Internal server Error")})
    @PutMapping(path = "/{id}")
    public ResponseEntity<?> updateCalendar(@PathVariable(name = "id") Integer id, @Valid @RequestBody Calendar cal){
        Calendar calendar = calendarRepository.findOne(id);
        if(calendar == null){
            //Return 404 as there is no calendar with the ID
            log.warn("No Calendar found with ID: ", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //Update the calendar
        calendar.setName(cal.getName());
        calendarRepository.save(calendar);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Updating events in a Calendar
     */
    @ApiOperation(value = "Update a Calendar Event", notes = "Update a Calendar Event")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success"),
            @ApiResponse(code = 400, message = "Validation Error"),
            @ApiResponse(code = 404, message = "Calendar Not Found"),
            @ApiResponse(code = 500, message = "Internal server Error")})
    @PutMapping(path = "/{calId}/events/{eventId}")
    public ResponseEntity<?> updateCalendarEvent(@PathVariable(name = "calId") Integer calId,
                                                 @PathVariable(name = "eventId") Integer eventId,
                                                 @Valid @RequestBody CalendarEvents calEvent) {
        // Check if the Calendar with the passed in CalendarID exists
        Calendar cal = calendarRepository.findOne(calId);
        if (cal == null){
            //Return 404 if the calendar is not found
            log.warn("No Calendar Found with calendar id: ", calId);
            return new ResponseEntity<>("No Calendar Found with the calendar id: "+ calId, HttpStatus.NOT_FOUND);
        }
        // Check if there is an event with the Calendar ID
        CalendarEvents event = calendarEventRepository.findOne(eventId);
        if (event == null){
            //Return 404 if the calendar is not found
            log.warn("No Calendar event Found with event id: ", eventId);
            return new ResponseEntity<>("No Calendar event Found with event id: " + eventId, HttpStatus.NOT_FOUND);
        }
        // Check if the event is in association with the calendar
        if(event.getCalendar().getId() == calId){
            //Go ahead, update the event
            event.setTitle(calEvent.getTitle());
            event.setAttendeeList(calEvent.getAttendeeList());
            event.setEventDate(calEvent.getEventDate());
            event.setLocation(calEvent.getLocation());
            event.setReminderTime(calEvent.getReminderTime());
            calendarEventRepository.save(event);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        // Calendar is not associated with the event, send 400
        log.warn("Calendar " + calId + " not associated with the event " + eventId);
        return new ResponseEntity<>("Calendar " + calId + " not associated with the event " + eventId, HttpStatus.BAD_REQUEST);
    }

    /**
     * Delete a Calendar
     * @param calId - Calendar ID to be deleted
     * @return No Content
     */
    @ApiOperation(value = "Delete a Calendar", notes = "Delete a Calendar")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success"),
            @ApiResponse(code = 404, message = "Calendar Not Found"),
            @ApiResponse(code = 500, message = "Internal server Error")})
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteCalendar(@PathVariable(name = "id") Integer calId){
        //Check if the calendar exists
        Calendar cal = calendarRepository.findOne(calId);
        if(cal == null){
            //No Calendar found for deletion
            log.warn("No Calendar with " + calId +" found for deletion");
            return new ResponseEntity<>("No Calendar with " + calId +" found for deletion", HttpStatus.NOT_FOUND);
        }
        //Delete the calendar
        calendarRepository.delete(calId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     *
     * @param calId - Calendar ID
     * @param eventId - Event ID
     * @return No Content
     */
    @ApiOperation(value = "Delete a Calendar event", notes = "Delete a Calendar event")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success"),
            @ApiResponse(code = 404, message = "Calendar Not Found"),
            @ApiResponse(code = 500, message = "Internal server Error")})
    @DeleteMapping(path = "/{calId}/events/{eventID}")
    public ResponseEntity<?> deleteEvent(@PathVariable(name = "calId") Integer calId,
                                         @PathVariable(name = "eventId") Integer eventId) {
        // Check if the Calendar with the passed in CalendarID exists
        Calendar cal = calendarRepository.findOne(calId);
        if (cal == null){
            //Return 404 if the calendar is not found
            log.warn("No Calendar Found with calendar id: ", calId);
            return new ResponseEntity<>("No Calendar Found with the calendar id: "+ calId, HttpStatus.NOT_FOUND);
        }
        // Check if there is an event with the Calendar ID
        CalendarEvents event = calendarEventRepository.findOne(eventId);
        if (event == null){
            //Return 404 if the calendar is not found
            log.warn("No Calendar event Found with event id: ", eventId);
            return new ResponseEntity<>("No Calendar event Found with event id: " + eventId, HttpStatus.NOT_FOUND);
        }
        //Delete the event
        calendarEventRepository.delete(eventId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Get events for the day
     */
    @GetMapping(value = "/{id}/events/day", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getEventsforDay(@PathVariable(name = "id") Integer calId) {
        // Check if the Calendar with the passed in CalendarID exists
        Calendar calendar = calendarRepository.findOne(calId);
        if (calendar == null){
            //Return 404 if the calendar is not found
            log.warn("No Calendar Found with calendar id: ", calId);
            return new ResponseEntity<>("No Calendar Found with the calendar id: "+ calId, HttpStatus.NOT_FOUND);
        }
        // Get the events for the day
        java.util.Calendar cal = java.util.Calendar.getInstance(Locale.US);

        String day = cal.get(java.util.Calendar.MONTH) + "/" + cal.get(java.util.Calendar.DAY_OF_MONTH) + "/" + cal.get(java.util.Calendar.YEAR);
        SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        List<CalendarEvents> events = null;

        try {
            Date startDate = date.parse(day + " 00:00:00");
            Date endDate = date.parse(day + " 23:59:59");
            log.info("getEventsforDay() : start Date : " + startDate + ", endDate = " + endDate);
            events = calendarEventRepository.findByEventDateBetween(startDate, endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (events == null || events.isEmpty()){
            return new ResponseEntity<>("No Events Found for the day", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(events, HttpStatus.OK);
    }
}
