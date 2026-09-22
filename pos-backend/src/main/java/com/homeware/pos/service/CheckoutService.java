package com.homeware.pos.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.homeware.pos.dto.CheckoutRequest;
import com.homeware.pos.model.Order;
import com.homeware.pos.model.OrderItem;
import com.homeware.pos.model.OrderStatus;
import com.homeware.pos.model.Payment;
import com.homeware.pos.model.PaymentMethod;
import com.homeware.pos.model.ProductVariant;
import com.homeware.pos.repository.OrderRepository;
import com.homeware.pos.repository.ProductVariantRepository;

@Service
public class CheckoutService {

    private final OrderRepository orderRepository;
    private final ProductVariantRepository variantRepository;

    public CheckoutService(OrderRepository orderRepository, ProductVariantRepository variantRepository) {
        this.orderRepository = orderRepository;
        this.variantRepository = variantRepository;
    }

    @Transactional
    public Order processCheckout(CheckoutRequest request) {
        Order order = new Order();
        order.setReceiptNumber("REC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setStatus(OrderStatus.COMPLETED);
        order.setOrderTime(LocalDateTime.now());

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;
        BigDecimal discountTotal = BigDecimal.ZERO;

        List<OrderItem> orderItems = new ArrayList<>();

        for (CheckoutRequest.ItemRequest itemReq : request.getItems()) {
            ProductVariant variant = variantRepository.findById(itemReq.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Variant not found: " + itemReq.getVariantId()));

            // Atomic stock deduction
            int updatedRows = variantRepository.deductStockIfAvailable(variant.getId(), itemReq.getQuantity());
            if (updatedRows == 0) {
                throw new RuntimeException("Insufficient stock for item: " + variant.getSku());
            }

            BigDecimal linePrice = variant.getRetailPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            subtotal = subtotal.add(linePrice);

            if (variant.getProduct() != null && variant.getProduct().getTaxRate() != null) {
                BigDecimal itemTax = linePrice.multiply(variant.getProduct().getTaxRate()).divide(BigDecimal.valueOf(100));
                taxTotal = taxTotal.add(itemTax);
            }

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .variant(variant)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(variant.getRetailPrice())
                    .costPrice(variant.getCostPrice())
                    .discountAmount(BigDecimal.ZERO)
                    .lineTotal(linePrice)
                    .build();

            orderItems.add(orderItem);
        }

        BigDecimal grandTotal = subtotal.add(taxTotal).subtract(discountTotal);

        order.setItems(orderItems);
        order.setSubtotal(subtotal);
        order.setTaxTotal(taxTotal);
        order.setDiscountTotal(discountTotal);
        order.setTotalAmount(grandTotal);
        order.setGrandTotal(grandTotal);

        List<Payment> payments = new ArrayList<>();
        for (CheckoutRequest.PaymentRequest payReq : request.getPayments()) {
            PaymentMethod method;
            try {
                String methodStr = payReq.getPaymentMethod() != null ? payReq.getPaymentMethod().toUpperCase() : "CASH";
                method = PaymentMethod.valueOf(methodStr);
            } catch (Exception e) {
                method = PaymentMethod.CASH;
            }

            Payment payment = Payment.builder()
                    .order(order)
                    .amount(payReq.getAmount())
                    .paymentMethod(method)
                    .paymentTime(LocalDateTime.now())
                    .build();

            payments.add(payment);
        }

        order.setPayments(payments);

        return orderRepository.save(order);
    }
}