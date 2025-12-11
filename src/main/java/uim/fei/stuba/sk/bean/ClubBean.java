package uim.fei.stuba.sk.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uim.fei.stuba.sk.dto.ClubDto;
import uim.fei.stuba.sk.dto.EventDto;
import uim.fei.stuba.sk.model.UserEntity;
import uim.fei.stuba.sk.security.SecurityUtil;
import uim.fei.stuba.sk.service.ClubService;
import uim.fei.stuba.sk.service.EventService;
import uim.fei.stuba.sk.service.UserService;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named
@Component
@ViewScoped
@Getter @Setter
public class ClubBean implements Serializable {

    @Autowired
    private ClubService clubService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    // Model attributes
    private ClubDto clubDto = new ClubDto();
    private List<ClubDto> clubs = new ArrayList<>();
    private List<EventDto> events = new ArrayList<>();
    private List<EventDto> searchEvents = new ArrayList<>();
    private UserEntity user = new UserEntity();

    // Pagination
    private int currentPage = 0;
    private int pageSize = 3;
    private int totalPages = 0;
    private long totalItems = 0;
    private boolean hasNext = false;
    private boolean hasPrevious = false;

    // Event pagination
    private int currentEventPage = 0;
    private int eventPageSize = 2;
    private int totalEventPages = 0;
    private long totalEventItems = 0;
    private boolean hasNextEvent = false;
    private boolean hasPreviousEvent = false;

    // Search
    private String searchQuery = "";

    @PostConstruct
    public void init() {
        loadUser();

        Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();

        String clubIdParam = params.get("clubId");
        String pageParam = params.get("page");
        String searchParam = params.get("search");

        // Handle search parameter for search-results page
        if (searchParam != null) {
            searchQuery = searchParam;
            performSearchInternal();
            return; // Don't load other data if this is search results
        }

        // Set pagination
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                currentPage = 0;
            }
        }

        if (clubIdParam != null && !clubIdParam.isEmpty()) {
            try {
                Long clubId = Long.parseLong(clubIdParam);
                loadClubDetail(clubId);
                loadEventsForClub(clubId);
            } catch (NumberFormatException e) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Invalid club ID");
            }
        } else {
            loadClubs();
        }
    }

    // Navigation methods (converted from @GetMapping)
    public String goToClubsList() {
        loadClubs();
        return "/clubs-list.xhtml?faces-redirect=true";
    }

    public String goToClubDetail(Long clubId) {
        loadClubDetail(clubId);
        return "/clubs-detail.xhtml?faces-redirect=true&clubId=" + clubId;
    }

    public String goToCreateClub() {
        clubDto = new ClubDto();
        return "/clubs-create.xhtml?faces-redirect=true";
    }

    public String goToEditClub(Long clubId) {
        clubDto = clubService.findClubById(clubId);
        return "/clubs-edit.xhtml?faces-redirect=true&clubId=" + clubId;
    }

    // Action methods (converted from @PostMapping)
    public String saveClub() {
        try {
            clubService.saveClub(clubDto);
            addMessage(FacesMessage.SEVERITY_INFO, "Club created successfully!");
            return "/clubs-list.xhtml?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error creating club: " + e.getMessage());
            return null; // Stay on current page
        }
    }

    public String updateClub() {
        try {
            clubService.updateClub(clubDto);
            addMessage(FacesMessage.SEVERITY_INFO, "Club updated successfully!");
            return "/clubs-detail.xhtml?faces-redirect=true&clubId=" + clubDto.getId();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error updating club: " + e.getMessage());
            return null; // Stay on current page
        }
    }

    public String deleteClub(Long clubId) {
        try {
            clubService.delete(clubId);
            addMessage(FacesMessage.SEVERITY_INFO, "Club deleted successfully!");
            return "/clubs-list.xhtml?faces-redirect=true";
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error deleting club: " + e.getMessage());
            return null;
        }
    }

    public String search() {
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            clubs = clubService.searchClubs(searchQuery.trim());
            // Reset pagination for search results
            currentPage = 0;
            totalPages = 1;
            totalItems = clubs.size();
            hasNext = false;
            hasPrevious = false;
        } else {
            loadClubs();
        }
        return null; // Stay on current page
    }

    // Pagination methods
    public String nextPage() {
        if (hasNext) {
            currentPage++;
            loadClubs();
        }
        return null;
    }

    public String previousPage() {
        if (hasPrevious) {
            currentPage--;
            loadClubs();
        }
        return null;
    }

    public String goToPage(int pageNumber) {
        if (pageNumber >= 0 && pageNumber < totalPages) {
            currentPage = pageNumber;
            loadClubs();
        }
        return null;
    }

    // Event pagination methods
    public String nextEventPage() {
        if (hasNextEvent) {
            currentEventPage++;
            loadEventsForCurrentClub();
        }
        return null;
    }

    public String previousEventPage() {
        if (hasPreviousEvent) {
            currentEventPage--;
            loadEventsForCurrentClub();
        }
        return null;
    }

    public String goToFirstPage() {
        currentPage = 0;
        loadClubs();
        return null;
    }

    public String goToLastPage() {
        currentPage = totalPages - 1;
        loadClubs();
        return null;
    }

    // Data loading methods
    private void loadUser() {
        String username = SecurityUtil.getSessionUser();
        if (username != null) {
            user = userService.findByUsername(username);
        } else {
            user = new UserEntity();
        }
    }

    private void loadClubs() {
        Page<ClubDto> clubsPage = clubService.findAllClubs(currentPage, pageSize);

        clubs = clubsPage.getContent();
        totalPages = clubsPage.getTotalPages();
        totalItems = clubsPage.getTotalElements();
        hasNext = clubsPage.hasNext();
        hasPrevious = clubsPage.hasPrevious();
    }

    private void loadClubDetail(Long clubId) {
        clubDto = clubService.findClubById(clubId);
        loadEventsForClub(clubId);
    }

    private void loadEventsForClub(Long clubId) {
        Page<EventDto> eventsPage = eventService.findEventsByClubId(clubId, currentEventPage, eventPageSize);

        events = eventsPage.getContent();
        totalEventPages = eventsPage.getTotalPages();
        totalEventItems = eventsPage.getTotalElements();
        hasNextEvent = eventsPage.hasNext();
        hasPreviousEvent = eventsPage.hasPrevious();
    }

    private void loadEventsForCurrentClub() {
        if (clubDto != null && clubDto.getId() != null) {
            loadEventsForClub(clubDto.getId());
        }
    }

    // Utility methods - OPRAVENÉ: len jedna metóda pre každý typ
    public String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return dateTime.format(formatter);
    }

    public String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return dateTime.format(formatter);
    }

    public String abbreviate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    public List<Integer> getPageNumbers() {
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
        clubDto = new ClubDto();
        searchQuery = "";
    }

    // Validation methods
    public boolean isOwner(ClubDto club) {
        return user != null && club != null && club.getCreatedBy() != null &&
               user.getId() != null && user.getId().equals(club.getCreatedBy().getId());
    }

    public boolean hasClubs() {
        return clubs != null && !clubs.isEmpty();
    }

    public boolean hasEvents() {
        return events != null && !events.isEmpty();
    }
    public String performSearch() {
        // Always redirect to search results, even with empty query
        String query = (searchQuery != null && !searchQuery.trim().isEmpty()) ? searchQuery.trim() : "";
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

        // Perform search before redirect to populate data
        performSearchInternal();

        return "/search-results.xhtml?faces-redirect=true&search=" + encodedQuery;
    }

    private void performSearchInternal() {
        System.out.println("=== SEARCH DEBUG ===");
        System.out.println("Search query: '" + searchQuery + "'");

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String query = searchQuery.trim();
            clubs = clubService.searchClubs(query);
            searchEvents = eventService.searchEvents(query);
            System.out.println("Found " + clubs.size() + " clubs with query");
            System.out.println("Found " + searchEvents.size() + " events with query");
        } else {
            // If no search query, show all clubs and events
            System.out.println("Empty search query, loading all clubs and events");
            clubs = clubService.findAllClubs();
            searchEvents = eventService.findAllEvents();
            System.out.println("Loaded " + clubs.size() + " clubs (all)");
            System.out.println("Loaded " + searchEvents.size() + " events (all)");
        }

        // Reset pagination
        currentPage = 0;
        totalPages = 1;
        totalItems = clubs.size();
        hasNext = false;
        hasPrevious = false;

        System.out.println("Final results - Clubs: " + clubs.size() + ", Events: " + searchEvents.size());
        System.out.println("=== END SEARCH DEBUG ===");
    }


    public List<EventDto> getSearchEvents() {
        return searchEvents;
    }

    // Event pagination method
    public String goToEventPage(int pageNumber) {
        if (pageNumber >= 0 && pageNumber < totalEventPages) {
            currentEventPage = pageNumber;
            loadEventsForCurrentClub();
        }
        return null;
    }

    // Get visible event page numbers
    public List<Integer> getVisibleEventPages() {
        List<Integer> pageNumbers = new ArrayList<>();
        int start = Math.max(0, currentEventPage - 2);
        int end = Math.min(totalEventPages - 1, currentEventPage + 2);

        for (int i = start; i <= end; i++) {
            pageNumbers.add(i);
        }
        return pageNumbers;
    }

    // Getters for JSF EL
    public boolean isHasNextEvent() {
        return hasNextEvent;
    }

    public boolean isHasPreviousEvent() {
        return hasPreviousEvent;
    }

}