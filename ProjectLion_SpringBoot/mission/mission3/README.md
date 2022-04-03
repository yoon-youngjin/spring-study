# Misson 3

# 데이터를 Database에서 관리하기

이전 미션에서 Post와 Board를 관리하는 서비스를 만들었습니다. 해당 서비스는 어플리케이션이 종료되면 데이터가 다 사라지게 되어있습니다. 이제 어플리케이션이 꺼져도 데이터가 유지될 수 있도록 JPA를 사용해 테이블을 구성합시다.

# 공통 미션

Chapter 6의 Spring Boot Properties를 활용하여, 기초 개발 단계에서는 H2 데이터베이스를 사용하도록 하며, 개발 환경과 상용환경의 데이터베이스를 구분하여 실행할 수 있도록 합시다.

## Basic Mission

커뮤니티 사이트에 데이터베이스 추가

이전 Basic Mission에서 만들었던 서비스에서 사용한 DTO를 기반으로 Entity를 만들어 관리해 봅시다.

1. `PostEntity` 와 `BoardEntity` 를 만들어 봅시다.
2. `PosetEntity` 와 `BoardEntity` 의 관계를 표현해 봅시다.
    1. `@ManyToOne` , `@OneToMany`, `@JoinColumn` 을 적절히 사용합시다.
3. `PostEntity` 의 작성자를 저장하기 위한 `UserEntity` 를 만들고, 마찬가지로 관계를 표현해 봅시다.

### 세부 조건

1. `UserEntity` 에 대한 CRUD를 작성합시다.
2. `Post` 를 작성하는 단계에서, `User` 의 정보를 어떻게 전달할지 고민해 봅시다.

## Challenge Mission

목적을 가진 커뮤니티 사이트 만들기

Basic Mission에서 만들었던 서비스를 바탕으로, 좀더 다양한 정보를 가진 서비스를 만들어 봅시다.

1. 위치정보를 담기 위한 `AreaEntity` 를 만들어 봅시다.
    1. ‘도, 광역시’, ‘시,군,구’, ‘동,면,읍’ 데이터를 따로 저장할 수 있도록 합시다.
    2. ‘위도’, ‘경도’ 데이터를 저장할 수 있도록 합시다.
2. 사용자 정보를 담는 `UserEntity` 를 Basic Mission과 유사하게 만들되, 사용자를 두가지로 분류할 수 있도록 합시다.
    1. 위에 만든 `AreaEntity` 에 대한 정보를 담을 수 있도록 합시다. 이 정보는 자신의 거주지를 담기 위한 정보입니다.
    2. `UserEntity` 는 사용자 하나를 나타내며, 일반 사용자 또는 상점 주인인지에 대한 분류가 되어야 합니다.
3. 특정 `UserEntity` 만 가질 수 있는 `ShopEntity` 를 작성합시다. 또, 해당 `ShopEntity` 가 취급하는 품목에 대한 `Cateogory` 를 어떻게 다룰지 생각하여 나타낼 수 있도록 합시다.
    1. `ShopEntity` 는 어디 지역의 상점인지에 대한 정보를 가지고 있어야 합니다.
4. 마지막으로 `ShopEntity` 에 대한 게시글인, `ShopPostEntity` 와 `ShopReviewEntity` 를 작성해 봅시다.
    1. `ShopReviewEntity` 는 어떤 사용자든 작성할 수 있으나, `ShopPostEntity` 는 해당 `ShopEntity` 에 대한 주인 `UserEntity` 만 작성할 수 있어야 합니다.

### 세부 조건

1. 생성된 테이블의 실제 이름에는 `Entity` 라는 문구가 들어가지 않도록 `@Table` 어노테이션을 활용합시다.
2. 변동될 가능성이 있는 데이터와 변동될 가능성이 없는 데이터를 잘 구분하여, `Entity` 작성 여부를 잘 판단합시다.
3. `Entity` 를 먼저 구성하되, 시간이 남으면 CRUD까지 구성해 봅시다.