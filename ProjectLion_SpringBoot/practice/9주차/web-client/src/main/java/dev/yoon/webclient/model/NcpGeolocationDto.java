package dev.yoon.webclient.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
/**
 * 전송이 되는 응답 dto
 */
public class NcpGeolocationDto {

    private Long returnCode;
    private String requestId;
    private GeolocationData geoLocation;

}
