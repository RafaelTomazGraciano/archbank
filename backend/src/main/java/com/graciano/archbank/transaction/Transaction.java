package com.graciano.archbank.transaction;

import com.graciano.archbank.account.Account;
import com.graciano.archbank.transaction.enums.TransactionStatus;
import com.graciano.archbank.transaction.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_origin_id", nullable = true)
    private Account accountOrigin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_destination_id", nullable = true)
    private Account accountDestination;

    @Column(name = "amount", nullable = false, precision = 16, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false)
    @Builder.Default
    private TransactionStatus transactionStatus = TransactionStatus.valueOf("COMPLETED");

    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
