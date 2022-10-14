package dev.yoon.orderservice.controller;

import dev.yoon.orderservice.dto.OrderDto;
import dev.yoon.orderservice.messagequeue.OrderProducer;
import dev.yoon.orderservice.repository.OrderEntity;
import dev.yoon.orderservice.messagequeue.KafkaProducer;
import dev.yoon.orderservice.service.OrderService;
import dev.yoon.orderservice.vo.RequestOrder;
import dev.yoon.orderservice.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order-service")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final Environment env;
    private final OrderService orderService;
    private final KafkaProducer kafkaProducer;
    private final OrderProducer orderProducer;

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in Order Service on PORT %s",
                env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
                                                     @RequestBody RequestOrder orderDetails) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = mapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);
        /* jpa */
    //        OrderDto createdOrder = orderService.createOrder(orderDto);
//        ResponseOrder responseOrder = mapper.map(createdOrder, ResponseOrder.class);

        /* Send an order to the Kafka */
//        kafkaProducer.send("example-order-topic", orderDto);

        /* kafka */
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDetails.getQty() * orderDetails.getUnitPrice());
        ResponseOrder responseOrder = mapper.map(orderDto, ResponseOrder.class);

        kafkaProducer.send("example-catalog-topic", orderDto); // order와 catalog를 연동하기 위한 kafka producer
        orderProducer.send("order", orderDto); // 사용자의 주문 정보를 kafka topic에 전달시키는 용도

        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId) throws Exception {
        List<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        ModelMapper mapper = new ModelMapper();
        orderList.forEach(v ->
                result.add(mapper.map(v, ResponseOrder.class))
        );

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
