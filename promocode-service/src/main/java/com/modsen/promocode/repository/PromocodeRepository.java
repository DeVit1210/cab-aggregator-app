package com.modsen.promocode.repository;

import com.modsen.promocode.model.Promocode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromocodeRepository extends JpaRepository<Promocode, Long> {
}
