package dev.yoon.refactoring_board.domain;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@ToString
public class Address {

    private String province;

    private String city;

    private String street;

}
