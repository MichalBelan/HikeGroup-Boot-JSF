package uim.fei.stuba.sk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationDto {
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 32, message = "Username must be 3–32 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
