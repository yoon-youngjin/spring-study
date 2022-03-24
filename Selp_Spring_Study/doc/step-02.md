## Account 생성, 조회, 수정 API를 간단하게 만드는 예제

1.	도메인 클래스 작성
2.	DTO 클래스를 이용한 Request, Response
3.	Setter 사용X

### 도메인 클래스 작성: Account Domain
```
@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    ...
    ...
    ...

    @Column(name = "zip", nullable = false)
    private String zip;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Builder
    public Account(String email, String fistName, String lastName, String password, String address1, String address2, String zip) {
        this.email = email;
        this.fistName = fistName;
        this.lastName = lastName;
        this.password = password;
        this.address1 = address1;
        this.address2 = address2;
        this.zip = zip;
    }

    public void updateMyAccount(AccountDto.MyAccountReq dto) {
        this.address1 = dto.getAddress1();
        this.address2 = dto.getAddress2();
        this.zip = dto.getZip();
    }
}
```

### 제약조건 맞추기
- 칼럼에 대한 제약조건을 생각하며 작성하는 것이 바람직합니다.
- 대표적으로 `nullable`, `unique` 조건 등 해당 DB의 스키마와 동일하게 설정하는 것이 좋습니다.

### 생성날짜, 수정날짜 값 설정 못하게 하기
-	기본적으로 `setter` 메서드가 모든 멤버 필드에 대해서 없고 생성자를 이용한 Build Pattern 메서드에도 생성, 수정, 날짜를 제외해 `@CreationTimestamp`, `@UpdateTimestamp` 어노테이션을 이용해서 VM시간 기준으로 날짜가 자동으로 입력하게 하거나 데이터베이스에서 자동으로 입력하게 설정하는 편이 좋습니다.
-	`@CreationTimestamp`: `INSERT` 쿼리가 발생할 때, 현재 시간을 값으로 채워서 쿼리를 생성 -> `@InsertTimeStamp` 어노테이션을 사용하면 데이터가 생성된 시점에 대한 관리하는 수고스러움을 덜 수 있다.
-	`@UpdateTimestamp`: `UPDATE` 쿼리가 발생할 때, 현재 시간을 값으로 채워서 쿼리를 생성
```
@CreationTimestamp // INSERT 시 자동으로 값을 채워줌
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @UpdateTimestamp // UPDATE 시 자동으로 값을 채워줌
    private LocalDateTime updatedAt = LocalDateTime.now();
```
-	매번 생성할 때 create 시간을 넣어주고, update할 때 넣어 주고 반복적인 작업과 실수를 줄일 수 있는 효과적인 방법이라고 생각

### 객체 생성 제약
-	`@NoArgsContructor(access= AccessLevel.PROTECTED)` Lombok 어노테이션을 통해서 객체의 직접생성을 외부에서 못하게 설정
-	`@Builder` 어노테이션에 설정돼 있는 `Account` 생성자 메소드를 통해서 해당 객체를 생성할 수 있습니다.

### DTO 클래스를 이용한 Request, Response
#### DTO 클래스
```
public class AccountDto {
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SignUpReq {
        private String email;
        ...
        private String address2;
        private String zip;

        @Builder
        public SignUpReq(String email, String fistName, String lastName, String password, String address1, String address2, String zip) {
            this.email = email;
            ...
            this.address2 = address2;
            this.zip = zip;
        }

        public Account toEntity() {
            return Account.builder()
                    .email(this.email)
                    ...
                    .address2(this.address2)
                    .zip(this.zip)
                    .build();
        }

    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MyAccountReq {
        private String address1;
        private String address2;
        private String zip;

        @Builder
        public MyAccountReq(String address1, String address2, String zip) {
            this.address1 = address1;
            this.address2 = address2;
            this.zip = zip;
        }

    }

    @Getter
    public static class Res {
        private String email;
        ...
        private String address2;
        private String zip;

        public Res(Account account) {
            this.email = account.getEmail();
            ...
            this.address2 = account.getAddress2();
            this.zip = account.getZip();
        }
    }
}
```
### DTO 클래스의 필요 이유
- Account에 정보를 변경하는 API가 있다고 가정했을 경우 RequestBody를 Account 클래스로 받게 된다면 다음과 같은 문제가 발생
- 데이터안정성
     	정보 변경 API에서는 firstName, lastName 두 속성만 변경할 수 있다고 했으면 Account 클래스로 RequestBody를 받게 된다면 email, password, Account 클래스의 모든 속성값들을 컨트롤러를 통해서 넘겨받을 수 있게 되고 원치 않은 데이터 변경이 발생할 수 있다.
     	firstName, lastName 속성 이외의 값들이 넘어온다면 그것은 잘못된 입력값이고 그런 값들을 넘겼을 경우 Bad Request 처리하는 것이 안전합니다.
     	Response 타입이 Account 클래스일 경우 계정의 모든 정보가 노출 되게 됩니다. JsonIgnore 속성들을 두어 임시로 막는 것은 바람직하지 않습니다.속성들을 두어 임시로 막는 것은 바람직하지 않습니다.
- 명확해지는 요구사항
     	MyAccoutReq 클래스에 속하는 필드들은 변경할 수 있는 값들로 address1, address2, zip 속성이 있습니다. 요구사항이 이 세 가지 속성에 대한 변경이어서 해당 API가 어떤 값들을 변경할 수 있는지 명확해집니다.

## Setter 사용 안 하기
-	JPA에서는 영속성이 있는 객체에서 Setter메서드를 통해서 데이터베이스 DML이 가능하게 됩니다. 만약 무분별하게 모든 필드에 대한 Setter 메서드를 작성했을 경우 email 변경 기능이 없는 기획 의도가 있더라도 영속성이 있는 상태에서 Setter 메서드를 사용해서 얼마든지 변경이 가능해지는 구조를 갖게됩니다.
-	DTO 클래스를 이용해서 데이터 변경을 하는 것이 훨씬 더 직관적이고 유지보수 하기 쉽다


