package hellojpa;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "USER")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "name", updatable = false)
    // updatable = false : username의 update를 차단
    private String username;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "MEMBER_ID")
    private List<AddressEntity> addressHistory = new ArrayList<>();

    public List<AddressEntity> getAddressHistory() {
        return addressHistory;
    }

    public void setAddressHistory(List<AddressEntity> addressHistory) {
        this.addressHistory = addressHistory;
    }

    @Embedded
    private Address homeAddress;

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }


//    @ElementCollection
//    @CollectionTable(
//            name = "FAVORITE_FOOD",
//            joinColumns = @JoinColumn(name = "MEMBER_ID")
//    )
//    @Column(name = "FOOD_NAME") // 예외적으로 필드가 하나이므로 적용 가능
//    private Set<String> favoriteFoods = new HashSet<>();
//
//    @ElementCollection
//    @CollectionTable(
//            name = "ADDRESS",
//            joinColumns = @JoinColumn(name = "MEMBER_ID")
//    )
//    private List<Address> addressHistory = new ArrayList<>();


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Address getHomeAddress() {
        return homeAddress;
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
