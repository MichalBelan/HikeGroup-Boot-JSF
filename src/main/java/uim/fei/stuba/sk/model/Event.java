package uim.fei.stuba.sk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)  // Zvýšený limit pre názov
    private String name;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Column(length = 500)  // Zvýšený limit pre typ
    private String type;

    @Column(length = 1000)  // Zvýšený limit pre URL
    private String photoUrl;

    @CreationTimestamp
    private LocalDateTime createdOn;

    @UpdateTimestamp
    private LocalDateTime updatedOn;

    @Column(columnDefinition = "TEXT")  // Už máš správne
    private String elevationData;

    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;
}