package dev.yoon.webclient.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString
/**
 * 실제 데이터
 */
public class GeolocationData {
    private String kr;
    private Long code;
    private String r1;
    private String r2;
    private String r3;
    private Double lat;
    /**
     * @JsonProperty
     * long은 자바의 키워드이므로 에러를 방지하기 위한 어노테이션
     */
    @JsonProperty("long")
    private Double longitude;
    private String net;

}
