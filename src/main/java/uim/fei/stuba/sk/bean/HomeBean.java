package uim.fei.stuba.sk.bean;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Named("homeBean")
@Component
@RequestScoped
public class HomeBean {

    private String searchQuery = "";

    @PostConstruct
    public void init() {
        // Metóda sa vykoná pri inicializácii beanu
        // Môžeš tu pridať inicializačnú logiku ak potrebuješ
    }

    // Navigation method (converted from @GetMapping)
    public String goHome() {
        return "/home.xhtml?faces-redirect=true";
    }

    // Search functionality
    public String search() {
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String encodedQuery = URLEncoder.encode(searchQuery.trim(), StandardCharsets.UTF_8);
            return "/search-results.xhtml?faces-redirect=true&search=" + encodedQuery;
        }
        return null; // Stay on current page if search is empty
    }

    // Alternative search method using redirect
    public void performSearch() {
        try {
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String encodedQuery = URLEncoder.encode(searchQuery.trim(), StandardCharsets.UTF_8);

                FacesContext.getCurrentInstance().getExternalContext()
                    .redirect("/search?search=" + encodedQuery);
            }
        } catch (IOException e) {
            // Handle redirect error
            System.err.println("Error redirecting to search: " + e.getMessage());
        }
    }

    // Navigation helpers for home page links
    public String goToClubs() {
        return "/clubs-list.xhtml?faces-redirect=true";
    }

    public String goToEvents() {
        return "/events-list.xhtml?faces-redirect=true";
    }

    // Getter and setter for search query
    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    // Reset search form
    public void resetSearch() {
        searchQuery = "";
    }
}