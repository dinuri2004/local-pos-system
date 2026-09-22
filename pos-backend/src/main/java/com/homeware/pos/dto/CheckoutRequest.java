package com.homeware.pos.dto;

import java.util.List;

public class CheckoutRequest {
    private List<CheckoutItemDto> items;
    private List<PaymentDto> payments;

    public List<CheckoutItemDto> getItems() { return items; }
    public void setItems(List<CheckoutItemDto> items) { this.items = items; }

    public List<PaymentDto> getPayments() { return payments; }
    public void setPayments(List<PaymentDto> payments) { this.payments = payments; }
}