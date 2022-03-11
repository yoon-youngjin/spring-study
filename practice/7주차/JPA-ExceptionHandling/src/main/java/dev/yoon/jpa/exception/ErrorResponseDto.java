package dev.yoon.jpa.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Getter @Setter
public class ErrorResponseDto {

    private String message;

}
