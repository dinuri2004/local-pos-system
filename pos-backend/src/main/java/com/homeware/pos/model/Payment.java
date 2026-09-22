package com.homeware.pos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String paymentType;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime paymentTime = LocalDateTime.now();

    public static class PaymentBuilder {
        private PaymentMethod paymentMethod;
        private String paymentType;

        public PaymentBuilder paymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            if (paymentMethod != null) {
                this.paymentType = paymentMethod.name();
            }
            return this;
        }

        public PaymentBuilder paymentMethod(String paymentMethodStr) {
            if (paymentMethodStr != null) {
                try {
                    this.paymentMethod = PaymentMethod.valueOf(paymentMethodStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    this.paymentMethod = PaymentMethod.CASH;
                }
                this.paymentType = paymentMethodStr;
            }
            return this;
        }
    }
}