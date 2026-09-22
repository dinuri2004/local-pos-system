package com.homeware.pos.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.homeware.pos.model.CashShift;
import com.homeware.pos.repository.CashShiftRepository;
import com.homeware.pos.repository.PaymentRepository;

@Service
public class ShiftService {

    private final CashShiftRepository shiftRepository;
    private final PaymentRepository paymentRepository;

    public ShiftService(CashShiftRepository shiftRepository, PaymentRepository paymentRepository) {
        this.shiftRepository = shiftRepository;
        this.paymentRepository = paymentRepository;
    }

    public CashShift openShift(String cashierName, BigDecimal openingFloat) {
        shiftRepository.findByStatus("OPEN").ifPresent(s -> {
            throw new IllegalStateException("An active shift is already open!");
        });

        CashShift shift = new CashShift();
        shift.setCashierName(cashierName);
        shift.setOpeningFloat(openingFloat);
        shift.setStatus("OPEN");
        return shiftRepository.save(shift);
    }

    public CashShift getActiveShift() {
        return shiftRepository.findByStatus("OPEN")
                .orElseThrow(() -> new IllegalStateException("No active shift found. Please open a shift first."));
    }

    public CashShift closeZReport(Long shiftId, BigDecimal countedCash) {
        CashShift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        if (!"OPEN".equals(shift.getStatus())) {
            throw new IllegalStateException("Shift is already closed.");
        }

        LocalDateTime now = LocalDateTime.now();

        BigDecimal cashSales = paymentRepository.sumCashPaymentsBetween(shift.getStartTime(), now);
        if (cashSales == null) {
            cashSales = BigDecimal.ZERO;
        }

        BigDecimal expectedCash = shift.getOpeningFloat().add(cashSales);
        BigDecimal variance = countedCash.subtract(expectedCash);

        shift.setCountedCash(countedCash);
        shift.setCashVariance(variance);
        shift.setEndTime(now);
        shift.setStatus("CLOSED");
        shift.setZCounter((int) (shiftRepository.count()));

        return shiftRepository.save(shift);
    }
}