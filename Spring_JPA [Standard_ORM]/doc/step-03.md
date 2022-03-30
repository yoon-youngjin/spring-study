## 영속성 컨텍스트
- 엔티티를 영구 저장하는 환경(=EntityManager.persist(entity))

> JPA에서 가장 중요한 2가지
>
> 객체와 관계형 데이터베이스 매핑하기(ORM)
> 
> 영속성 컨텍스트

<p align="center">
     <img src="https://user-images.githubusercontent.com/83503188/160802172-4d5d97d1-2c13-4d2b-adb4-295eb5e3a13c.png" width="600px" />
</p>

- persist 메소드는 DB에 저장하는 개념이 아닌 영속성 컨텍스트에 저장하는 것
- 영속성 컨텍스트는 논리적인 개념 (물리적인 공간 X)
- 엔티티 매니저를 통해서 영속성 컨텍스트에 접근
- 애플리케이션과 DB사이에 중간계층(=영속성 컨텍스트)이 있음


### 엔티티의 생명주기
1. 비영속(new/transient) : 영속성 컨텍스트와 전혀 관계가 없는 **새로운** 상태 
   1. Member m = new Member();
2. 영속(managed): 영속성 컨텍스트에 **관리**되는 상태
   1. EntityManager.find(Member.class, 1);
   2. EntityManager.persist(m);
   3. persist메소드가 호출된다고 db에 저장되는 것이 아님
   4. transaction.commit메소드가 호출될 때 영속성 컨텍스트 상태의 객체를 db에 저장
3. 준영속(detached): 영속성 컨텍스트에 저장되었다가 **분리**된 상태
   1. EntityManager.detach(m);
   2. 회원 엔티티를 영속성 컨텍스트에서 분리
4. 삭제(removed): **삭제**된 상태
   1. EntityManager.remove(m);

<p align="center">
     <img src="https://user-images.githubusercontent.com/83503188/160803284-b917a6f0-3370-491d-bec2-346a75e9f5c1.png" width="600px" />
</p>

### 영속성 컨텍스트의 이점
- 1차 캐시 
- 동일성 보장
- 트랜잭션을 지원하는 쓰기 지연
- 변경 감지
- 지연 로딩

#### 엔티티 조회, 1차 캐시
- 조회 시 DB를 접근하는 것이 아닌 영속 컨텍스트를 먼저 접근하여 찾음
<p align="center">
     <img src="https://user-images.githubusercontent.com/83503188/160803845-181a3ea9-6e6f-41d4-bcbd-a02afb133734.png" width="400px" height="250px"/>
     <img src="https://user-images.githubusercontent.com/83503188/160803974-d1702fa8-72af-4287-9130-2a9b16822bf7.png" width="400px" height="250px"/>
</p>

- 조회 시 영속 컨텍스트 내의 1차 캐시에 없는 경우 DB에 QUERY를 날려 1차 캐시에 저장한 뒤 반환
   - 이후 다시 member2를 조회하면 1차 캐시에서 반환(SELECT SQL X)
<p align="center">
     <img src="https://user-images.githubusercontent.com/83503188/160804557-3ea78963-5237-4176-bac5-d333cae2a0c7.png" width="600px" />
</p>

#### 영속 엔티티의 동일성 보장 
<p align="center">
     <img src="https://user-images.githubusercontent.com/83503188/160804695-a3a5e5ae-a676-4831-98e9-22d410fb579c.png" width="600px" />
</p>

#### 엔티티 등록 

- 트랜잭션을 지원하는 쓰기 지연
<p align="center">
     <img src="https://user-images.githubusercontent.com/83503188/160805285-c81fc933-13dd-4f8a-a2b7-91eed5667173.png" width="400px" height="250px"/>
     <img src="https://user-images.githubusercontent.com/83503188/160805257-5d33fb89-ef13-4ed6-b396-e1e6d356985a.png" width="400px" height="250px"/>
     <img src="https://user-images.githubusercontent.com/83503188/160805302-272e5504-270d-42fc-a306-1a5941f27567.png" width="400px" height="250px"/>
</p>

#### 엔티티 수정
- **변경 감지(Dirty Checking)**
<p align="center">
     <img src="https://user-images.githubusercontent.com/83503188/160805622-86dabd30-50f0-4cc6-b5ff-ce63e856c59f.png" width="400px" height="250px"/>
     <img src="https://user-images.githubusercontent.com/83503188/160805641-66b6f0a9-528f-4e19-b1d0-ef6c1c901719.png" width="400px" height="250px"/>
</p>

- JPA는 Transaction을 commit하는 시점에서 내부적으로 flush를 호출
- Entity와 스냅샷(=최초 시점의 상태)을 비교
- 비교 후 변경을 감지하여 UPDATE QUERY를 DB에 반영
#### 플러시
- 영속성 컨텍스트의 변경 내용을 데이터 베이스에 반영
- 플러시 발생
  - 변경 감지
  - 수정된 엔티티 쓰기 지연 SQL 저장소에 등록
  - 쓰기 지연 SQL 저장소의 QUERY를 DB에 전송(등록, 수정, 삭제, ... 쿼리)
- 영속성 컨텍스트를 플러시하는 방법
  - em.flush(): 직접 호출
  - 트랜잭션 커밋 – 플러시 자동 호출
  - JPQL 쿼리 실행 – 플러시 자동 호출
- JPQL 쿼리 실행시 플러시가 자동 으로 호출되는 이유
<p align="center">
     <img src="https://user-images.githubusercontent.com/83503188/160807086-c7137b9f-abb0-422b-80a5-bad7b10de86a.png" width="600px" />
</p>

   - JPQL실행을 통해 플러시가 자동으로 호출되어 member가 모두 조회 가능
   - 문제되는 걸 막고자 JPQL실행 시 자동으로 flush 호출

- 플러시 모드 옵션
  - FlushModeType.AUTO: 커밋이나 쿼리를 실행할 때 플러시 (기본값)
  - FlushModeType.COMMIT: 커밋할 때만 플러시

- 플러시 정리
  - 영속성 컨텍스트를 비우지 않음
  - 영속성 컨텍스트의 변경내용을 데이터베이스에 동기화 
  - 트랜잭션이라는 작업 단위가 중요 -> 커밋 직전에만 동기화하면 됨

### 준영속 상태
- 영속 상태의 엔티티가 영속성 컨텍스트에서 분리(detached)
- 영속성 컨텍스트가 제공하는 기능을 사용 못함

#### 준영속 상태로 만드는 방법
1. em.detach(entity): 특정 엔티티만 준영속 상태로 전환
2. em.clear(): 영속성 컨텍스트를 완전히 초기회
3. em.close(): 영속성 컨텍스트를 종료
