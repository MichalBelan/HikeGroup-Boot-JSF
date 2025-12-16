package uim.fei.stuba.sk.service;

import org.springframework.data.domain.Page;
import uim.fei.stuba.sk.dto.EventDto;
import java.util.List;

public interface EventService {
    void createEvent(Long clubId, EventDto eventDto);
    Page<EventDto> findAllEvents(int page, int size);
    Page<EventDto> findEventsByClubId(Long clubId, int page, int size);
    List<EventDto> findAllEvents();
    EventDto findByEventId(Long eventId);
    void updateEvent( EventDto eventDto);
    void deleteEvent(Long eventId);
    List<EventDto> searchEvents(String query);

}
