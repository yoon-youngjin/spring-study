package dev.yoon.sss.delivery.exception;

import dev.yoon.sss.delivery.domain.DeliveryStatus;

public class DeliveryStatusEqaulsException extends RuntimeException {


    private DeliveryStatus status;

    public DeliveryStatusEqaulsException(DeliveryStatus status) {
        super(status.name() + " It can not be changed to the same state.");
        this.status = status;
    }
}
