package com.homeware.pos.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.homeware.pos.dto.CheckoutItemDto;
import com.homeware.pos.dto.CheckoutRequest;
import com.homeware.pos.dto.PaymentDto;
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

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductVariantRepository variantRepository;

    @Autowired
    private ReceiptPrinterService receiptPrinterService;

    @Transactional
    public Order processCheckout(CheckoutRequest request) {
        Order order = new Order();
        order.setOrderTime(LocalDateTime.now());
        order.setReceiptNumber("REC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setStatus(OrderStatus.COMPLETED);

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CheckoutItemDto itemDto : request.getItems()) {
            ProductVariant variant = variantRepository.findById(itemDto.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Variant not found with ID: " + itemDto.getVariantId()));

            if (variant.getStockQuantity() < itemDto.getQuantity()) {
                throw new RuntimeException("Insufficient stock for SKU: " + variant.getSku());
            }

            variant.setStockQuantity(variant.getStockQuantity() - itemDto.getQuantity());
            variantRepository.save(variant);

            BigDecimal lineTotal = variant.getRetailPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            subtotal = subtotal.add(lineTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductVariant(variant); // Updated to match OrderItem model property
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setUnitPrice(variant.getRetailPrice());
            orderItem.setCostPrice(variant.getCostPrice());
            orderItem.setLineTotal(lineTotal);

            order.getItems().add(orderItem);
        }

        BigDecimal grandTotal = subtotal;

        order.setSubtotal(subtotal);
        order.setTaxTotal(BigDecimal.ZERO);
        order.setTotalAmount(grandTotal);

        for (PaymentDto payDto : request.getPayments()) {
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setAmount(payDto.getAmount());
            payment.setPaymentMethod(payDto.getPaymentMethod());
            payment.setPaymentType("SALE");
            payment.setPaymentTime(LocalDateTime.now());

            order.getPayments().add(payment);
        }

        Order savedOrder = orderRepository.save(order);

        boolean hasCash = savedOrder.getPayments().stream()
                .anyMatch(p -> p.getPaymentMethod() == PaymentMethod.CASH);

        receiptPrinterService.printReceiptAndOpenDrawer(savedOrder, hasCash);

        return savedOrder;
    }
}