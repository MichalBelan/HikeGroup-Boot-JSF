package uim.fei.stuba.sk.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import uim.fei.stuba.sk.model.Club;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private Long id;

    @NotEmpty(message = "Name must not be empty")
    private String name;

    @NotNull(message = "Start time is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endTime;

    @NotEmpty(message = "Type must not be empty")
    private String type;

    @NotEmpty(message = "Photo URL must not be empty")
    private String photoUrl;

    private String elevationData;


    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private Club club;
}
