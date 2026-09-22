package com.homeware.pos.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class CheckoutRequest {
    private List<ItemRequest> items;
    private List<PaymentRequest> payments;

    @Data
    public static class ItemRequest {
        private Long variantId;
        private Integer quantity;
    }

    @Data
    public static class PaymentRequest {
        private BigDecimal amount;
        private String paymentMethod; // Accepts "CASH", "CARD", etc. directly from frontend
    }
}