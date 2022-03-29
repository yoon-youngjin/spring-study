package hellojpa;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USER")
public class Member extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "name",updatable = false)
    // updatable = false : username의 update를 차단
    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Team team;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
//    @OneToMany(mappedBy = "member")
//    private List<MemberProduct> memberProducts = new ArrayList<>();

//    @OneToOne
//    @JoinColumn(name = "LOCKER_ID")
//    private Locker locker;



//    @ManyToOne(fetch = FetchType.LAZY)
//    // JoinColumn을 통해 FK매핑
//    @JoinColumn(name = "TEAM_ID")
//    private Team team;
//
//
//    @Enumerated(EnumType.STRING)
//    private RoleType roleType;
//
//    public Team getTeam() {
//        return team;
//    }
//
//    // 연관관계 편의 메소드
//    public void setTeam(Team team) {
//        this.team = team;
//        team.getMembers().add(this);
//    }

//    // @Temporal을 사용하지 않고 LocalDate나 LocalDateTime으로 사용
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date createdDate;
//
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date lastModifiedDate;

//    private LocalDate testLocalDate;

//    private LocalDateTime createdDate;
//
//    private LocalDateTime lastModifiedDate;

//    @Lob
//    private String description;

    public Member() {
    }

    public Member(Long id, String name) {
        this.id = id;
        this.username = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.username = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return username;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + username + '\'' +
                '}';
    }

}
