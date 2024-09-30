package com.shop.product.service;

import com.shop.product.dto.ProductRequest;
import com.shop.product.dto.ProductResponse;
import com.shop.product.model.Product;
import com.shop.product.repository.ProductRepository;

import java.util.ArrayList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(final ProductRequest productRequest) {
        Product product = mapDtoToEntity(productRequest);
        productRepository.save(product);
        log.info("Product: {}-{} created", product.getName(), product.getId());
        return mapEntityToDto(product);
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try {
            products.addAll(productRepository.findAll());
        } catch (Exception e) {
            log.info("No products found");
        }
        return products.stream().map(this::mapEntityToDto).toList();
    }

    private ProductResponse mapEntityToDto(final Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .skuCode(product.getSkuCode())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }

    private Product mapDtoToEntity(final ProductRequest productRequest) {
        return Product.builder()
                .skuCode(productRequest.getSkuCode())
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();
    }
}
