# Chapter 7: 주문

## 주문 기능 구현하기

고객이 상품을 주문하면 현재 상품의 재고에서 주문 수량만큼 재고를 감소시켜야 한다. 고객이 주문을 했는데 실제 재고가 없다면 배송을 하지 못하고 결품 처리가 되기 때문에 주문 수량만큼 상품의 재고를 감소시켜야 한다. 
또한 주문 수량이 현재 재고 수보다 클 경우 주문이 되지 않도록 구현해야 한다.

```java
public class OutOfStockException extends BusinessException {

    public OutOfStockException(String msg) {
        super(msg);
    }
}

```
상품의 주문 수량보다 재고의 수가 적을 때 발생시킬 exception을 정의한다.

```java
public class Item extends BaseEntity {

    ...
    
    public void removeStock(int stockNumber) {
        int restStock = this.stockNumber - stockNumber;

        if (restStock < 0) {
            throw new OutOfStockException(ErrorCode.OUT_OF_STOCK.getMessage() + String.format("(현재 재고 수량: %d)", this.stockNumber));
        }
        if (restStock == 0) {
            this.itemSellStatus = ItemSellStatus.SOLD_OUT;
        }
        this.stockNumber = restStock;
    }
}

```
엔티티 클래스 안에 비즈니스로직을 메소드로 작성하면 코드의 재사용과 데이터의 변경 포인트를 한군데로 모을 수 있다는 장점이 있다.


```java
public class OrderItem extends BaseEntity {

    ...

    public static OrderItem createOrderItem(Item item, int count) {
        /**
         * 주문 상품을 생성한다는 것은 주문 수량만큼 상품의 재고를 차감하는 것
         */
        item.removeStock(count); // 1)

        return OrderItem.builder()
                .orderPrice(item.getPrice())
                .count(count)
                .item(item)
                .build();
    }

    public int getTotalPrice() {
        return this.getOrderPrice() * this.getCount();
    }

}

```

주문할 상품과 주문 수량을 통해 OrderItem 객체를 만드는 메소드를 작성한다. 

1. 주문 상품을 생성한다는 것은 주문 수량만큼 상품의 재고를 차감하는 것

```java
public class Order  extends BaseEntity {

    ...

    public static Order createOrder(Member member, List<OrderItem> orderItems) {

        return Order.builder()
                .orderStatus(OrderStatus.ORDER)
                .member(member)
                .orderItems(orderItems)
                .build();
        
    }
    public int getTotalPrice() {
        
        int totalPrice = this.orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice).sum();
        return totalPrice;
        
    }
}

```

