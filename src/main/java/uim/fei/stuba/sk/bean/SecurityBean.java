package uim.fei.stuba.sk.bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uim.fei.stuba.sk.model.UserEntity;
import uim.fei.stuba.sk.security.SecurityUtil;
import uim.fei.stuba.sk.service.UserService;

import java.io.Serializable;

@Named("securityBean")
@Component
@SessionScoped
@Getter @Setter
public class SecurityBean implements Serializable {

    @Autowired
    private UserService userService;

    private UserEntity currentUser;

    public boolean isAuthenticated() {
        return SecurityUtil.getSessionUser() != null;
    }

    public String getUsername() {
        return SecurityUtil.getSessionUser();
    }

    public void clearCurrentUser() {
        currentUser = null;
    }

    public String logout() {
        try {
            clearCurrentUser();
            SecurityContextHolder.clearContext();

            // Invalidate JSF session
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            externalContext.invalidateSession();

            // Redirect to login with logout parameter
            return "/login.xhtml?faces-redirect=true&logout=true";

        } catch (Exception e) {
            // If redirect fails, try external redirect
            return "/login.xhtml?faces-redirect=true&logout=true";
        }
    }
}