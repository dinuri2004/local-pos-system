package com.homeware.pos.config;

import com.homeware.pos.model.Product;
import com.homeware.pos.model.ProductVariant;
import com.homeware.pos.repository.ProductRepository;
import com.homeware.pos.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) return; // Prevent duplicate inserts

        // Product 1: Ceramic Mug
        Product mug = Product.builder()
                .name("Nordic Ceramic Coffee Mug")
                .category("Kitchenware")
                .description("Handmade ceramic mug with matte finish")
                .taxRate(new BigDecimal("12.00"))
                .active(true)
                .build();
        productRepository.save(mug);

        ProductVariant mugBlack = ProductVariant.builder()
                .product(mug)
                .sku("MUG-BLK-350")
                .barcode("89010001")
                .size("350ml")
                .color("Matte Black")
                .costPrice(new BigDecimal("450.00"))
                .retailPrice(new BigDecimal("850.00"))
                .stockQuantity(25)
                .reorderPoint(5)
                .build();

        ProductVariant mugWhite = ProductVariant.builder()
                .product(mug)
                .sku("MUG-WHT-350")
                .barcode("89010002")
                .size("350ml")
                .color("Ivory White")
                .costPrice(new BigDecimal("450.00"))
                .retailPrice(new BigDecimal("850.00"))
                .stockQuantity(15)
                .reorderPoint(5)
                .build();

        // Product 2: Cotton Bed Sheet
        Product sheet = Product.builder()
                .name("Egyptian Cotton Fitted Sheet")
                .category("Bedding")
                .description("400 thread count luxury fitted sheet")
                .taxRate(new BigDecimal("18.00"))
                .active(true)
                .build();
        productRepository.save(sheet);

        ProductVariant sheetKingBlue = ProductVariant.builder()
                .product(sheet)
                .sku("SHT-KNG-BLU")
                .barcode("89010003")
                .size("King")
                .color("Navy Blue")
                .costPrice(new BigDecimal("2200.00"))
                .retailPrice(new BigDecimal("4200.00"))
                .stockQuantity(8)
                .reorderPoint(2)
                .build();

        variantRepository.saveAll(List.of(mugBlack, mugWhite, sheetKingBlue));
        System.out.println(">> Sample Homeware Inventory Seeded Successfully!");
    }
}