package jpabook.jpashop.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    private String name;

    private String city;

    private String street;

    private String zopcode;

    @OneToMany(
            targetEntity = Order.class,
            mappedBy = "member",
            fetch = FetchType.LAZY
    )
    private List<Order> orders = new ArrayList<>();

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZopcode() {
        return zopcode;
    }

    public void setZopcode(String zopcode) {
        this.zopcode = zopcode;
    }
}
