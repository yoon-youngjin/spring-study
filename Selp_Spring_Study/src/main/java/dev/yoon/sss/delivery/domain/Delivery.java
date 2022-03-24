package dev.yoon.sss.delivery.domain;

import dev.yoon.sss.Account.domain.Address;
import dev.yoon.sss.common.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "delivery")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Delivery extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Embedded
    private Address address;

    /**
     * Delivery: 1 <-> DeliveryLog: *
     */
    @OneToMany(mappedBy = "delivery", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DeliveryLog> logs = new ArrayList<>();

//    @Embedded
//    private DateTime dateTime;

    @Builder
    public Delivery(Address address) {
        this.address = address;
    }

    public void addLog(DeliveryStatus status) {
        this.logs.add(buildLog(status));
        this.logs.get(0).setCreatedDate(LocalDateTime.now());
        this.logs.get(0).setModifiedDate(LocalDateTime.now());
    }

    private DeliveryLog buildLog(DeliveryStatus status) {
        return DeliveryLog.builder()
                .status(status)
                .delivery(this)
                .build();
    }
}
