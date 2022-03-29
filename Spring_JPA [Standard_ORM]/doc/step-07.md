## JPA 소개

### SQL 중심적인 개발의 문제점
> 대부분의 언어가 객체지향 언어를 DB는 관계형 DB를 사용

- 무한 반복, 지루한 코드 (반복되는 CRUD코드)
  - 새로운 필드 추가 시 기존의 모든 쿼리를 수정
<p align="center">
  <img src="https://user-images.githubusercontent.com/83503188/160542658-f4183780-ab45-4c70-baeb-b098d22dafdd.png" width="400px" height="250px"/>
  <img src="https://user-images.githubusercontent.com/83503188/160542688-db9b6cc1-f9a0-419d-80d0-7ec8d572065f.png" width="400px" height="250px"/>
</p>

- SQL에 의존적인 개발을 피하기 어렵다.

- 패러다임의 불일치: 객체 vs. 관계형 DB

- 객체를 관계형 DB에 저장하기 위해 객체를 SQL로 변환해야 함
  - 개발자가 SQL매퍼 일을 처리

#### 객체와 관계형 데이터베이스의 차이
1. 상속: 객체 -> O, 관계형 DB -> X
2. 연관관계: 객체 -> O(Reference), 관계형 DB -> O(PK,FK를 통해 JOIN)
   1. 객체의 연관관계는 단방향, 테이블의 연관관계는 양방향
<p align="center">
     <img src="https://user-images.githubusercontent.com/83503188/160544020-79123f54-1930-42b8-bcd2-de5414810fa3.png" width="600px" />
</p>


### JPA(Java Persistence API)
- ORM(Object-relational mapping) : 객체 관계 매핑 => 객체는 객체대로 설계, 관계형 데이터베이스는 관계형 데이터베이스대로 설계 후 ORM 프레임워크가 중간에서 매핑
- 현재 사용중인 ORM이 하이버네이트
> ORM? 
> 
> 객체와 관계형 데이터베이스의 데이터를 자동으로 매핑(연결)해주는 것

- JPA 동작
<p align="center">
     <img src="https://user-images.githubusercontent.com/83503188/160544445-92f7b41e-176b-4b39-812a-e86bcea794b2.png" width="400px" height="250px"/>
     <img src="https://user-images.githubusercontent.com/83503188/160544489-dac54627-6de4-4b91-a390-b1d69cbc3994.png" width="400px" height="250px"/>
</p>


#### JPA를 왜 사용해야 하는가?
1. SQL 중심적인 개발에서 객체 중심으로 개발

2. 생산성
   <p align="center">
     <img src="https://user-images.githubusercontent.com/83503188/160544944-977b4fab-3771-42d8-9ab2-a616957d2a3c.png" width="600px" />
   </p>
3. 유지보수
   <p align="center">
     <img src="https://user-images.githubusercontent.com/83503188/160545072-ca462cfd-0727-4575-97ac-00509f5d9fe5.png" width="400px" height="250px"/>
     <img src="https://user-images.githubusercontent.com/83503188/160545099-54e8cfd0-6587-41e3-94d4-29dba3538399.png" width="400px" height="250px"/>
    </p>
4. 패러다임의 불일치 해결
5. 성능
   1. 1차 캐시와 동일성 보장 -> 같은 트랜잭션 안에서는 같은 엔티티를 반환
   2. 트랜잭션을 지원하는 쓰기 지연(transactional write-behind) - 버퍼링 기능
      1. 트랜잭션을 커밋할 때까지 INSERT SQL을 모음
      2. JDBC BATCH SQL 기능을 사용해서 한번에 SQL 전송
         <p align="center">
           <img src="https://user-images.githubusercontent.com/83503188/160545531-2384b554-24ed-491c-b39d-5acfcd7ebb28.png" width="400px" height="250px"/>
           <img src="https://user-images.githubusercontent.com/83503188/160545558-48bc666a-3d18-470c-883c-b5c92b9c1b47.png" width="400px" height="250px"/>
         </p>
   3. 지연 로딩(Lazy Loading), 즉시로딩(Eager Loading)
      1. 지연로딩: 객체가 실제 사용(=getTeam())될 때 로딩, 쿼리가 너무 많이 나감
      2. 즉시로딩: JOIN SQL로 한번에 연관된 객체까지 미리 조회
         <p align="center">
           <img src="https://user-images.githubusercontent.com/83503188/160545779-169919a1-8dce-4bf8-b2f0-95fd5ade7ee8.png" width="600px" />
         </p>
