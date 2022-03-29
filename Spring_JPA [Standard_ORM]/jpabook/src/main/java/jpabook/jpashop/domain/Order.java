package jpabook.jpashop.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORDERS")
public class Order extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ORDER_ID")
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "DELIVERY_ID"
    )
    private Delivery delivery;

    @OneToMany(
            cascade = CascadeType.ALL,
            mappedBy = "order"
    )
    private List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime orderDate; // ORDER_DATE, order_date(스프링 부트로 하이버네이트를 걸어서 jpa에 올리면 'order_date'로 올라감=> 변경할 수 있음)

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;



















    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
}
