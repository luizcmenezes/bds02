package com.devsuperior.bds02.services;

import com.devsuperior.bds02.dto.CityDTO;
import com.devsuperior.bds02.dto.EventDTO;
import com.devsuperior.bds02.entities.City;
import com.devsuperior.bds02.entities.Event;
import com.devsuperior.bds02.repositories.CityRepository;
import com.devsuperior.bds02.repositories.EventRepository;
import com.devsuperior.bds02.services.execptions.DatabaseException;
import com.devsuperior.bds02.services.execptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    EventRepository repo;

    @Transactional(readOnly = true)
    public List<EventDTO> findAll(){
        List<Event> list = repo.findAll(Sort.by("name"));
        return list.stream().map(EventDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public EventDTO insert(EventDTO dto) {
        Event event = getEvent(dto);
        return new EventDTO(event);
    }

    @Transactional
    public EventDTO update(Long id, EventDTO dto) {
        try {
            Event event = repo.findById(id).get();
            dto.setId(event.getId());
            event = getEvent(dto);
            return new EventDTO(event);
        } catch (NoSuchElementException e){
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    private Event getEvent(EventDTO dto) {
        Event event = new Event();
        event.setId(dto.getId());
        event.setName(dto.getName());
        event.setDate(dto.getDate());
        event.setUrl(dto.getUrl());
        event.setCity(new City(dto.getCityId(), null));
        event = repo.save(event);
        return event;
    }

    public void delete(Long id) {
        try {
            repo.deleteById(id);
        } catch (EmptyResultDataAccessException e){
            throw new ResourceNotFoundException("Id not found " + id);
        } catch (DataIntegrityViolationException e){
            throw new DatabaseException("Integrity violation");
        }
    }
}
