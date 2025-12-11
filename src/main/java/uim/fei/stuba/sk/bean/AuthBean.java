package uim.fei.stuba.sk.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uim.fei.stuba.sk.dto.RegistrationDto;
import uim.fei.stuba.sk.model.UserEntity;
import uim.fei.stuba.sk.service.UserService;

@Named
@Component
@RequestScoped
@Getter @Setter
public class AuthBean {

    @Valid
    private RegistrationDto user = new RegistrationDto();

    @Autowired
    private UserService userService;

    private String message;

    // Navigation methods (converted from @GetMapping)
    public String goToLogin() {
        return "/login.xhtml?faces-redirect=true";
    }

    public String goToRegister() {
        // Reset form for new registration
        user = new RegistrationDto();
        message = null;
        return "/register.xhtml?faces-redirect=true";
    }

    // JSF-based registration method (alternative to HTML form)
    public String register() {
        FacesContext context = FacesContext.getCurrentInstance();

        // Validate email
        UserEntity existingEmail = userService.findByEmail(user.getEmail());
        if (existingEmail != null) {
            context.addMessage("registerForm:email",
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Email Error", "Email is already in use"));
            return null; // Stay on page
        }

        // Validate username
        UserEntity existingUsername = userService.findByUsername(user.getUsername());
        if (existingUsername != null) {
            context.addMessage("registerForm:username",
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Username Error", "Username is already taken"));
            return null; // Stay on page
        }

        try {
            // Save user
            userService.saveUser(user);

            // Success message
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Success", "Registration successful! You can now log in."));

            // Redirect to login with success parameter
            return "/login.xhtml?faces-redirect=true&success=true";

        } catch (Exception e) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error", "Registration failed. Please try again."));
            return null; // Stay on page
        }
    }

    // Server-side validation for AJAX calls (optional)
    public void validateEmail() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            UserEntity existingEmail = userService.findByEmail(user.getEmail());
            if (existingEmail != null) {
                context.addMessage("registerForm:email",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Email Error", "Email is already in use"));
            }
        }
    }

    public void validateUsername() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
            UserEntity existingUsername = userService.findByUsername(user.getUsername());
            if (existingUsername != null) {
                context.addMessage("registerForm:username",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Username Error", "Username is already taken"));
            }
        }
    }

    // Method to reset form
    public void resetForm() {
        user = new RegistrationDto();
        message = null;
    }

    // Utility method to check if there are validation errors
    public boolean hasErrors() {
        return FacesContext.getCurrentInstance().getMessageList().stream()
                .anyMatch(msg -> msg.getSeverity() == FacesMessage.SEVERITY_ERROR);
    }

    // Getters for URL parameters (for handling Spring Security redirects)
    public boolean isSuccessParam() {
        String success = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("success");
        return "true".equals(success);
    }

    public boolean isErrorParam() {
        String error = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("error");
        return "true".equals(error);
    }

    public boolean isLogoutParam() {
        String logout = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("logout");
        return "true".equals(logout);
    }
}