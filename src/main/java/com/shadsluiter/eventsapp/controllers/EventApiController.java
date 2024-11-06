package com.shadsluiter.eventsapp.controllers;

import java.util.List;

import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shadsluiter.eventsapp.models.EventModel;
import com.shadsluiter.eventsapp.models.EventSearch;
import com.shadsluiter.eventsapp.service.EventService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/events")
public class EventApiController {

    @Autowired
    private EventService eventService;

    @GetMapping("/getAll")
    public ResponseEntity<?> registerUser(Authentication auth) {
        List<EventModel> events = eventService.findAll();
        
        return ResponseEntity.ok(events);
    }
}
