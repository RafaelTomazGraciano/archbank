package com.graciano.archbank.pix;

import com.graciano.archbank.account.Account;
import com.graciano.archbank.pix.enums.PixKeyType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "pix_keys")
@SQLDelete(sql="UPDATE pix_keys SET is_active = false WHERE id = ?")
@SQLRestriction("is_active = true")
public class Pix {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "key_type")
    private PixKeyType keyType;

    @Column(name = "key_value")
    private String keyValue;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
