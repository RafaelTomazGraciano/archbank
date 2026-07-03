package com.graciano.archbank.account;

import com.graciano.archbank.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "accounts")
@SQLDelete(sql="UPDATE accounts SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "account_number")
    private String accountNumber;

    @Builder.Default
    private String branch = "0001";

    @Column(name = "balance", nullable = false, precision = 16, scale = 2)
    private BigDecimal balance;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updated_at;
}
