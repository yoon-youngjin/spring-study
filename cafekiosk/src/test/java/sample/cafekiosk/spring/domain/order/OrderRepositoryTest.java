package sample.cafekiosk.spring.domain.order;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import sample.cafekiosk.spring.IntegrationTestSupport;
import sample.cafekiosk.spring.domain.orderproduct.OrderProductRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static sample.cafekiosk.spring.domain.order.OrderStatus.PAYMENT_COMPLETED;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

//@ActiveProfiles("test")
//@SpringBootTest
//@DataJpaTest
class OrderRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @AfterEach
    void tearDown() {
        orderProductRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("특정 날짜와 주문 상태에 맞는 주문 데이터를 조회한다.")
    void findAllBySellingStatusIn() {
        // given
        Product product1 = createProduct(HANDMADE, "001", 1000);
        Product product2 = createProduct(HANDMADE, "002", 3000);
        Product product3 = createProduct(HANDMADE, "003", 5000);
        List<Product> products = List.of(product1, product2, product3);
        productRepository.saveAll(products);

        LocalDate targetDate = LocalDate.of(2023, 12, 13);
        Order order1 = createOrder(OrderStatus.INIT, products, LocalDate.of(2023, 12, 14));
        Order order2 = createOrder(PAYMENT_COMPLETED, products, targetDate);

        orderRepository.saveAll(List.of(order1, order2));

        // when
        List<Order> orders = orderRepository.findOrdersBy(targetDate.atStartOfDay(), targetDate.plusDays(1).atStartOfDay(), PAYMENT_COMPLETED);

        // then
        assertThat(orders).hasSize(1)
                .extracting("orderStatus", "totalPrice", "registeredDateTime")
                .containsExactlyInAnyOrder(
                        tuple(PAYMENT_COMPLETED, 9000, targetDate.atStartOfDay())
                );
    }

    private Order createOrder(OrderStatus orderStatus, List<Product> products, LocalDate registeredDate) {
        return Order.builder()
                .orderStatus(orderStatus)
                .products(products)
                .registeredDateTime(registeredDate.atStartOfDay())
                .build();
    }

    private Product createProduct(ProductType type, String productNumber, int price) {
        return Product.builder()
                .type(type)
                .productNumber(productNumber)
                .price(price)
                .sellingStatus(SELLING)
                .name("메뉴 이름")
                .build();
    }

}