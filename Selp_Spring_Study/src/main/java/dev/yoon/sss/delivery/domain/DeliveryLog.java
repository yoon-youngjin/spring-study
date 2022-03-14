package dev.yoon.sss.delivery.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.yoon.sss.common.BaseTimeEntity;
import dev.yoon.sss.common.DateTime;
import dev.yoon.sss.delivery.exception.DeliveryAlreadyDeliveringException;
import dev.yoon.sss.delivery.exception.DeliveryStatusEqaulsException;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "delivery_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, updatable = false)
    private DeliveryStatus status;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "delivery_id", nullable = false, updatable = false)
    private Delivery delivery;

    @Embedded
    private DateTime dateTime;

    @Transient
    // 테이블에 매핑시키지 않을 필드
    private DeliveryStatus lastStatus;

    @Builder
    public DeliveryLog(final DeliveryStatus status, final Delivery delivery) {
        verifyStatus(status, delivery);
        setStatus(status);
        this.delivery = delivery;
    }

    private void verifyStatus(DeliveryStatus status, Delivery delivery) {
        if (!delivery.getLogs().isEmpty()) {
            lastStatus = getLastStatus(delivery);
            verifyLastStatusEquals(status);
            verifyAlreadyCompleted();
        }
    }

    private DeliveryStatus getLastStatus(Delivery delivery) {
        final int lastIndex = delivery.getLogs().size() - 1;
        return delivery.getLogs().get(lastIndex).getStatus();
    }

    private void setStatus(final DeliveryStatus status) {
        switch (status) {
            case DELIVERING:
                delivering();
                break;
            case COMPLETED:
                completed();
                break;
            case CANCELED:
                cancel();
                break;
            case PENDING:
                pending();
                break;
            default:
                throw new IllegalArgumentException(status.name() + " is not found");
        }
    }

    private void pending() {
        this.status = DeliveryStatus.PENDING;
    }

    private void cancel() {
        verifyNotYetDelivering();
        this.status = DeliveryStatus.CANCELED;
    }

    private void completed() {
        this.status = DeliveryStatus.COMPLETED;
    }

    private void delivering() {
        this.status = DeliveryStatus.DELIVERING;
    }

    private void verifyNotYetDelivering() {
        if (isNotYetDelivering()) throw new DeliveryAlreadyDeliveringException();
    }

    private boolean isNotYetDelivering() {
        return this.lastStatus != DeliveryStatus.PENDING ;
    }

    private void verifyAlreadyCompleted() {
        if (isCompleted())
            throw new IllegalArgumentException("It has already been completed and can not be changed.");
    }

    private void verifyLastStatusEquals(DeliveryStatus status) {
        if (lastStatus == status) throw new DeliveryStatusEqaulsException(lastStatus);
    }

    private boolean isCompleted() {
        return lastStatus == DeliveryStatus.COMPLETED;
    }


}
