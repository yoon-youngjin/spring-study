## 연관관계 매핑
> 용어
>
> 방향: 단방향, 양방향 
> 
> 다중성: 다대일, 일대다, 일대일, 다대다 
> 
> 연관관계의 주인: 객체 양방향 연관관계는 관리 주인이 필요

### 연관관계가 필요한 이유

#### 객체를 테이블에 맞추어 모델링 

<p align="center">
     <img src="https://user-images.githubusercontent.com/83503188/160978596-e193f769-1bd9-4990-927c-7e03bc71ff10.png" width="600px" />
</p>

```
   @Entity
   public class Member {
      @Id @GeneratedValue
      private Long id;
      
      @Column(name = "USERNAME")
      private String name;
      
      @Column(name = "TEAM_ID")
      private Long teamId;
   …
}
   @Entity
   public class Team {
      @Id @GeneratedValue
      private Long id;
      
      private String name;
   …
}

   //팀 저장
   Team team = new Team();
   team.setName("TeamA");
   em.persist(team);
   //회원 저장
   Member member = new Member();
   member.setName("member1");
   member.setTeamId(team.getId());
   em.persist(member);
   
   //조회
   Member findMember = em.find(Member.class, member.getId());
   //연관관계가 없음
   Team findTeam = em.find(Team.class, team.getId());

```

- 협력 관계를 만들 수 없다.
- **테이블은 외래 키로 조인**을 사용해서 연관된 테이블을 찾는다.
- **객체는 참조**를 사용해서 연관된 객체를 찾는다.
- 테이블과 객체 사이에는 이런 큰 간격이 있다.


### 단방향 연관관계
<p align="center">
     <img src="https://user-images.githubusercontent.com/83503188/160978977-95df5858-5bd2-46e5-a57c-5af379fdc14a.png" width="600px" />
</p>

```
@Entity
public class Member {
   @Id @GeneratedValue
   private Long id;
   @Column(name = "USERNAME")
   private String name;
   private int age;
   // @Column(name = "TEAM_ID")
   // private Long teamId;
   @ManyToOne
   @JoinColumn(name = "TEAM_ID")
   private Team team;
…
}

   //팀 저장
   Team team = new Team();
   team.setName("TeamA");
   em.persist(team);
   //회원 저장
   Member member = new Member();
   member.setName("member1");
   member.setTeam(team); //단방향 연관관계 설정, 참조 저장
   em.persist(member);


   //조회
   Member findMember = em.find(Member.class, member.getId());
   //참조를 사용해서 연관관계 조회
   Team findTeam = findMember.getTeam();
   
   // 새로운 팀B
   Team teamB = new Team();
   teamB.setName("TeamB");
   em.persist(teamB);
   // 회원1에 새로운 팀B 설정
   member.setTeam(teamB);
```

> persist ?
> 
> PK 자동 생성

### 양방향 연관관계와 연관관계의 주인
<p align="center">
     <img src="https://user-images.githubusercontent.com/83503188/160979302-aa73f0ef-5488-4efb-834c-dbd494adecd6.png" width="600px" />
</p>

```
@Entity
public class Member {
   @Id @GeneratedValue
   private Long id;

   @Column(name = "USERNAME")
   private String name;

   private int age;

   @ManyToOne
   @JoinColumn(name = "TEAM_ID")
   private Team team;
   }
   
public class Team {
      @Id @GeneratedValue
      private Long id;
      
      private String name;
      
      @OneToMany(mappedBy = "team")
      List<Member> members = new ArrayList<Member>();
      …
     }

```

#### 연관관계 주인과 mappedBy
- 객체와 테이블간의 연관관계의 차이?
  - 객체 연관관계 = 2개
  - 회원 -> 팀 연관관계 1개(단방향)
  - 팀 -> 회원 연관관계 1개(단방향)
    - 객체의 양방향 관계는 사실 양방향 관계가 아니라 서로 다른 단방향 관계 2개
    - 객체를 양방향으로 참조하려면 단방향 연관관계를 2개 만들어야 한다.
  - 테이블 연관관계 = 1개
    - 회원 <-> 팀의 연관관계 1개(양방향)
      - 테이블은 외래 키 하나로 두 테이블의 연관관계를 관리

<p align="center">
     <img src="https://user-images.githubusercontent.com/83503188/160979864-214f387c-19ee-4113-94e8-ae5dfb0adbdf.png" width="400px" height="250px"/>
     <img src="https://user-images.githubusercontent.com/83503188/160979774-af0cd64f-184a-4741-a4ee-7c6c69c4f0f7.png" width="600px" />
</p>

- 객체와 테이블의 연관관계 차이로 인해서 **Member또는 Team 둘 중 하나로 외래 키를 관리해야 한다.**
  - **mappedBy를 통해 외래 키를 관리하는 주인(FK를 가진 테이블)을 지정해줌**
  - 주인은 mappedBy 속성을 사용하지 않은 쪽
  - 주인이 아닌 쪽은 읽기만 가능
  - 연관관계의 주인만이 외래 키를 관리(등록, 수정)
  - 외래 키가 있는 곳을 주인으로 지정 -> Member
  - **주인이 아닌 쪽(=Team.members)는 읽기만 가능, 데이터를 변경해도 변화x**
  - 일대다(1:N) 관계의 경우 다(N)쪽이 주인

#### 양방향 매핑시 가장 많이 하는 실수
```
   Team team = new Team();
   team.setName("TeamA");
   em.persist(team);
   Member member = new Member();
   member.setName("member1");
   //역방향(주인이 아닌 방향)만 연관관계 설정
   team.getMembers().add(member);
   em.persist(member);
```

<p align="center">
     <img src="https://user-images.githubusercontent.com/83503188/160980472-b38984c4-983a-4d77-a59e-edc791919c9e.png" width="600px" />
</p>

- 양방향 매핑시 연관관계의 주인에 값을 입력해야 한다 !! 
- 주인이 아닌 쪽(=Team)은 읽기 전용

```
Team team = new Team();
team.setName("TeamA");
em.persist(team);
Member member = new Member();
member.setName("member1");
team.getMembers().add(member);
//연관관계의 주인에 값 설정
member.setTeam(team); //**
em.persist(member);
```

<p align="center">
     <img src="https://user-images.githubusercontent.com/83503188/160980606-10a9f037-fb79-4f36-a080-3fb572d6d627.png" width="600px" />
</p>

- 연관관계 주인에만 값을 넣어서 실수를 유발하지 말고, 객체지향적으로 양쪽 모두 값을 대입하자 -> 편의 메소드 사용
- team.getMember().add(member);를 안 할 경우 생기는 2가지 문제
  1. flush전에 find를 할 경우 team이 1차 캐시에 올라간 상태이므로 member와 맵핑이 되기 전 따라서, team에 현재 member가 속하지 않은 상태로 불러옴
  2. test케이스 작성 시 문제 발생
- 순수 객체 상태를 고려해서 항상 양쪽에 값을 설정하자
- 연관관계 편의 메소드를 생성하자
- 단방향 매핑만으로 연관관계 매핑을 완료하고, 조회 기능이 필요할 경우 양방향 매핑을 추가하자

