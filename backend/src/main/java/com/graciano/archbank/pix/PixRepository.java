package com.graciano.archbank.pix;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PixRepository extends JpaRepository<Pix, UUID> {
}
