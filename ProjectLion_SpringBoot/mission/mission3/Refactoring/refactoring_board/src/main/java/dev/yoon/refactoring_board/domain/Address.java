package dev.yoon.refactoring_board.domain;

import lombok.Getter;
import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {

    private String province;

    private String city;

    private String street;

}
