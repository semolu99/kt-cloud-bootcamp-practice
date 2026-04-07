package com.example.my_api_server.service;

import com.example.my_api_server.entity.Member;
import com.example.my_api_server.entity.Order;
import com.example.my_api_server.entity.OrderStatus;
import com.example.my_api_server.entity.Product;
import com.example.my_api_server.repo.MemberDBRepo;
import com.example.my_api_server.repo.OrderRepo;
import com.example.my_api_server.repo.ProductRepo;
import com.example.my_api_server.service.dto.OrderCreateDto;
import com.example.my_api_server.service.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepo orderRepo;
    private final MemberDBRepo memberRepo;
    private final ProductRepo productRepo;

    @Transactional
    public OrderResponseDto createOrder(OrderCreateDto dto) {
        Member member = memberRepo.findById(dto.memberId()).orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        Order order = Order.createOrder(member, dto.orderTime());

        List<Product> products = productRepo.findAllById(dto.productId());

        if (products.size() != dto.productId().size()) throw new RuntimeException("존재하지 않는 제품입니다.");

        order.addOrderProducts(dto.count(), products);

        Order savedOrder = orderRepo.save(order);
        //entity -> Dto로 변환
        return OrderResponseDto.of(savedOrder.getId(), savedOrder.getOrderTime(), OrderStatus.COMPLETED, true);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW) //낙관락 예시
    @Retryable(includes = ObjectOptimisticLockingFailureException.class, maxRetries = 3)
    public OrderResponseDto CreateOrderOptLock(OrderCreateDto dto) {
        log.info("@Retryable 테스트");
        Member member = memberRepo.findById(dto.memberId()).orElseThrow();
        LocalDateTime orderTime = LocalDateTime.now();

        Order order = Order.builder()
                .buyer(member)
                .orderStatus(OrderStatus.PENDING)
                .orderTime(orderTime)
                .build();

        List<Product> products = productRepo.findAllById(dto.productId());

//        List<OrderProduct> orderProducts = IntStream.range(0, dto.count().size())
//                .mapToObj(idx -> {
//                    Product product = products.get(idx);
//                    if (product.getStock() - dto.count().get(idx) < 0) {
//                        throw new RuntimeException("재고가 없으므로 주문 불가");
//                    }
//
//                    product.decreaseStock(dto.count().get(idx));
//
//                    return OrderProduct.builder()
//                            .order(order)
//                            .product(products.get(idx))
//                            .number(dto.count().get(idx))
//                            .build();
//                }).toList();

        order.addOrderProducts(dto.count(), products);

        Order savedOrder = orderRepo.save(order);

        OrderResponseDto orderResponseDto = OrderResponseDto.of(order.getId(), savedOrder.getOrderTime(), OrderStatus.COMPLETED, true);

        return orderResponseDto;
    }

    @Transactional
    public OrderResponseDto CreateOrderPLock(OrderCreateDto dto) {
        Member member = memberRepo.findById(dto.memberId()).orElseThrow();
        LocalDateTime orderTime = LocalDateTime.now();

        Order order = Order.builder()
                .buyer(member)
                .orderStatus(OrderStatus.PENDING)
                .orderTime(orderTime)
                .build();
        List<Product> products = productRepo.findAllByIdsWithXLock(dto.productId());

//        List<OrderProduct> orderProducts = IntStream.range(0, dto.count().size())
//                .mapToObj(idx -> {
//                    Product product = products.get(idx);
//                    if (product.getStock() - dto.count().get(idx) < 0) {
//                        throw new RuntimeException("재고가 없으므로 주문 불가");
//                    }
//
//                    product.decreaseStock(dto.count().get(idx));
//
//                    return OrderProduct.builder()
//                            .order(order)
//                            .product(products.get(idx))
//                            .number(dto.count().get(idx))
//                            .build();
//                }).toList();

        order.addOrderProducts(dto.count(), products);

        Order savedOrder = orderRepo.save(order);

        //entity -> Dto로 변환
        OrderResponseDto orderResponseDto = OrderResponseDto.of(order.getId(), savedOrder.getOrderTime(), OrderStatus.COMPLETED, true);

        return orderResponseDto;
    }

    /**
     * JPA는 내부적으로 캐시(중간 지점의 미니 창고) 매커니즘
     * - 내부에 1차 캐시, 2차 캐시
     * - 1차 캐시 내부적으로 영속화(내자식으로 만들곘다)
     * - readOnly=true시 내부 하이버네이트
     */

    @Transactional(readOnly = true) //readOnly
    public OrderResponseDto findOrder(Long orderId) {
        Order order = orderRepo.findById(orderId).orElseThrow();
        return OrderResponseDto.of(order.getId(), order.getOrderTime(), order.getOrderStatus(), true);
    }
}
