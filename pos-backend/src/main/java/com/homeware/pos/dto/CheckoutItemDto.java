package com.homeware.pos.dto;

public class CheckoutItemDto {
    private Long variantId;
    private Integer quantity;

    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}