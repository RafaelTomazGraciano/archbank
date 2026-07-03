package com.graciano.archbank.notification;

import com.graciano.archbank.notification.enums.NotificationType;
import com.graciano.archbank.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String title;

    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType notification;

    @Builder.Default
    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
