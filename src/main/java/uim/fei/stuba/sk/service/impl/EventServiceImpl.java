package uim.fei.stuba.sk.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uim.fei.stuba.sk.dto.EventDto;
import uim.fei.stuba.sk.mapper.EventMapper;
import uim.fei.stuba.sk.model.Club;
import uim.fei.stuba.sk.model.Event;
import uim.fei.stuba.sk.repository.ClubRepository;
import uim.fei.stuba.sk.repository.EventRepository;
import uim.fei.stuba.sk.service.EventService;
import java.util.List;
import java.util.stream.Collectors;
import static uim.fei.stuba.sk.mapper.EventMapper.mapToEvent;
import static uim.fei.stuba.sk.mapper.EventMapper.mapToEventDto;

@Service
public class EventServiceImpl implements EventService {

    private EventRepository eventRepository;
    private ClubRepository clubRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, ClubRepository clubRepository ) {
        this.eventRepository = eventRepository;
        this.clubRepository = clubRepository;

    }

    @Override
    public void createEvent(Long clubId, EventDto eventDto) {
        Club club = clubRepository.findById(clubId).get();
        Event event = mapToEvent(eventDto);
        event.setClub(club);
        eventRepository.save(event);

    }

    @Override
    public Page<EventDto> findAllEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventsPage = eventRepository.findAll(pageable);
        return eventsPage.map(EventMapper::mapToEventDto);
    }

    @Override
    public List<EventDto> findAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream().map(event -> mapToEventDto(event)).collect(Collectors.toList());
    }

    @Override
    public EventDto findByEventId(Long eventId) {
        Event event = eventRepository.findById(eventId).get();
        return mapToEventDto(event);
    }

    @Override
    public void updateEvent(EventDto eventDto) {
        Event event = mapToEvent(eventDto);
        eventRepository.save(event);

    }

    @Override
    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    @Override
    public List<EventDto> searchEvents(String query) {
        List<Event> events = eventRepository.searchEvents(query);
        return events.stream().map(EventMapper::mapToEventDto).collect(Collectors.toList());
    }

    @Override
    public Page<EventDto> findEventsByClubId(Long clubId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventsPage = eventRepository.findByClubId(clubId, pageable);
        return eventsPage.map(EventMapper::mapToEventDto);
    }


}

