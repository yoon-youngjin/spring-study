package dev.yoon.webclient.service;

import dev.yoon.webclient.model.NcpGeolocationDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Service
/**
 * Geolocation API를 사용하기 위한 Service
 * Geolocation API: IP주소를 요청하면 위도 경도를 반환하는 API
 * NCP: Naver Cloud Platform
 */
public class NcpApiService {
    private static final Logger logger = LoggerFactory.getLogger(NcpApiService.class);

    @Value("${ncp.api.access-key:stub-api-key}")
    private String accessKey;

    @Value("${ncp.api.secret-key:stub-secret-key}")
    private String secretKey;

    /**
     * 추가적인 헤더가 필요
     */
    private static final String ncpHeaderNameTimestamp = "x-ncp-apigw-timestamp";
    private static final String ncpHeaderNameSignature = "x-ncp-apigw-signature-v2";

    private final WebClient ncpWebClient;

    public NcpApiService(WebClient ncpWebClient) {
        this.ncpWebClient = ncpWebClient;
    }


    public NcpGeolocationDto geoLocation(String ip){
        String epochString = String.valueOf(System.currentTimeMillis());
        /**
         * 도메인 주소
         */
        String uriBase = "https://geolocation.apigw.ntruss.com";
        String uriPath = String.format("/geolocation/v2/geoLocation" +
                "?ip=%s&ext=t&responseFormatType=json", ip);

        String ncpSignature = this.makeSignature("GET", uriPath, epochString);

        return this.ncpWebClient
                .get() // get, post, delete, ...
                .uri(uriBase + uriPath)
//                .header(ncpHeaderNameTimestamp, epochString)
//                .header(ncpHeaderNameSignature, ncpSignature)
                /**
                 * header함수를 두번 호출하지 않고 묶어서 사용
                 */
                .headers(httpHeaders -> {
                    httpHeaders.add(ncpHeaderNameTimestamp, epochString);
                    httpHeaders.add(ncpHeaderNameSignature, ncpSignature);
                })
                /**
                 * exchangeToMono
                 * retrieve()와 bodyToMono()를 합친 함수
                 * clientResponse: 실제 응답에 대한 정보를 담고 있는 변수
                 */
                .exchangeToMono(clientResponse -> {
                    logger.trace(clientResponse.headers().toString());
                    return clientResponse.bodyToMono(NcpGeolocationDto.class);
                })
                .block();
    }

    /**
     * URL과 Timestamp를 모두 합친 문자열을 암호화하는 함수
     * NCP에 예시 설명되어 있음
     * method: 요청 방식(get,post,...), path: url, epochString: timestamp
     */
    private String makeSignature(String method, String path, String epochString) {
        String space = " ";					// one space
        String newLine = "\n";					// new line

        String message = method +
                space +
                path +
                newLine +
                epochString +
                newLine +
                accessKey;
        try {
            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));

            return Base64.encodeBase64String(rawHmac);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
