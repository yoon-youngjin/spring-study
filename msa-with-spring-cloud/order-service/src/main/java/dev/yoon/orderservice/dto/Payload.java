package dev.yoon.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class Payload {

    private String order_id;
    private String user_id;
    private String product_id;
    private int qty;
    private int total_price;
    private int unit_price;
}
