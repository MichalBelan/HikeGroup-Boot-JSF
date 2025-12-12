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

    // Search functionality
    public String search() {
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String encodedQuery = URLEncoder.encode(searchQuery.trim(), StandardCharsets.UTF_8);
            return "/search-results.xhtml?faces-redirect=true&search=" + encodedQuery;
        }
        return null;
    }

}