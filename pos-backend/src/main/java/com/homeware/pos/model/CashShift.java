package com.homeware.pos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "cash_shifts")
@Data
public class CashShift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cashierName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal openingFloat;

    @Column(precision = 12, scale = 2)
    private BigDecimal countedCash;

    @Column(precision = 12, scale = 2)
    private BigDecimal cashVariance;

    @Column(nullable = false)
    private LocalDateTime startTime = LocalDateTime.now();

    private LocalDateTime endTime;

    @Column(nullable = false)
    private String status = "OPEN"; // OPEN or CLOSED

    private Integer zCounter;
}