package com.graciano.archbank.pix;

import com.graciano.archbank.pix.enums.PixKeyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PixKeysRepository extends JpaRepository<PixKeys, UUID> {
    Optional<PixKeys> findByKeyTypeAndKeyValueAndIsActiveTrue(@NotNull PixKeyType type, @NotBlank String s);
}
