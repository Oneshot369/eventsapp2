package com.shadsluiter.eventsapp.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shadsluiter.eventsapp.models.EventModel;
import com.shadsluiter.eventsapp.models.UserModel;
import com.shadsluiter.eventsapp.service.EventService;
import com.shadsluiter.eventsapp.service.UserService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/events")
public class EventApiController {

    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;

    @GetMapping("/getAll")
    public ResponseEntity<?> registerUser(Authentication auth) {
        List<EventModel> events = eventService.findAll();
        
        return ResponseEntity.ok(events);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@RequestBody EventModel event, Authentication auth) {
        try{
            //get our user from db
            User user = (User) auth.getPrincipal();
            UserModel existingUser = userService.findByLoginName(user.getUsername());
            //set id of our user
            event.setOrganizerid(existingUser.getId());
            EventModel newEvent = eventService.save(event);
            return ResponseEntity.ok("Created event with ID: " + newEvent.getId());
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body("no good: \n" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable String id, @RequestBody EventModel entity, Authentication auth) {
        try{
            //get our user from db
            User user = (User) auth.getPrincipal();
            UserModel existingUser = userService.findByLoginName(user.getUsername());
            //set id of our user
            entity.setOrganizerid(existingUser.getId());
            eventService.updateEvent(id, entity);
            return ResponseEntity.ok("Updated event");
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body("no good:\n" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable String id){
        try{
            eventService.delete(id);
            return ResponseEntity.ok("Deleted event");
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body("no good:\n" + e.getMessage());
        }
       
    }

}
