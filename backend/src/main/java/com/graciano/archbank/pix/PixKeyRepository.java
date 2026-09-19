package com.graciano.archbank.pix;

import com.graciano.archbank.pix.enums.PixKeyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PixKeyRepository extends JpaRepository<PixKey, UUID> {
    Optional<PixKey> findByKeyTypeAndKeyValue(PixKeyType type, String value);

    boolean existsByAccountIdAndKeyType(UUID accountId, PixKeyType keyType);

    List<PixKey> findAllByAccountUserId(UUID id);
}
