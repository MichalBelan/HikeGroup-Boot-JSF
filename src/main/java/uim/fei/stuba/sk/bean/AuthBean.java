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

    // JSF-based registration method (alternative to HTML form)
    public String register() {
        FacesContext context = FacesContext.getCurrentInstance();

        // Validate email
        UserEntity existingEmail = userService.findByEmail(user.getEmail());
        if (existingEmail != null) {
            context.addMessage("registerForm:email",
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Email Error", "Email is already in use"));
            return null;
        }

        // Validate username
        UserEntity existingUsername = userService.findByUsername(user.getUsername());
        if (existingUsername != null) {
            context.addMessage("registerForm:username",
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Username Error", "Username is already taken"));
            return null;
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

}