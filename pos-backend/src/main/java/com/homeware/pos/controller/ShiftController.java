package com.homeware.pos.controller;

import com.homeware.pos.model.CashShift;
import com.homeware.pos.service.ShiftService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/shifts")
@CrossOrigin(origins = "*")
public class ShiftController {

    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @PostMapping("/open")
    public ResponseEntity<CashShift> openShift(@RequestBody Map<String, Object> request) {
        String cashierName = (String) request.get("cashierName");
        BigDecimal openingFloat = new BigDecimal(request.get("openingFloat").toString());
        CashShift shift = shiftService.openShift(cashierName, openingFloat);
        return ResponseEntity.ok(shift);
    }

    @GetMapping("/active")
    public ResponseEntity<CashShift> getActiveShift() {
        try {
            CashShift shift = shiftService.getActiveShift();
            return ResponseEntity.ok(shift);
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/close/{id}")
    public ResponseEntity<CashShift> closeShift(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        BigDecimal countedCash = new BigDecimal(request.get("countedCash").toString());
        CashShift shift = shiftService.closeZReport(id, countedCash);
        return ResponseEntity.ok(shift);
    }
}