package com.shadsluiter.eventsapp.service;

import com.shadsluiter.eventsapp.data.EventRepositoryInterface;
import com.shadsluiter.eventsapp.models.EventEntity;
import com.shadsluiter.eventsapp.models.EventModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


import java.sql.Date;
import java.text.SimpleDateFormat;

@Service
public class EventService {

    private final EventRepositoryInterface eventRepository;

    @Autowired
    public EventService(EventRepositoryInterface eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventModel> findAll() {
        List<EventEntity> eventEntities = eventRepository.findAll();
        return convertToModels(eventEntities);
    }

    public List<EventModel> findByOrganizerid(String organizerid) {
        List<EventEntity> eventEntities = eventRepository.findByOrganizerid(Long.valueOf(organizerid));
        return convertToModels(eventEntities);
    }

    public EventModel save(EventModel event) {
        EventEntity eventEntity = convertToEntity(event);
        EventEntity savedEvent = eventRepository.save(eventEntity);
        return convertToModel(savedEvent);
    }

    public void delete(String id) {
        eventRepository.deleteById(Long.valueOf(id));
    }

    public EventModel updateEvent(String id, EventModel event) {
        event.setId(id);
        return save(event);
    }

    public EventModel findById(String id) {
        EventEntity eventEntity = eventRepository.findById(Long.valueOf(id));
        return convertToModel(eventEntity);
    }

    private List<EventModel> convertToModels(List<EventEntity> eventEntities) {
        List<EventModel> eventModels = new ArrayList<>();
        for (EventEntity eventEntity : eventEntities) {
            eventModels.add(convertToModel(eventEntity));
        }
        return eventModels;
    }

    private EventModel convertToModel(EventEntity eventEntity) {
        return new EventModel(
                eventEntity.getId().toString(),
                eventEntity.getName(),
                eventEntity.getDate(),
                eventEntity.getLocation(),
                eventEntity.getOrganizerid(),
                eventEntity.getDescription()
        );
    }

    private EventEntity convertToEntity(EventModel eventModel) {
        EventEntity eventEntity = new EventEntity();
        if (eventModel.getId() != null) {
            eventEntity.setId(Long.parseLong(eventModel.getId()));
        }
        eventEntity.setName(eventModel.getName());
        
        // Format the date to 'YYYY-MM-DD'
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(eventModel.getDate());
        eventEntity.setDate(Date.valueOf(formattedDate)); // Use java.sql.Date

        eventEntity.setLocation(eventModel.getLocation());
        eventEntity.setOrganizerid(eventModel.getOrganizerid());
        eventEntity.setDescription(eventModel.getDescription());
        return eventEntity;
    }
    //' UNION SELECT 999, 999, '2024-01-01', login_name, password, 999 FROM users WHERE 'a'LIKE'a
    //' UNION SELECT * FROM cst407.events 'a' like 'a
    public List<EventModel> findByDescription(String searchString) {
        List<EventEntity> eventEntities = eventRepository.findByDescription(searchString);
        return convertToModels(eventEntities);  
    }
}
