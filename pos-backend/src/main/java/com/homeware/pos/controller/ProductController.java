package com.homeware.pos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.homeware.pos.repository.ProductVariantRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // Allows Angular frontend to connect
@RequiredArgsConstructor
public class ProductController {

    private final ProductVariantRepository variantRepository;

    @GetMapping("/scan/{barcode}")
    public ResponseEntity<?> scanBarcode(@PathVariable String barcode) {
        return variantRepository.findByBarcode(barcode)
                .map(variant -> ResponseEntity.ok((Object) variant))
                .orElseGet(() -> ResponseEntity.status(404)
                        .body("Product with barcode '" + barcode + "' not found"));
    }
}