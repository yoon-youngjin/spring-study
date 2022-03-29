package jpabook.jpashop.domain;

import javax.persistence.*;

@Entity
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "ORDER_ITEM_ID")
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            targetEntity = Order.class
    )
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    @ManyToOne(
            fetch = FetchType.LAZY,
            targetEntity = Item.class
    )
    @JoinColumn(name = "ITEM_ID")
    private Item item;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
