# 예제 도메인 모델

![image](https://user-images.githubusercontent.com/83503188/185355323-fef61409-7889-4f5e-aaaa-e7d716bf2724.png)
    
## Member 엔티티

```java
@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"}) 
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String username;

    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username) {
        this(username, 0, null);
    }

    public Member(String username, int age) {
        this(username, age, null);
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team != null) {
            changeTeam(team);
        }
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }


}
```

- `@ToString(of = {"id", "username", "age"})` : team을 포함하면 양방향 연관관계로 인해 순환참조가 발생하기 때문에 `of`를 통해 출력 대상을 지정해준다.

## Team 엔티티

```java
@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name"})
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }
}
```

## 데이터 확인 테스트

```java
@SpringBootTest
@Transactional
@Commit
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Test
    public void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        // 초기화
        em.flush(); 
        em.clear(); 

        List<Member> members = em.createQuery("select m from  Member m", Member.class)
                .getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("-> member.team" + member.getTeam());

        }


    }

}
```

- `@Commit`: Test에 Transactional 있는 경우 기본적으로 모두 Rollback 처리하므로 Rollback을 막기 위해서 `@Commit` 어노테이션을 사용해준다.
- `em.flush()`: 영속성 컨텍스트에 존재하는 것들을 Query로 만들어 DB에 날린다.
- `em.clear()`: 영속성 컨텍스트를 전부 삭제한다.