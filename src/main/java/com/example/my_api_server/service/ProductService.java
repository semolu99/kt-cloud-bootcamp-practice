package com.example.my_api_server.service;

import com.example.my_api_server.entity.Product;
import com.example.my_api_server.repo.ProductRepo;
import com.example.my_api_server.service.dto.ProductCreateDto;
import com.example.my_api_server.service.dto.ProductResDto;
import com.example.my_api_server.service.dto.ProductUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductService {

    private final ProductRepo productRepo;

    //상품 등록
    //JPA 하이버네이트는 DB랑 통신하기위해 ACID가 되기 위해선 Begin, 등 해야한다
    @Transactional
    public ProductResDto createProduct(ProductCreateDto dto) {
        Product product = Product.builder()
                .productName(dto.getProductName())
                .productType(dto.getProductType())
                .productNumber(dto.getProductNumber())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .build();

        Product savedProduct = productRepo.save(product);

        ProductResDto resDto = ProductResDto.builder()
                .productNumber(savedProduct.getProductNumber())
                .stock(savedProduct.getStock())
                .price(savedProduct.getPrice())
                .build();

        return resDto;
    }

    //상품 조회
    public ProductResDto findProduct(Long productId) {
        Product product = productRepo.findById(productId).orElseThrow();

        ProductResDto resDto = ProductResDto.builder()
                .productNumber(product.getProductNumber())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
        return resDto;
    }

    //상품 수정
    @Transactional
    public ProductResDto updateProduct(ProductUpdateDto dto) {
        Product product = productRepo.findById(dto.productId()).orElseThrow();

        product.changeProductName(dto.changeProductName());
        product.increaseStock(dto.changeStock());

        ProductResDto resDto = ProductResDto.builder()
                .productNumber(product.getProductNumber())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();

        return resDto;
    }

}
