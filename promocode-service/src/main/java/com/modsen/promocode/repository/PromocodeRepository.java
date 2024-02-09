package com.modsen.promocode.repository;

import com.modsen.promocode.model.Promocode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromocodeRepository extends JpaRepository<Promocode, Long> {
    boolean existsByName(String name);

    Optional<Promocode> findByName(String name);
}
