package com.shop.product.service;

import com.shop.product.dto.ProductDto;
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

    public ProductDto createProduct(final ProductDto productDto) {
        Product product = mapDtoToEntity(productDto);
        productRepository.save(product);
        log.info("Product: {}-{} created", product.getName(), product.getId());
        return mapEntityToDto(product);
    }

    public List<ProductDto> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try {
            products.addAll(productRepository.findAll());
        } catch (Exception e) {
            log.info("No products found");
        }
        return products.stream().map(this::mapEntityToDto).toList();
    }

    private ProductDto mapEntityToDto(final Product product) {
        return new ProductDto(product.getId(), product.getName(), product.getDescription(), product.getSkuCode(), product.getPrice());
    }

    private Product mapDtoToEntity(final ProductDto productDto) {
        return Product.builder()
                .skuCode(productDto.skuCode())
                .name(productDto.name())
                .description(productDto.description())
                .price(productDto.price())
                .build();
    }
}
