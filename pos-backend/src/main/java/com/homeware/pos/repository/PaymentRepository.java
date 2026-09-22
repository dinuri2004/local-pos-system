package com.homeware.pos.repository;

import com.homeware.pos.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentType = 'CASH' AND p.paymentTime BETWEEN :start AND :end")
    BigDecimal sumCashPaymentsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}