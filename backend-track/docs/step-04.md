# 4주차 과제 피드백

## String 비교는 null이 항상 아닌 쪽을 앞에 두고 비교하자

앞에서 부터 검증을 시작하기 때문에 null일 가능성이 존재하는 경우 NPE 발생

- 기존 코드
```java
@PostMapping
    public ResponseEntity<TokenDto> socialLogin(
            @RequestBody SocialLoginRequestDto requestDto,
            HttpServletRequest request) {


        if (!requestDto.getMemberType().equals(MemberType.KAKAO.name())) {
            throw new BusinessException(ErrorCode.INVALID_MEMBER_TYPE);
        }
…
}

```
- 변경된 코드 

```java
public class LoginController {

    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<TokenDto> socialLogin(
            @RequestBody SocialLoginRequestDto requestDto,
            HttpServletRequest request) {
        
        if(!MemberType.KAKAO.name().equals(requestDto.getMemberType())) {
            throw new BusinessException(ErrorCode.INVALID_MEMBER_TYPE);
        }

```

- StringUtils 사용한 코드

```java
import com.shop.projectlion.domain.member.constant.MemberType;
import org.thymeleaf.util.StringUtils;

public class LoginController {

    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<TokenDto> socialLogin(
            @RequestBody SocialLoginRequestDto requestDto,
            HttpServletRequest request) {

        if (!StringUtils.equals(MemberType.KAKAO.name(), requestDto.getMemberType())) {
            throw new BusinessException(ErrorCode.INVALID_MEMBER_TYPE);
        }
…
    }
}

```

## 소셜 로그인 시 헤더에 Bearer를 체크하자!

현재 access token을 클라이언트에서 전달 받을 때 `Bearer {access token}`과 같은 형태로 헤더에 담아서 요청을 받고 있다. 

예를 들어, 오타에 의해서 `Kearer {access token}`과 같은 형태에 반응하지 않도록 예외처리하자!

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {

    private final LoginService loginService;
    private final TokenValidator tokenValidator;

    @PostMapping("/oauth/login")
    public ResponseEntity<OauthLoginDto.Response> socialLogin(
            @RequestBody OauthLoginDto.Request requestDto,
            HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");

        /**
         * header 값을 검증
         */
        tokenValidator.validateAuthorization(authorization);
        ...
    }
}
@Service
public class TokenValidator {

    public void validateAuthorization(String authorizationHeader) {

        ...

        // 2. authorization Bearer 체크
        String[] authorizations = authorizationHeader.split(" ");
        if(authorizations.length < 2 || (!GrantType.BEARRER.getType().equals(authorizations[0]))) {
            throw new AuthenticationException(ErrorCode.NOT_VALID_BEARER_GRANT_TYPE);
        }
    }

    ...
}
    
```

--- 

## ResponseEntity 객체에는 header or status를 지정하여 반환할 수 있다.

```java
```java
@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<HealthCheckResponseDto> healthCheck() {

        HealthCheckResponseDto checkDto = HealthCheckResponseDto.builder()
                .status(true)
                .health("ok")
                .build();

        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("token", "yoon-token");

        return new ResponseEntity<>(checkDto, header, HttpStatus.OK);
    }
}
```

![image](https://user-images.githubusercontent.com/83503188/170090352-7a9af469-6931-4c05-9d31-7bb2963fb141.png)

## 카카오 토큰 발급 과정

![image](https://user-images.githubusercontent.com/83503188/170090422-685f9876-91c1-4333-bd7e-d3973afe6b87.png)

보통 프론트 개발자가 카카오에서 토큰을 받아오고 해당 토큰을 서버에 전달하면 해당 토큰을 이용해 카카오 서버로 부터 회원 정보를 가져와서 우리 서버에 로그인을 하거나 회원가입을 하고 클라이언트에게 jwt를 반환하는 프로세스이다.

위의 그림의 경우 서버에서 카카오 인가 코드, 토큰을 받아오는 과정을 모두 진행하고 있으므로 Client(Backend)라고 명시, 실제 프로세스 그림은 아래 그림과 같다.

![image](https://user-images.githubusercontent.com/83503188/170090703-10f0c77f-3896-4191-9c1c-06d40838fd95.png)

## 카카오 앱 등록 과정

![image](https://user-images.githubusercontent.com/83503188/170090826-0c161ab6-e8f9-4e74-9188-1c5ea1209c60.png)
![image](https://user-images.githubusercontent.com/83503188/170090832-62af2b05-105b-49d2-b3d9-d7205fce285b.png)
![image](https://user-images.githubusercontent.com/83503188/170090836-9cd01e3b-19f8-46bd-a1df-0fcd56ad5913.png)

![image](https://user-images.githubusercontent.com/83503188/170090853-271d948c-4782-4228-a808-fc3636e1ea95.png)
- 인가 코드를 받기 위한 Redirect URI 등록

![image](https://user-images.githubusercontent.com/83503188/170090858-ac1396b9-fbf9-4b44-ac64-980afb92053b.png)
![image](https://user-images.githubusercontent.com/83503188/170090865-9ce73aad-c9b7-47bb-86f3-0fdd909b4261.png)
![image](https://user-images.githubusercontent.com/83503188/170090874-d8bd0552-6c31-49df-bf7c-28d691a78a0f.png)

![image](https://user-images.githubusercontent.com/83503188/170090883-736e1f1f-8201-4de9-865e-a57dc9c23eb4.png)
- secret key 생성

![image](https://user-images.githubusercontent.com/83503188/170090890-fa3653af-f503-43b5-992a-dd45c5f98b8c.png)
- 해당 값들은 따로 외부에 노출되면 안 되기 때문에 github에 따로 올리지 않고 외부에서 주입을 해주거나 암호화

![image](https://user-images.githubusercontent.com/83503188/170090906-f8e93697-dcfe-4943-83e1-168564fd1e2e.png)

![image](https://user-images.githubusercontent.com/83503188/170091467-101f5327-1033-4216-aa16-af5b947d624a.png)

## 변경 전후 FeignClient 

### 변경 전 FeignClient

```java
@FeignClient(name = "kauth.kakao.com", url = "https://kauth.kakao.com")
public interface TokenFeignClient {

    @PostMapping(value = "/oauth/token", headers = "Content-Type=application/x-www-form-urlencoded")
    ResponseEntity<KakaoTokenResponseDto> getKakaoToken(@SpringQueryMap MultiValueMap<String, String> param);

}

public KakaoTokenResponseDto getKakaoTokenInfo(String code) {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        ResponseEntity<KakaoTokenResponseDto> response = feignClient.getKakaoToken(params);


        if (response.getStatusCode() != HttpStatus.OK) {
            throw new TokenNotFoundException(ErrorCode.TOKEN_NOT_FOUND);
        }
        return response.getBody();
    }

```
### 변경 후 FeignClient

```java
@FeignClient(name = "kakaoTokenClient", url = "https://kauth.kakao.com")
public interface KakaoTokenClient {
    @PostMapping(value = "/oauth/token", consumes = "application/json")
    ResponseEntity<KakaoTokenResponseDto> requestKakaoToken(
            @RequestHeader("Content-Type") String contentType,
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("code") String code,
            @RequestParam("client_secret") String clientSecret
    );

}

@RequiredArgsConstructor
@Service
public class KakaoTokenService {

    @Value("${kakao.client.secret}")
    private String clientSecret;

    @Value("${kakao.client.id}")
    private String clientId;

    private final KakaoTokenClient kakaoTokenClient;

    private final String CONTENT_TYPE = "application/x-www-form-urlencoded;charset=utf-8";
    private final String GRANT_TYPE = "authorization_code";
    private final String REDIRECT_URI = "http://localhost:8080/auth/kakao/callback";

    public KakaoTokenResponseDto getKakaoTokenInfo(String code) {

        
        ResponseEntity<KakaoTokenResponseDto> response = kakaoTokenClient.requestKakaoToken(CONTENT_TYPE,
                GRANT_TYPE, clientId, REDIRECT_URI, code, clientSecret);


        if (response.getStatusCode() != HttpStatus.OK) {
            throw new TokenNotFoundException(ErrorCode.TOKEN_NOT_FOUND);
        }
        return response.getBody();
    }
}

```

- 변경 전 코드에 비해 훨씬 깔끔해진 것을 확인할 수 있다.

## **소셜 로그인 다형성 활용**

구글 로그인, 애플 로그인, 카카오 로그인, 등등 access token을 받아서 각 소셜 서버에 회원 정보를 가져오는 공통 로직이 존재하므로 공통 로직을 interface로 묶어서 다형성을 활용하자.


![image](https://user-images.githubusercontent.com/83503188/170092485-d98dc57b-fc0c-4af2-b612-6f83c7393fe5.png)


- OAuthAttributes를 반환, 구글, 카카오, 네이버, ... 모두 각 소셜에서 반환해주는 응답 값은 전부 다르다. 따라서 하나의 DTO에 필수적으로 존재해야하는 필드를 두고 처리한다.
- LoginService에서는 어떤 소셜 로그인 구현체(`NaverLoginApiServiceImpl`, `KakaoLoginApiServiceImpl`, ...)을 이용할지 결정한다.
- SocialLoginApiServiceFactory는 적절한 소셜 로그인 구현체를 결정하는 주체가 된다. -> ex. 응답으로 들어온 MemberType이 KAKAO인 경우에는 SocialLoginApiServiceFactory에서 KakoLoginApiServiceImpl 객체를 반환
- 클래스 자체에 의존하는게 아닌 interface에 의존한다 -> 특정 구현체 클래스에 의존하는 것이 아닌 Factory 클래스와 interface에 의존한다. 
  - 따라서 LoginService는 구현체가 어떻게 구현되어있는지 신경 쓰지 않아도 된다.
    
      ```java
      @RequiredArgsConstructor
      @Service
      @Transactional(readOnly = true)
      public class LoginService {
    
          private final TokenManager tokenManager;
          private final MemberService memberService;
    
          ...
    
          private OAuthAttributes getSocialUserInfo(String accessToken, MemberType memberType) {
              /**
               * memberType에 해당하는 SocialLoginApiService를 반환
               */
              SocialLoginApiService socialLoginApiSerivce = SocialLoginApiServiceFactory.getSocialLoginApiService(memberType);
              OAuthAttributes oAuthAttributes = socialLoginApiSerivce.getUserInfo(accessToken);
              return oAuthAttributes;
          }
      }
    
      ```

## 로그아웃

Authorization 헤더에 access token을 보내면 해당 계정의 refresh token을 만료 처리함으로써 로그아웃을 구현한다.

```java
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class LogoutService {

    private final MemberService memberService;
    private final TokenManager tokenManager;

    @Transactional
    public void logout(String accessToken) {

        // 1. access token 만료 확인
        Claims tokenClaims = tokenManager.getTokenClaims(accessToken);
        Date accessTokenExpiration = tokenClaims.getExpiration();
        if(tokenManager.isTokenExpired(accessTokenExpiration)) {
            throw new AuthenticationException(ErrorCode.ACCESS_TOKEN_EXPIRED);
        }

        // 2. 토큰 타입 검증
        String tokenType = tokenClaims.getSubject();
        if(!TokenType.isAccessToken(tokenType)) {
            throw new AuthenticationException(ErrorCode.NOT_ACCESS_TOKEN_TYPE);
        }

        // 3. 로그인할 때 발행한 refresh token 만료 처리
        String email = tokenManager.getMemberEmail(accessToken);
        Member member = memberService.getMemberByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_EXISTS));

        member.expireRefreshToken(LocalDateTime.now());

    }
}
```

---

