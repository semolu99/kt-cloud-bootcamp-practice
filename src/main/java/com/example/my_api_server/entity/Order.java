package com.example.my_api_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@Getter
@Builder
public class Order {

    //주문(1) <-> 주문상품(여러 상품들) <-> 상품(1)
    //Order가 저장되면 orderProduct도 같이 저장된다.(생명주기를 동일하게 하겠다)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderProduct> orderProducts = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//PK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member buyer;//구매자
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;//주문상태
    @Column(nullable = false)
    private LocalDateTime orderTime;//주문시간

    public static Order createOrder(Member member, LocalDateTime orderTime) {

        return Order.builder()
                .buyer(member)
                .orderStatus(OrderStatus.PENDING)
                .orderTime(orderTime)
                .build();
    }

    public OrderProduct createOrderProduct(Long orderCount, Product product) {
        product.buyProductWithStock(orderCount);
        return OrderProduct.builder()
                .order(this)
                .product(product)
                .number(orderCount)
                .build();
    }

    //양방향 매핑
    public void addOrderProducts(List<Long> counts, List<Product> products) {

        this.orderProducts = IntStream.range(0, counts.size())
                .mapToObj(idx -> {
                    return this.createOrderProduct(counts.get(idx), products.get(idx));
                }).toList();
    }
}
