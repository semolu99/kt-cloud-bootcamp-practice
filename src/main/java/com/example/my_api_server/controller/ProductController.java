package com.example.my_api_server.controller;

import com.example.my_api_server.service.ProductService;
import com.example.my_api_server.service.dto.ProductCreateDto;
import com.example.my_api_server.service.dto.ProductResDto;
import com.example.my_api_server.service.dto.ProductUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ProductResDto createProduct(@Validated @RequestBody ProductCreateDto dto) {
        return productService.createProduct(dto);
    }

    @GetMapping("/{id}")
    public ProductResDto findProduct(@PathVariable Long id) {
        return productService.findProduct(id);
    }

    //put과 patch의 차이는 뭘까
    //patch는 일부분 수정
    //put은 전체 수정 의미
    @PatchMapping
    public ProductResDto updateProduct(@Validated @RequestBody ProductUpdateDto dto) {
        return productService.updateProduct(dto);
    }
}
