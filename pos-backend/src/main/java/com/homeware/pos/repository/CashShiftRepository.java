package com.homeware.pos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homeware.pos.model.CashShift;

public interface CashShiftRepository extends JpaRepository<CashShift, Long> {
    Optional<CashShift> findByStatus(String status);
}