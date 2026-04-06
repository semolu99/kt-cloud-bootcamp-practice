package com.example.my_api_server.service.dto;

import java.util.List;

public record OrderCreateDto(
        Long memberId,

        List<Long> productId,//주문 상품 ID들

        List<Long> count
) {

}
