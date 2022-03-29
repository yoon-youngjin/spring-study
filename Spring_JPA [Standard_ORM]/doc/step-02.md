## JPA 시작

### JPA 구동 방식
<p align="center">
     <img src="https://user-images.githubusercontent.com/83503188/160547315-68aaabdf-e1ad-49ab-9fb2-05c759a6b016.png" width="600px" height="300px"/>
</p>

#### 객체와 테이블을 생성하고 매핑하기
- @Entity: JPA가 관리할 객체
- @Id: 데이터베이스 PK와 매핑
```
@Entity 
public class Member {
   @Id
   private Lond id;
   private String name;
   ...
}

create table Member (
   id bigint not null,
   name varchar(255),
   primary key (id)
);
```
#### 주의
- **엔티티 매니저 팩토리**는 하나만 생성해서 애플리케이션 전체에
  서 공유
- **엔티티 매니저**는 쓰레드간에 공유X (사용하고 버려야 한다)
- **JPA의 모든 데이터 변경은 트랜잭션 안에서 실행**

#### 수정
- Java객체에서 값만 바꿔도 JPA를 통해 객체를 가져오면(=em.find) JPA가 관리하게 된다.
  - JPA가 값이 변경되었는지 트랜잭션을 커밋하는 시점에 검사함
- 변경된 값이 있는 경우 update query를 날림
    
### JPQL 소개
- 나이가 18살 이상인 회원을 모두 검색하고 싶다면? JPQL
- JPA를 사용하면 엔티티 객체를 중심으로 개발
- 문제는 검색 쿼리
  - 검색을 할 때도 테이블이 아닌 엔티티 객체를 대상으로 검색
  - 모든 DB데이터를 객체로 변환해서 검색하는 것은 불가능
  - 애플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요

- JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어 제공
- SQL과 문법 유사,  SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원
- JPQL과 SQL의 차이점
  - **JPQL은 엔티티 객체**를 대상으로 쿼리
  - **SQL은 데이터베이스 테이블**을 대상으로 쿼리
