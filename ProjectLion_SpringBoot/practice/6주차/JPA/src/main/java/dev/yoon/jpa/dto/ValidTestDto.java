package dev.yoon.jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@ToString
public class ValidTestDto {

    @NotNull // 변수가 null인지 아닌지를 구분하는 어노테이션
    private String notNullString;

    @NotEmpty // empty한 string : "", null이 아니면서, Object.size > 0 또는 Object.length인지를 구분하는 어노테이션
    private String notEmptyString;

    @NotBlank // 열거형 컬렉션(List,...)에 사용x, 공백으로만 구성된 문자열("    ")을 제외
    private String notBlackString;

    @NotEmpty // null이 아니면서, Object.size > 0
    private List<String> notEmptyStringList;


}
