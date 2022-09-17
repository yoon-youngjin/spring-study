package dev.yoon.core.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter @Setter
@ToString
public class Order {

    private Long memberId;
    private String itemName;
    private int itemPrice;
    private int discountPrice;

    // 비즈니스 계산로직
    public int calculatePrice() {
        return itemPrice - discountPrice;
    }


}
