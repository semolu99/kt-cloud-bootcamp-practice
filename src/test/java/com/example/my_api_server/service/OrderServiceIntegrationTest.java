package com.example.my_api_server.service;

import com.example.my_api_server.common.MemberFixture;
import com.example.my_api_server.common.ProductFixture;
import com.example.my_api_server.config.TestContainerConfig;
import com.example.my_api_server.entity.Member;
import com.example.my_api_server.entity.OrderProduct;
import com.example.my_api_server.entity.Product;
import com.example.my_api_server.repo.MemberDBRepo;
import com.example.my_api_server.repo.OrderProductRepo;
import com.example.my_api_server.repo.OrderRepo;
import com.example.my_api_server.repo.ProductRepo;
import com.example.my_api_server.service.dto.OrderCreateDto;
import com.example.my_api_server.service.dto.OrderResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestContainerConfig.class)
@ActiveProfiles("test")
public class OrderServiceIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceIntegrationTest.class);
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private MemberDBRepo memberDBRepo;

    @Autowired
    private OrderProductRepo orderProductRepo;

    private List<Long> getProductIds(List<Product> products) {
        return products.stream().map(Product::getId).toList();
    }

    @BeforeEach
    public void setup() {
        orderProductRepo.deleteAllInBatch();
        productRepo.deleteAllInBatch();
        orderRepo.deleteAllInBatch();
        memberDBRepo.deleteAllInBatch();
    }

    private Member getSaveMember(String password) {
        return memberDBRepo.save(MemberFixture
                .defaultMember()
                .password(password)
                .build());
    }

    private List<Product> getProducts() {
        return productRepo.saveAll(ProductFixture.defaultProducts());
    }

    @Test
    @DisplayName("주문 생성 시 ")
    public void createOrderStockDecreaseSuccess() {
        //given
        List<Long> counts = List.of(1L, 2L);

        Member savedMember = getSaveMember("1234");

        List<Product> products = getProducts();

        List<Long> productIds = getProductIds(products);

        OrderCreateDto createDto = new OrderCreateDto(savedMember.getId(), productIds, counts, LocalDateTime.now());

        //when
        OrderResponseDto retDto = orderService.createOrder(createDto);

        //then
        List<Product> resultProducts = productRepo.findAllById(productIds);
        for (int i = 0; i < products.size(); i++) {
            Product beforeProduct = products.get(i);
            Product nowProduct = resultProducts.get(i);
            Long orderStock = counts.get(i);
            assertThat(beforeProduct.getStock() - orderStock).isEqualTo(nowProduct.getStock());
        }
    }

    @Nested
    @DisplayName("주문 생성 TC")
    class OrderCreateTest {

        @Test
        @DisplayName("주문 생성 시 DB에 저장되고 주문 시간이 Null이 아니다.")
        public void createOrderPersistAndReturn() {
            //given
            List<Long> counts = List.of(1L, 2L);

            Member savedMember = getSaveMember("1234");

            List<Product> products = getProducts();

            List<Long> productIds = getProductIds(products);

            OrderCreateDto createDto = new OrderCreateDto(savedMember.getId(), productIds, counts, LocalDateTime.now());

            //when
            OrderResponseDto retDto = orderService.createOrder(createDto);

            //then
            assertThat(retDto.getOrderCompletedTime()).isNotNull();
        }

        @Test
        @DisplayName("주문 생성 시 재고가 정상적으로 차감된다.")
        public void createOrderStockDecreaseSuccess() {
            //given
            List<Long> counts = List.of(1L, 2L);

            Member savedMember = getSaveMember("1234");

            List<Product> products = getProducts();

            List<Long> productIds = getProductIds(products);

            OrderCreateDto createDto = new OrderCreateDto(savedMember.getId(), productIds, counts, LocalDateTime.now());

            //when
            OrderResponseDto retDto = orderService.createOrder(createDto);

            //then
            List<Product> resultProducts = productRepo.findAllById(productIds);
            for (int i = 0; i < products.size(); i++) {
                Product beforeProduct = products.get(i);
                Product nowProduct = resultProducts.get(i);
                Long orderStock = counts.get(i);
                assertThat(beforeProduct.getStock() - orderStock).isEqualTo(nowProduct.getStock());
            }
        }

        @Test
        @DisplayName("주문 생성 시 재고가 부족하면 예외가 정상 작동한다.")
        public void createOrderStockValidation() {
            //given
            List<Long> counts = List.of(100L, 100L);

            Member savedMember = getSaveMember("1234");

            List<Product> products = getProducts();

            List<Long> productIds = getProductIds(products);

            OrderCreateDto createDto = new OrderCreateDto(savedMember.getId(), productIds, counts);

            //when

            //then
            assertThatThrownBy(() -> orderService.createOrder(createDto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("재고가 없으므로 주문 불가");
        }

        @Test
        @DisplayName("주문 생성 시 상품 주문 개수와 저장된 값이 같다.")
        public void createOrderSameCount() {
            //given
            List<Long> counts = List.of(5L, 6L);
            Member savedMember = getSaveMember("1234");
            List<Product> products = getProducts();
            List<Long> productIds = getProductIds(products);
            OrderCreateDto createDto = new OrderCreateDto(savedMember.getId(), productIds, counts, LocalDateTime.now());
            //when
            OrderResponseDto response = orderService.createOrder(createDto);
            //then
            List<OrderProduct> orderProducts = orderProductRepo.findAllByOrderId(response.getOrderId());
            assertThat(orderProducts)
                    .extracting(OrderProduct::getNumber)
                    .containsExactlyElementsOf(counts);
        }


    }

    @Nested
    @DisplayName("주문과 연관된 도메인 예외 TC")
    class OrderRelatedExceptionTest {
        @Test
        @DisplayName("주문 시 회원이 존재하지 않으면 예외가 발생한다.")
        public void validateMemberWhenCreateOrder() {
            List<Long> counts = List.of(1L, 1L);
            Member savedMember = getSaveMember("1234"); //멤버 저장
            List<Product> products = getProducts(); //상품 저장
            List<Long> productIds = getProductIds(products); //productId 추출 작업

            OrderCreateDto createDto = new OrderCreateDto(12341L, productIds, counts, LocalDateTime.now());

            //when
            //then
            assertThatThrownBy(() -> orderService.createOrder(createDto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("회원이 존재하지 않습니다.");
        }

        @Test
        @DisplayName("주문 시 존재하지 않는 상품에 대해서 예외가 발생한다.")
        public void validateProductWhenCreateOrder() {
            //given
            List<Long> counts = List.of(1L, 1L);
            Member savedMember = getSaveMember("1234"); //멤버 저장
            List<Product> products = getProducts(); //상품 저장
            List<Long> productIds = List.of(123L, 223L); //없는 product ID

            OrderCreateDto createDto = new OrderCreateDto(savedMember.getId(), productIds, counts, LocalDateTime.now());
            //when //then
            assertThatThrownBy(() -> orderService.createOrder(createDto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("존재하지 않는 제품입니다.");
        }
    }
}
