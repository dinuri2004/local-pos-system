package com.homeware.pos.config;

import com.homeware.pos.model.Product;
import com.homeware.pos.model.ProductVariant;
import com.homeware.pos.repository.ProductRepository;
import com.homeware.pos.repository.ProductVariantRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner loadData(ProductRepository productRepo, ProductVariantRepository variantRepo) {
        return args -> {
            if (productRepo.count() == 0) {
                // Product 1
                Product p1 = new Product();
                p1.setName("Premium Ceramic Mug");
                p1.setDescription("A high-quality ceramic mug for hot beverages.");
                p1.setCategory("Kitchen");
                
                productRepo.save(p1);

                ProductVariant v1 = new ProductVariant();
                v1.setProduct(p1);
                v1.setSku("MUG-WHT-001");
                v1.setBarcode("89010001"); // The famous testing barcode!
                v1.setSize("Standard");
                v1.setColor("White");
                v1.setCostPrice(new BigDecimal("150.00"));
                v1.setRetailPrice(new BigDecimal("450.00"));
                v1.setStockQuantity(50);
                variantRepo.save(v1);

                // Product 2
                Product p2 = new Product();
                p2.setName("Non-Stick Frying Pan");
                p2.setDescription("Teflon coated 28cm frying pan.");
                p2.setCategory("Cookware");
                
                productRepo.save(p2);

                ProductVariant v2 = new ProductVariant();
                v2.setProduct(p2);
                v2.setSku("PAN-28-NS");
                v2.setBarcode("89010002");
                v2.setSize("28cm");
                v2.setColor("Black");
                v2.setCostPrice(new BigDecimal("800.00"));
                v2.setRetailPrice(new BigDecimal("2100.00"));
                v2.setStockQuantity(20);
                variantRepo.save(v2);

                // Product 3
                Product p3 = new Product();
                p3.setName("Ergonomic Office Chair");
                p3.setDescription("Comfortable mesh office chair with lumbar support.");
                p3.setCategory("Furniture");
                
                productRepo.save(p3);

                ProductVariant v3 = new ProductVariant();
                v3.setProduct(p3);
                v3.setSku("CHR-OFF-01");
                v3.setBarcode("89010003");
                v3.setSize("Adjustable");
                v3.setColor("Black");
                v3.setCostPrice(new BigDecimal("9500.00"));
                v3.setRetailPrice(new BigDecimal("15000.00"));
                v3.setStockQuantity(5);
                variantRepo.save(v3);

                System.out.println("Data Seeder initialized standard mockup products into H2 Database!");
            }
        };
    }
}
