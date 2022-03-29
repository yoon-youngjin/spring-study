package jpabook.jpashop.domain;

import javax.persistence.*;

@Entity
public class Delivery {

    @Id @GeneratedValue
    private Long id;

    @OneToOne(
            fetch = FetchType.LAZY,
            mappedBy = "delivery"
    )
    private Order order;

    private String city;

    private String street;

    private String zopcode;

    private DeliveryStatus status;
}
