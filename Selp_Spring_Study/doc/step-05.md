## OneToMany 관계 설정

### 요구사항
-	배송이 있고 배송의 상태를 갖는 배송 로그
-	배송이 있고 배송의 상태를 갖는 배송 로그의 관계는 1:N
-	배송은 배송 상태를 1개 이상 반드시 갖는다.

### Entity
```
@Entity
public class Delivery {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DeliveryLog> logs = new ArrayList<>();
}

@Entity
public class DeliveryLog {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, updatable = false)
    private DeliveryStatus status;

    @ManyToOne
    @JoinColumn(name = "delivery_id", nullable = false, updatable = false)
    private Delivery delivery;
}
    ....
}
```

### Delivery 저장

-	일대다 관계에서는 다 쪽이 외래 키를 관리
-	JPA 상에서는 외래 키가 갖는 쪽이 연관 관계의 주인이 되고 연관 관계의 주인만이 데이터베이스 연관 관계와 매핑되고 외래 키를 관리(등록, 수정, 삭제)할 수 있으므로 DeliveryLog에서 Delivery를 관리
-	**하지만 DeliveryLog는 Delivery 상태를 저장하는 로그 성격이기 떄문에 핵심 비즈니스 로직을 Delivery에서 작성하는 것이 바람직**
-	이럴 때 편의 메소드와 Casacade 타입 PERSIST 이용하면 보다 이러한 문제를 해결할 수 있음




### 편의 메소드
```
class Delivery {
    public void addLog(DeliveryStatus status) {
        this.logs.add(DeliveryLog.builder()
                .status(status)
                .delivery(this) // this를 통해서 Delivery를 넘겨준다.
                .build());
    }
}

class DeliveryLog {
    public DeliveryLog(final DeliveryStatus status, final Delivery delivery) {
        this.delivery = delivery;
    }
}

class DeliveryService {
    public Delivery create(DeliveryDto.CreationReq dto) {
        final Delivery delivery = dto.toEntity();
        delivery.addLog(DeliveryStatus.PENDING);
        return deliveryRepository.save(delivery);
    }
}
```

### CaseCade PERSIST 설정
```
// cascade 없는 경우
Hibernate: insert into delivery (id, address1, address2, zip, created_at, update_at) values (null, ?, ?, ?, ?, ?)

// cascade PERSIST 설정 했을 경우
Hibernate: insert into delivery (id, address1, address2, zip, created_at, update_at) values (null, ?, ?, ?, ?, ?)
Hibernate: insert into delivery_log (id, created_at, update_at, delivery_id, status) values (null, ?, ?, ?, ?)
````
-	CaseCade PERSIST를 통해서 Delivery 엔티티에서 DeliveryLog를 생성할 수 있게 설정
-	PERSIST가 없을 때는 실제 객체에는 저장되지만, 영속성 있는 데이터베이스에 저장에 필요한 INSERT QUERY가 동작 하지 않음
-	**JPA를 잘활용하면 도메인의 의도가 분명하게 들어나도록 개발 가능**

### 고아 객체(orphanRemoval)
-	JPA는 부모 엔티티와 연관 관계가 끊어진 자식 엔티티를 자동으로 삭제하는 기능을 제공
-	고아 객체 제거

### DeliveryLog 삭제
```
public Delivery removeLogs(long id) {
    final Delivery delivery = findById(id);
    delivery.getLogs().clear(); // DeloveryLog 전체 삭제
    return delivery; // 실제 DeloveryLog 삭제 여부를 확인하기 위해 리턴
}
```
```
// delete SQL
Hibernate: delete from delivery_log where id=?
```

Delivery 삭제
```
public void remove(long id){
    deliveryRepository.delete(id);
}
```
```
// delete SQL
Hibernate: delete from delivery_log where id=?
Hibernate: delete from delivery where id=?
```
-	delivery, deliverylog 참조 관계를 맺고 있어 Delivery만 삭제할 수 있음
-	delivery_log부터 제거 이후 delivery를 제거


