package com.graciano.archbank.auth;

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
@Table(name = "login_attempts")
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String email;

    @Column(name = "ip_address")
    private String ipAddress;

    private Boolean success;

    @Column(name = "attempted_at")
    private LocalDateTime attemptedAt;

}
