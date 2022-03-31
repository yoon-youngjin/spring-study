## 엔티티 매핑
1. 객체와 테이블 매핑: @Entity, @Table
2. 필드와 컬럼 매핑: @Column
3. 기본 키(PK) 매핑: @Id
4. 연관관계 매핑: @ManyToOne, @JoinColumn

### 객체와 테이블 매핑
- @Entity가 붙은 클래스는 JPA가 관리하는 엔티티라 한다.

- 주의 !
  - 기본 생성자 필수
  - final 클래스, enum, interface, inner 클래스 사용X
  - 저장할 필드에 final 사용X  

[//]: # (> 기본 생성자가 필수인 이유 )

[//]: # (> )

[//]: # (> 위의 문제를 알기 위해서는 Java Reflection부터 알아야 한다.)

[//]: # (> )

[//]: # (> Java Reflection: 구체적인 클래스 타입을 알지 못해도 그 클래스의 메소드, 타입, 변수들에 접근할 수 있도록 해주는 Java API)

[//]: # (> )

[//]: # (> 단, Java Reflection을 통해 가져오지 못하는 정보 중 하나가 바로 생성자의 인자 정보들 )

[//]: # (> )

[//]: # (> 따라서 기본 생성자 없이 파라미터가 있는 생성자만 존재한다면 Java Reflection이 객체를 생성할 수 없음)

[//]: # (> )

[//]: # (> 지연 로딩을 설정하게 되면 proxy 객체를 사용하는데 proxy 객체는 해당 엔티티를 상속 받아서 만들게 된다.)

[//]: # (> )

[//]: # (> 따라서 상속을 해야하므로 public 혹은 protected 기본 생성자가 필요하게 된다.)
    
- @Entity
  - name 속성: JPA에서 사용할 엔티티 이름을 지정
  - 기본값: 클래스 이름을 그대로 사용

- @Table: 엔티티와 매핑할 테이블 지정
  - Name: 매핑할 테이블 이름 -> 기본값: 엔티티 이름을 사용
  - Catalog: 데이터베이스 catalog 매핑
  - Schema: 데이터베이스 schema매핑
  - uniqueConstraints(DDL): DDL 생성 시에 유니크 제약 조건 생성

#### 데이터베이스 스키마 자동 생성 - 속성
- hibernate.hbm2ddl.auto
1. create: 기존테이블 삭제 후 다시 생성
2. create-drop: create와 같으나 종료시점에 테이블 DROP
3. update: 변경분만 반영(운영DB에는 사용하면 안됨)
   1. 기존 엔티티에 필드가 추가된 경우 drop하지 않고 alter하여 컬럼 추가
4. validate: 엔티티와 테이블이 정상 매핑되었는지만 확인
   1. 기존 엔티티에 존재하지 않던 필드가 추가된 경우 에러 발생
5. none: 사용하지 않음, 기본값

#### 데이터베이스 스키마 자동 생성 - 주의

1. **운영 장비에는 절대 create, create-drop, update 사용하면 안된다.**
2. 개발 초기 단계는 create 또는 update
3. 테스트 서버는 update 또는 validate
4. 스테이징과 운영 서버는 validate 또는 none

### DDL 생성 기능
1. 제약조건 추가: 회원 이름은 필수, 10자 초과X
   1. @Column(nullable = false, length = 10)
2. 유니크 제약조건 추가
   1. @Column(unique = true)
   2. @Table(uniqueContraints={@UniqueContraints(name=”NAME_AGE_UNIQUE”, columnNames ={“NAME”,”AGE”} )})

### 필드와 컬럼 매핑

#### 요구사항 추가
- 회원은 일반 회원과 관리자로 구분해야 한다.
```
@Enumerated(EnumType.STRING)
private RoleType roleType;
```
- 회원 가입일과 수정일이 있어야 한다.
  - TemporalType.DATE: 날짜
  - TemporalType.TIME: 시간
  - TemporalType.TIMESTAMP: 날짜,시간

- 회원을 설명할 수 있는 필드가 있어야 한다. 이 필드는 길이 제한이 없다.
  - @Lob
  
> 엔티티에 DB와 관련 없는 필드 만들기 ?
> 
> @Transient

<p align="center">
     <img src="https://user-images.githubusercontent.com/83503188/160976497-3deb14cd-af8b-4d01-8c7b-93d559dce56f.png" width="600px" />
</p>

- Unique의 경우 필드에 맵핑하면 아이디값이 이상하게 나와서 필드에 맵핑하지 않고 테이블에 맵핑함

#### @Enumerated
- EnumType.ORDINAL: enum 순서를 데이터베이스에 저장 -> **사용 x** 
- EnumType:STRING: enum 이름을 데이터베이스에 저장

#### @Temporal
- 날짜 타입(java.util.Date, java.util.Calender)을 매핑할 때 사용 -> 지금 사용 x
- LocalDate, LocalDateTime을 사용할 때는 생략 가능(최신 하이버네이트 지원)

#### @Lob
- @Lob에는 지정할 수 있는 속성이 없다.
- 매핑하는 필드 타입이 문자면 CLOB 매핑, 나머지는 BLOB 매핑
  - CLOB: String, char[], java.sql.CLOB
  - BLOB: byte[], java.sql. BLOB

#### @Transient
- 필드 매핑X
- 데이터베이스에 저장X, 조회X
- 주로 메모리상에서만 임시로 어떤 값을 보관하고 싶을 때 사용

### 기본 키(PK) 매핑 
- 직접 할당: @Id만 사용
- 자동 생성: @Id, @GeneratedValue
  - @GeneratedValue(strategy = GenerationType.AUTO): DB방언에 맞춰서 자동 생성
    - 
  - @GeneratedValue(strategy = GenerationType.IDEENTITY): 기본 키 생성을 데이터베이스에 위임 -> ex. MYSQL의 경우 AUTO_INCREMENT
    - ID를 맵핑 하지 않고 persist하기 때문에 DB에 접근하는 시점이 되면 id값을 알 수 있다.
    - IDENTITY전략만 예외적으로 em.persist하는 시점에 insert 쿼리를 날린다.
    - 기본적으로 em.commit시점에 insert쿼리가 나감
      <p align="center">
        <img src="https://user-images.githubusercontent.com/83503188/160977193-90936bfb-b3f4-4d30-b4b3-70e95f3d7305.png" width="600px" />
      </p>
  - @GeneratedValue(strategy = GenerationType.SEQUENCE): sequence object를 통해 생성
    - 데이터베이스 시퀀스는 유일한 값을 순서대로 생성하는 특별한 데이터베이스 오브젝트
    - 데이터베이스 시퀀스는 실제로 존재하는 오브젝트
    - em.persist하는 시점에 시퀀스 오브젝트를 call하여 sequence의 다음 값을 얻어와서 id값을 맵핑 해주고 영속성 컨텍스트에 저장, IDENTITY전략과 다르게 현 시점에 insert쿼리가 날라가지 않는다.
    - 버퍼링 가능
    - allocationSize: 시퀀스 한 번 호출에 증가하는 수(성능 최적화에 사용됨), 미리 가져올 수를 정함, 디폴트 값: 50 == next콜 하나에 50개 get
      <p align="center">
             <img src="https://user-images.githubusercontent.com/83503188/160977440-0b93cbdc-64d5-4ca9-aae0-c6a1686e3c1e.png" width="600px" />
      </p>
  - @GeneratedValue(strategy = GenerationType.TABLE): 키 생성 전용 테이블을 하나 만들어서 데이터베이스 시퀀스를 흉내내는 전략
    <p align="center">
           <img src="https://user-images.githubusercontent.com/83503188/160977590-be60e36d-54ad-41a7-94e4-a96143848ffc.png" width="600px" />
    </p>

