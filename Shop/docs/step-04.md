# Chapter 5: 연관 관계 매핑

엔티티들은 대부분 다른 엔티티와 연관 관계를 맺고 있다. JPA에서는 엔티티에 연관 관계를 매핑해두고 필요할 때 해당 엔티티와 연관된 엔티티를 사용하여 좀 더 객체지향적으로 프로그래밍할 수 있도록 도와준다.

- [x] 일대일(1:1): `@OneToOne`
- [x] 일대다(1:N): `@OneToMany`
- [x] 다대일(N:1): `@ManyToOne`
- [x] 다대다(N:N): `@ManyToMany`

엔티티를 매핑할 때는 방향성을 고려해야 한다. 테이블에서 관계는 항상 양방향이지만, 객체에서는 단방향과 양방햐이 존재한다. 

## 연관 관계 매핑 종류

### 일대일 단방향 매핑하기

```java
@Table(name = "cart")
@Getter
@Entity
public class Cart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // 1)
    @JoinColumn(name = "member_id") // 2)
    private Member member;
    
}
```

1. `@OneToOne` 어노테이션을 이용해 멤버 엔티티와 일대일 매핑
2. `@JoinColumn` 어노테이션을 이용해 매핑할 외래키(FK)를 지정한다. name 속성에는 매핑할 외래키의 이름을 설정한다. name을 명시하지 않으면 JPA가 알아서 ID를 찾지만 컬럼명이 원하는 대로 생성되지 않을 수 있기 때문에 직접 지정한다.

### 다대일 단방향 매핑하기

![image](https://user-images.githubusercontent.com/83503188/168439607-3654c05e-18f6-4d58-8c2d-16095d95963b.png)


하나의 장바구니(Cart)에는 여러 개의 상품(CartItem)이 들어갈 수 있다. 또한 같은 상품을 여러 개 주문할 수도 있으므로 몇 개를 담아 줄 것인지도 설정해줘야 한다.

```java
@Entity
@Getter
@Table(name = "cart_item")
public class CartItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart; // 1)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; // 2)
    
    private int count; // 3)
    
}

```

1. 하나의 장바구니에는 여러 개의 상품을 담을 수 있으므로 `@ManytoIne` 어노테이션을 이용하여 다대일 관계로 매핑
2. 장바구니에 담을 상품의 정보를 알아야 하므로 상품 엔티티를 매핑해준다. 하나의 상품은 여러 장바구니의 장바구니 상품으로 담길 수 있으므로 마찬가지로 `@ManyToone` 어노테이션을 이용하여 다대일 관계로 매핑한다.
3. 같은 상품을 장바구니에 몇 개 담을지 저장한다.

### 다대일/일대다 양방향 매핑하기

![image](https://user-images.githubusercontent.com/83503188/168440596-3dc81774-9455-4023-bd6e-7b591f6ced15.png)

```java
@Getter
@Table(name = "order_item")
@Entity
public class OrderItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; // 1)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; // 2)
    
    private int orderPrice;
    
    private int count;
    
}
```

1. 하나의 상품은 여러 주문 상품으로 들어갈 수 있으므로 주문 상품 기준으로 다대일 단방향 매핑을 설정한다.
2. 한 번의 주문에 여러 개의 상품을 주문할 수 있으므로 주문 상품 엔티티와 주문 엔티티를 다대일 단방향 매핑을 설정한다.




```java
@Getter
@Table(name = "orders")
@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order") // 1)
    private List<OrderItem> orderItems = new ArrayList<>(); // 2)
    
}
```

1. 주문 상품 엔티티와 일대다 매핑을 한다. 외래키(order_id)가 order_item 테이블에 있으므로 연관 관계의 주인은 OrderItem 엔티티, Order 엔티티가 주인이 아니므로 `mappedBy` 속성으로 연관 관계의 주인을 설정한다. 속성의 값으로 `order`를 적은 이유는 OrderItem에 있는 Order에 의해 관리된다는 의미로 해석하면 된다. 
2. 하나의 주문이 여러 개의 주문 상품을 갖으므로 List 자료형을 사용해서 매핑한다.

테이블은 외래키 하나로 양방향 조회가 가능하는데 반해 엔티티는 양방향 연관 관계로 설정하면 객체의 참조는 둘인데 외래키는 하나이므로 둘 중 누가 외래키를 관리할지를 정해야 한다.

- 연관 관계의 주인은 외래키가 있는 곳으로 설정
- 연관 관계의 주인이 외래키를 괸리(등록, 수정, 삭제)
- 주인이 아닌 쪽은 연관 관계 매핑 시 mappedBy 속성 값으로 연관 관계 주인을 설정
- 주인이 아닌 쪽은 읽기만 가능

### 다대다 매핑하기

다대다 매핑은 실무에서는 사용하지 않는 매핑 관계, 관계형 데이터베이스는 정규화된 테이블 2개로 다대다를 표현할 수 없다. 따라서 연결 테이블을 생성해서 다대다 관계를 일대다, 다대일 관계로 풀어낸다.

다대다 매핑을 사용하지 않는 이유는 연결 테이블에는 컬럼을 추가할 수 없기 때문이다.(연결 테이블에는 2개의 아이디만 존재) 연결 테이블에는 조인 컬럼뿐 아니라 추가 컬럼들이 필요한 경우가 많다. 또한 엔티티를 조회할 때 member 엔티티에서 item을 조회하면 중간 테이블이 있기 때문에 어떤 쿼리문이 실행될지 예측하기도 쉽지 않다. 

따라서 연결 테이블용 엔티티를 하나 생성한 후 일대다 다대일 관계로 매핑을 하면 된다.



