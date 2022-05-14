package dev.yoon.shop.domain.cart.entity;

import dev.yoon.shop.domain.base.BaseTimeEntity;
import dev.yoon.shop.domain.member.entity.Member;
import lombok.Getter;

import javax.persistence.*;

@Table(name = "cart")
@Getter
@Entity
public class Cart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

}
