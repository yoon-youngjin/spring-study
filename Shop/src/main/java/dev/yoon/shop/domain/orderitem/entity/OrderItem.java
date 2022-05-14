package dev.yoon.shop.domain.orderitem.entity;


import dev.yoon.shop.domain.base.BaseTimeEntity;
import dev.yoon.shop.domain.item.entity.Item;
import dev.yoon.shop.domain.order.entity.Order;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Table(name = "order_item")
@Entity
public class OrderItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;

    private int count;

}
