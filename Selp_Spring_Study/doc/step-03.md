## 효과적인 validate, 예외처리 (1)
- 반복적인 작업을 보다 효율적으로 처리하고 정확한 예외 메시지를 front-end에게 전달해주는 것이 목표

1.	@Valid를 통한 유효성검사
2.	@ControllerAdvice를 이용한 Exception 핸들링
3.	ErrorCode 에러 메시지 통합

### @Vaild를 통한 유효성검사
- `@Valid` 어노테이션을 통해서 유효성 검사를 진행하고 유효성 검사를 실패하면 `MethodArgumentValidException` 예외 발생

### @ControllerAdvice를 이용한 Exception 핸들링
```
@ControllerAdvice
public class ErrorExceptionController {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
	    retrun errorResponse...
	}
}
```
- `@ControllerAdvice` 어노테이션을 추가하면 특정 Exception을 핸들링하여 적절한 값을 Response 값으로 리턴해줍니다. 위처럼 `MethodArgumentNotValidException` 핸들링을 하지 않으면 스프링 자체의 에러 Response 값을 리턴
### MethodArgumentNotValidException의 Response처리
```
@ExceptionHandler(MethodArgumentNotValidException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
protected ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
	log.error(e.getMessage());
	final BindingResult bindingResult = e.getBindingResult();
	final List<FieldError> errors = bindingResult.getFieldErrors();

		return buildFieldErrors(
			ErrorCode.INPUT_VALUE_INVALID,
			errors.parallelStream()
				.map(error -> ErrorResponse.FieldError.builder()
					.reason(error.getDefaultMessage())
					.field(error.getField())
					.value((String) error.getRejectedValue())
					.build())
				.collect(Collectors.toList())
	);
}
```


## 효과적인 validate, 예외 처리 (2)

### 이전 예외처리의 단점
1. 모든 Request Dto에 대한 반복적인 유효성 검사의 어노테이션이 필요

      A.	회원 가입, 회원 정보 수정 등등 지속적으로 DTO 클래스가 추가되고 그때마다 반복적으로 어노테이션이 추가

2. 유효성 검사 로직이 변경되면 모든 곳에 변경이 따른다.

      A.	만약 비밀번호 유효성 검사가 특수문자가 추가된다고 하면 비밀번호 변경에 따른 유효성 검사를 정규 표현식의 변경을 모든 DTO마다 해줘야 한다.


### @Embeddable / @Embedded
```
public class Account {
    @Embedded
    private com.cheese.springjpa.Account.model.Email email;
}

@Embeddable
public class Email {

    @org.hibernate.validator.constraints.Email
    @Column(name = "email", nullable = false, unique = true)
    private String address;
}
```

### Dto 변경
```
public static class SignUpReq {

    // @Email 기존코드
    // private String email;
    @Valid // @Valid 반드시 필요
    private com.cheese.springjpa.Account.model.Email email;

    private String zip;
    @Builder
    public SignUpReq(com.cheese.springjpa.Account.model.Email email, String fistName, String lastName, String password, String address1, String address2, String zip) {
        this.email = email;
        ...
        this.zip = zip;
    }

    public Account toEntity() {
        return Account.builder()
                .email(this.email)
                ...
                .zip(this.zip)
                .build();
    }
}
```
- 모든 Request Dto에 대한 반복적인 유효성 검사의 어노테이션이 필요했었지만 새로운 Email 클래스를 바라보게 변경하면 해당 클래스의 이메일 유효성 검사를 바라보게 됩니다. 
- 그 결과 이메일에 대한 유효성 검사는 Embeddable 타입의 Email 클래스가 관리하게 됩니다. 
- 물론 이메일 유효성 검사는 로직이 거의 변경할 일이 없지만 다른 입력값들은 변경할 일들이 자주 생깁니다.
- 이럴 때 모든 DTO에 가서 유효성 로직을 변경하는 것은 불편한 것을 넘어서 불안한 구조를 갖게 됩니다.
- 관리 포인트를 줄이는 것은 제가 생각했을 때는 되게 중요하다고 생각합니다.





