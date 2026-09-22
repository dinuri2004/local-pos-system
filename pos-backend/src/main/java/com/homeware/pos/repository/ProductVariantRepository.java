package com.homeware.pos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.homeware.pos.model.ProductVariant;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    Optional<ProductVariant> findByBarcode(String barcode);
    Optional<ProductVariant> findBySku(String sku);

    @Modifying
    @Query("UPDATE ProductVariant v SET v.stockQuantity = v.stockQuantity - :qty WHERE v.id = :id AND v.stockQuantity >= :qty")
    int deductStockIfAvailable(@Param("id") Long id, @Param("qty") Integer qty);
}