package uim.fei.stuba.sk.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uim.fei.stuba.sk.dto.EventDto;
import uim.fei.stuba.sk.model.UserEntity;
import uim.fei.stuba.sk.security.SecurityUtil;
import uim.fei.stuba.sk.service.EventService;
import uim.fei.stuba.sk.service.UserService;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Named
@Component
@ViewScoped
@Getter @Setter
public class EventBean implements Serializable {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    // Model attributes
    private EventDto event = new EventDto();
    private List<EventDto> events = new ArrayList<>();
    private UserEntity user = new UserEntity();
    private Long clubId;

    // Pagination
    private int currentPage = 0;
    private int pageSize = 2;
    private int totalPages = 0;
    private long totalItems = 0;
    private boolean hasNext = false;
    private boolean hasPrevious = false;

    // Elevation data
    private Double maxElevation;
    private List<Double> elevationList = new ArrayList<>();

    @PostConstruct
    public void init() {
        loadUser();

        // Get parameters from URL
        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();

        String eventIdParam = params.get("eventId");
        String clubIdParam = params.get("clubId");
        String pageParam = params.get("page");

        // Set pagination
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                currentPage = 0;
            }
        }

        // Set club ID for event creation
        if (clubIdParam != null && !clubIdParam.isEmpty()) {
            try {
                clubId = Long.parseLong(clubIdParam);
            } catch (NumberFormatException e) {
                clubId = null;
            }
        }

        // Load specific event if eventId parameter exists
        if (eventIdParam != null && !eventIdParam.isEmpty()) {
            try {
                Long eventId = Long.parseLong(eventIdParam);
                loadEventDetail(eventId);
            } catch (NumberFormatException e) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Invalid event ID");
            }
        } else {
            // Load events list for pagination
            loadEvents();
        }
    }

    // Navigation methods (converted from @GetMapping)
    public String goToEventsList() {
        currentPage = 0;
        loadEvents();
        return "/events-list.xhtml?faces-redirect=true";
    }

    public String goToEventDetail(Long eventId) {
        return "/events-detail.xhtml?faces-redirect=true&eventId=" + eventId;
    }

    public String goToCreateEvent(Long clubId) {
        this.clubId = clubId;
        event = new EventDto();
        return "/events-create.xhtml?faces-redirect=true&clubId=" + clubId;
    }

    public String goToEditEvent(Long eventId) {
        return "/events-edit.xhtml?faces-redirect=true&eventId=" + eventId;
    }

    // Action methods (converted from @PostMapping)
    public String createEvent() {
        try {
            if (clubId == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Club ID is required");
                return null;
            }

            // Validate required fields
            if (event.getName() == null || event.getName().trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Event name is required");
                return null;
            }

            if (event.getType() == null || event.getType().trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Event type is required");
                return null;
            }

            if (event.getStartTime() == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Start time is required");
                return null;
            }

            if (event.getEndTime() == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "End time is required");
                return null;
            }

            if (event.getPhotoUrl() == null || event.getPhotoUrl().trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Photo URL is required");
                return null;
            }

            eventService.createEvent(clubId, event);
            addMessage(FacesMessage.SEVERITY_INFO, "Event created successfully");
            return "/clubs-detail.xhtml?faces-redirect=true&clubId=" + clubId;

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error creating event: " + e.getMessage());
            return null;
        }
    }

    public String updateEvent() {
        try {
            if (event.getId() == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Event ID is required");
                return null;
            }

            // Validate required fields
            if (event.getName() == null || event.getName().trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Event name is required");
                return null;
            }

            if (event.getType() == null || event.getType().trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Event type is required");
                return null;
            }

            if (event.getStartTime() == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Start time is required");
                return null;
            }

            if (event.getEndTime() == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "End time is required");
                return null;
            }

            if (event.getPhotoUrl() == null || event.getPhotoUrl().trim().isEmpty()) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Photo URL is required");
                return null;
            }

            // Get original event to preserve club information
            EventDto originalEvent = eventService.findByEventId(event.getId());
            event.setClub(originalEvent.getClub());

            eventService.updateEvent(event);
            addMessage(FacesMessage.SEVERITY_INFO, "Event updated successfully");
            return "/events-detail.xhtml?faces-redirect=true&eventId=" + event.getId();

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error updating event: " + e.getMessage());
            return null;
        }
    }

    public String deleteEvent() {
        try {
            if (event.getId() == null) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Event ID is required");
                return null;
            }

            eventService.deleteEvent(event.getId());
            addMessage(FacesMessage.SEVERITY_INFO, "Event deleted successfully");
            return "/events-list.xhtml?faces-redirect=true";

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error deleting event: " + e.getMessage());
            return null;
        }
    }

    // Pagination methods
    public String nextPage() {
        if (hasNext) {
            currentPage++;
            loadEvents();
        }
        return null;
    }

    public String previousPage() {
        if (hasPrevious) {
            currentPage--;
            loadEvents();
        }
        return null;
    }

    public String goToPage(int pageNumber) {
        if (pageNumber >= 0 && pageNumber < totalPages) {
            currentPage = pageNumber;
            loadEvents();
//            updateUrl();
        }
        return null;
    }

    public String goToFirstPage() {
        currentPage = 0;
        loadEvents();
        return null;
    }

    public String goToLastPage() {
        currentPage = totalPages - 1;
        loadEvents();
        return null;
    }

//    private void updateUrl() {
//        FacesContext context = FacesContext.getCurrentInstance();
//        ExternalContext externalContext = context.getExternalContext();
//        try {
//            String url = "events-list.xhtml";
//            if (currentPage > 0) {
//                url += "?page=" + currentPage;
//            }
//            externalContext.redirect(url);
//        } catch (Exception e) {
//            System.err.println("Error updating URL: " + e.getMessage());
//        }
//    }

    // Data loading methods
    private void loadUser() {
        String username = SecurityUtil.getSessionUser();
        if (username != null) {
            user = userService.findByUsername(username);
        } else {
            user = new UserEntity();
        }
    }

    private void loadEvents() {
        Page<EventDto> eventsPage = eventService.findAllEvents(currentPage, pageSize);

        events = eventsPage.getContent();
        totalPages = eventsPage.getTotalPages();
        totalItems = eventsPage.getTotalElements();
        hasNext = eventsPage.hasNext();
        hasPrevious = eventsPage.hasPrevious();
    }

    private void loadEventDetail(Long eventId) {
        try {
            event = eventService.findByEventId(eventId);
            calculateElevationData();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Event not found");
        }
    }

    private void calculateElevationData() {
        maxElevation = null;
        elevationList.clear();

        String elevationData = event.getElevationData();

        if (elevationData != null && !elevationData.trim().isEmpty()) {
            try {
                String[] elevations = elevationData.split(",");
                double max = 0;

                for (String elevation : elevations) {
                    try {
                        double val = Double.parseDouble(elevation.trim());
                        elevationList.add(val);
                        if (val > max) {
                            max = val;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid elevation value: " + elevation);
                    }
                }

                if (max > 0) {
                    maxElevation = max;
                }
            } catch (Exception e) {
                System.out.println("Error processing elevation data: " + e.getMessage());
            }
        }
    }

    // Utility methods
    public String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return dateTime.format(formatter);
    }

    public String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return dateTime.format(formatter);
    }

    // Add this method to handle conversion issues
    public String getFormattedCreatedOn(EventDto event) {
        if (event == null || event.getCreatedOn() == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return event.getCreatedOn().format(formatter);
    }

    public Double getElevationPercentage(Double elevation) {
        if (elevation == null || maxElevation == null || maxElevation == 0) {
            return 0.0;
        }
        return (elevation / maxElevation) * 100;
    }

    public List<Integer> getVisiblePages() {
        List<Integer> pageNumbers = new ArrayList<>();
        int start = Math.max(0, currentPage - 2);
        int end = Math.min(totalPages - 1, currentPage + 2);

        for (int i = start; i <= end; i++) {
            pageNumbers.add(i);
        }
        return pageNumbers;
    }

    // Helper method for adding faces messages
    private void addMessage(FacesMessage.Severity severity, String summary) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(severity, summary, null));
    }

    // Reset method for form cleanup
    public void resetForm() {
        event = new EventDto();
        clubId = null;
        maxElevation = null;
        elevationList.clear();
    }

    // Validation methods
    public boolean isOwner(EventDto eventDto) {
        return user != null && eventDto != null &&
               eventDto.getClub() != null &&
               eventDto.getClub().getCreatedBy() != null &&
               user.getId().equals(eventDto.getClub().getCreatedBy().getId());
    }

    public boolean hasEvents() {
        return events != null && !events.isEmpty();
    }

    public boolean hasElevationData() {
        return elevationList != null && !elevationList.isEmpty() &&
               maxElevation != null && maxElevation > 0;
    }

    // Getters for JSF EL
    public boolean isHasNext() {
        return hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    // Pridaj do EventBean.java
    public String getFormattedStartTime() {
        if (event.getStartTime() == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return event.getStartTime().format(formatter);
    }

    public void setFormattedStartTime(String formattedStartTime) {
        if (formattedStartTime != null && !formattedStartTime.trim().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            event.setStartTime(LocalDateTime.parse(formattedStartTime, formatter));
        }
    }

    public String getFormattedEndTime() {
        if (event.getEndTime() == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return event.getEndTime().format(formatter);
    }

    public void setFormattedEndTime(String formattedEndTime) {
        if (formattedEndTime != null && !formattedEndTime.trim().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            event.setEndTime(LocalDateTime.parse(formattedEndTime, formatter));
        }
    }
}