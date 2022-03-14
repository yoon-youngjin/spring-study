package dev.yoon.refactoring_board.dto;

import dev.yoon.refactoring_board.domain.Address;
import dev.yoon.refactoring_board.domain.Location;
import lombok.*;

import javax.persistence.Embedded;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AreaDto {

    @Embedded
    private Address address;

    @Embedded
    private Location location;
}
