package com.homeware.pos.dto;

import java.math.BigDecimal;

import com.homeware.pos.model.PaymentMethod;

public class PaymentDto {
    private BigDecimal amount;
    private PaymentMethod paymentMethod;

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
}