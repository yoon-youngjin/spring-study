package dev.yoon.orderservice.dto;

import dev.yoon.orderservice.dto.Field;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Schema {
    private String type;
    private List<Field> fields;
    private boolean optional;
    private String name;
}
