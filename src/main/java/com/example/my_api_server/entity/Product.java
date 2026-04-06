package com.example.my_api_server.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@Getter
@Builder
public class Product { //상품

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    private String productNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    private Long price;

    private Long stock;

    @Version
    private Long version;

    public void changeProductName(String changeProductName) {
        this.productName = changeProductName;
    }

    public void increaseStock(Long addStock) {
        this.stock += addStock; // 현재 재고 + 더 할 재고
    }

    public void decreaseStock(Long subStock) {
        this.stock -= subStock;
    }

    public void buyProductWithStock(Long orderCount) {
        if (this.stock - orderCount < 0) {
            throw new RuntimeException("재고가 없으므로 주문 불가");
        }
        this.decreaseStock(orderCount);
    }
}
