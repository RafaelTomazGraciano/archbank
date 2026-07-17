package com.graciano.archbank.scheduled;

import com.graciano.archbank.account.Account;
import com.graciano.archbank.scheduled.enums.PaymentRecurrence;
import com.graciano.archbank.scheduled.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "scheduled_payments")
@EntityListeners(AuditingEntityListener.class)
public class ScheduledPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_origin_id", nullable = false)
    private Account accountOrigin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_destination_id", nullable = false)
    private Account accountDestination;

    @Column(name = "amount", nullable = false, precision = 16, scale = 2)
    private BigDecimal amount;

    private String description;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Enumerated(EnumType.STRING)
    private PaymentRecurrence recurrence;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @CreatedDate
    @Column(name = "last_processed_at", nullable = false, updatable = false)
    private LocalDateTime lastProcessedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
