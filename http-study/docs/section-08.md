# HTTP 헤더2 - 캐시와 조건부 요청 

## 캐시 기본 동작

### 캐시가 없을 때 

**첫 번째 요청**

![image](https://user-images.githubusercontent.com/83503188/209657998-ecd631f3-458e-4c4d-a39d-41e6166c5048.png)

![image](https://user-images.githubusercontent.com/83503188/209658027-a0b49a94-1a02-4e08-a0fe-28a7e52d5aa9.png)

![image](https://user-images.githubusercontent.com/83503188/209658066-bfccb27c-f3a0-445f-b1f7-6ece492fb318.png)


**두 번째 요청**

![image](https://user-images.githubusercontent.com/83503188/209658133-90741101-d164-4db1-b275-d57acfd24c10.png)

![image](https://user-images.githubusercontent.com/83503188/209658156-4f07d1d5-e8a7-4f04-aed0-9d17c0d89713.png)

- 데이터가 변경되지 않아도 계속 네트워크를 통해서 데이터를 다운로드 받아야 한다.
- 인터넷 네트워크는 매우 느리고 비싸다.
- 브라우저 로딩 속도가 느리다.
- 느린 사용자 경험

### 캐시 적용

**첫 번째 요청**

![image](https://user-images.githubusercontent.com/83503188/209658229-c240c990-53d7-4aa3-a6c0-977c4bf70c80.png)

![image](https://user-images.githubusercontent.com/83503188/209658283-1045abe7-aa5e-4a4e-8269-c77e94ff16d1.png)

![image](https://user-images.githubusercontent.com/83503188/209658312-c6b97e3e-bec1-47d4-8c3f-1d19363d66d1.png)

**두 번째 요청**

![image](https://user-images.githubusercontent.com/83503188/209658369-965f8486-ae7e-4f91-a0e6-eedceb3e11ce.png)

![image](https://user-images.githubusercontent.com/83503188/209658383-3463d1c1-b101-4657-8d1f-9f98eb7e15e2.png)

- 캐시 덕분에 캐시 가능 시간동안 네트워크를 사용하지 않아도 된다.
- 비싼 네트워크 사용량을 줄일 수 있다.
- 브라우저 로딩 속도가 매우 빠르다.
- 빠른 사용자 경험

**세 번째 요청 - 캐시 시간 초과**

![image](https://user-images.githubusercontent.com/83503188/209658426-a711bf61-a0a7-4566-a3d6-e247e11fdb79.png)

![image](https://user-images.githubusercontent.com/83503188/209658547-8f1f5c8e-c3c8-4078-bdd0-bbbf493241d3.png)

![image](https://user-images.githubusercontent.com/83503188/209658576-a2b78a9d-4d1a-4608-9e12-d2e16d2dc4d6.png)

- 캐시 유효 시간이 초과하면, 서버를 통해 데이터를 다시 조회하고, 캐시를 갱신한다.
- 이때 다시 네트워크 다운로드가 발생한다. 

star.jpg 라는 이미지가 전혀 바뀌지 않아도 전체를 다시 다운로드 받는 상황이 발생한다. 

## 검증 헤더와 조건부 요청1

- 검증 헤더: Last-Modified
- 조건부 요청: if-modified-since

### 캐시 시간 초과

캐시 유효 시간이 초과해서 서버에 다시 요청하면 다음 두 가지 상황이 나타난다.

![image](https://user-images.githubusercontent.com/83503188/209659239-7abe938d-f932-4af1-a91d-2ab4425f884e.png)

- 캐시 만료후에도 서버에서 데이터를 변경하지 않음
- 생각해보면 데이터를 전송하는 대신에 저장해 두었던 캐시를 재사용 할 수 있다.
- 단 클라이언트의 데이터와 서버의 데이터가 같다는 사실을 확인할 수 있는 방법 필요

### 검증 헤더 추가

**첫 번째 요청**

![image](https://user-images.githubusercontent.com/83503188/209659378-1b1dc002-7d7b-4a19-b7c6-4ecd431872a1.png)

![image](https://user-images.githubusercontent.com/83503188/209659402-1f06b23c-b013-46a5-ba4e-c36aa06b3ac0.png)
- 브라우저 캐시에 데이터를 저장할 때 유효 시간뿐 아니라 헤더 정보(Last-Modified)를 이용하여 데이터 최종 수정일도 포함시킨다.

**두 번째 요청 - 캐시 시간 초과**

![image](https://user-images.githubusercontent.com/83503188/209659480-f8439242-54f8-427e-b447-9081b8808371.png)

![image](https://user-images.githubusercontent.com/83503188/209659493-f6b8b30b-8a93-4233-9a66-db232167bcfa.png)
- 브라우저 캐시에 존재하는 데이터의 유효 시간이 만료되었기 때문에 서버에 다시 이미지를 요청
- 이때 브라우저 캐시에 최종 수정일 데이터가 존재하므로 조건부 요청 헤더(if-modified-since)를 포함

![image](https://user-images.githubusercontent.com/83503188/209659533-814751db-4132-4e9e-b871-503b6b504d86.png)
- 서버에 존재하는 이미지의 최종 수정일과 브라우저 캐시에 존재하는 데이터의 최종 수정일을 비교한다.

![image](https://user-images.githubusercontent.com/83503188/209659568-ff090ba4-560e-42ae-ab32-cf112a6c2ba9.png)
- 수정이 안됨을 판단하면 HTTP Body 가 없는 304 Not Modified 메시지를 전송

![image](https://user-images.githubusercontent.com/83503188/209659594-d1ee051a-2e69-437e-80c8-2eea7c224ba2.png)

![image](https://user-images.githubusercontent.com/83503188/209659627-cbaa3d8a-7bc4-46f0-b408-bac7ace9ea6d.png)

- 캐시 유효 시간이 초과해도, 서버의 데이터가 갱신되지 않으면
- 304 Not Modified + 헤더 메타 정보만 응답(바디X)
- 클라이언트는 서버가 보낸 응답 헤더 정보로 캐시의 메타 정보를 갱신
- 클라이언트는 캐시에 저장되어 있는 데이터 재활용
- 결과적으로 네트워크 다운로드가 발생하지만 용량이 적은 헤더 정보만 다운로드
- 매우 실용적인 해결책

## 검증 헤더와 조건부 요청2

- 검증 헤더
  - 캐시 데이터와 서버 데이터가 같은지 검증하는 데이터
  - Last-Modified , ETag
- 조건부 요청 헤더
  - 검증 헤더로 조건에 따른 분기
  - If-Modified-Since: Last-Modified 사용
  - If-None-Match: ETag 사용
  - 조건이 만족하면 200 OK
  - 조건이 만족하지 않으면 304 Not Modified

**예시**

- If-Modified-Since: 이후에 데이터가 수정되었으면?
  - 데이터 미변경 예시
    - 캐시: 2020년 11월 10일 10:00:00 vs 서버: 2020년 11월 10일 10:00:00
    - 304 Not Modified, 헤더 데이터만 전송(BODY 미포함)
    - 전송 용량 0.1M (헤더 0.1M, 바디 1.0M)
  - 데이터 변경 예시
    - 캐시: 2020년 11월 10일 10:00:00 vs 서버: 2020년 11월 10일 11:00:00
    - 200 OK, 모든 데이터 전송(BODY 포함)
    - 전송 용량 1.1M (헤더 0.1M, 바디 1.0M)

**Last-Modified, If-Modified-Since 단점**

- 1초 미만(0.x초) 단위로 캐시 조정이 불가능
  - 2020년 11월 10일 10:00:00 -> 최대가 초 단위
- 날짜 기반의 로직 사용
- 데이터를 수정해서 날짜가 다르지만, 같은 데이터를 수정해서 데이터 결과가 똑같은 경우
- 서버에서 별도의 캐시 로직을 관리하고 싶은 경우
  - 예) 스페이스나 주석처럼 크게 영향이 없는 변경에서 캐시를 유지하고 싶은 경우

**ETag, If-None-Match**

- ETag(Entity Tag)
- 캐시용 데이터에 날짜가 아닌 임의의 고유한 버전 이름을 달아둠
  - 예) ETag: "v1.0", ETag: "a2jiodwjekjl3"
- 데이터가 변경되면 이 이름을 바꾸어서 변경함(Hash를 다시 생성)
  - 파일을 해시 알고리즘을 통해 결과를 출력하면 파일의 Content 를 통해 출력하기 때문에, Content 가 변경되지 않으면 해시 결과는 동일하게 나온다.
  - 단순히 서버에서 값을 생성하여 Etag 에 넣을 수도 있음
  - 예) ETag: "aaaaa" -> ETag: "bbbbb"
- 진짜 단순하게 ETag만 보내서 같으면 유지, 다르면 다시 받기!

### 검증 헤더 추가

**첫 번째 요청**

![image](https://user-images.githubusercontent.com/83503188/209661447-d6332126-91e4-4056-b19b-c81ddb64be63.png)

![image](https://user-images.githubusercontent.com/83503188/209661481-8f1aa3b5-ea4a-4c46-9b1f-11a536cfce9d.png)

**두 번째 요청 - 캐시 시간 초과**

![image](https://user-images.githubusercontent.com/83503188/209661524-7d31c705-b66a-4fec-a816-b20e55f413f6.png)

![image](https://user-images.githubusercontent.com/83503188/209661541-6a5782f3-e8d5-4b5a-9c18-afbcdfedb75f.png)

![image](https://user-images.githubusercontent.com/83503188/209661570-1f4cb013-f330-4138-b159-8eeedcd5a50d.png)

![image](https://user-images.githubusercontent.com/83503188/209661578-6f0832b3-d247-4d02-bffe-aae5f48c5db0.png)

![image](https://user-images.githubusercontent.com/83503188/209661608-36980fc2-aa9b-443a-9b69-7ec3330742b7.png)

![image](https://user-images.githubusercontent.com/83503188/209661629-574b6b16-576a-492d-bb0f-ff2279d6fc61.png)

![image](https://user-images.githubusercontent.com/83503188/209661641-9d2f08bb-84d7-408c-a547-609afd6a643a.png)

- 진짜 단순하게 ETag만 서버에 보내서 같으면 유지, 다르면 다시 받기!
- **캐시 제어 로직을 서버에서 완전히 관리**
- 클라이언트는 단순히 이 값을 서버에 제공(클라이언트는 캐시 메커니즘을 모름)
- 예)
  - 서버는 배타 오픈 기간인 3일 동안 파일이 변경되어도 ETag를 동일하게 유지
  - 애플리케이션 배포 주기에 맞추어 ETag 모두 갱신

## 캐시와 조건부 요청 헤더

### 캐시 제어 헤더

- Cache-Control: 캐시 제어
- Pragma: 캐시 제어(하위 호환)
- Expires: 캐시 유효 기간(하위 호환)

#### Cache-Control

**캐시 지시어(directives)**

- Cache-Control: max-age
  - 캐시 유효 시간, 초 단위
- Cache-Control: no-cache
  - 데이터는 캐시해도 되지만, 항상 원(origin) 서버에 검증하고 사용
- Cache-Control: no-store
  - 데이터에 민감한 정보가 있으므로 저장하면 안됨(메모리에서 사용하고 최대한 빨리 삭제)

#### Pragma

**캐시 제어(하위 호환)**
- Pragma: no-cache
- HTTP 1.0 하위 호환

#### Expires

**캐시 만료일 지정(하위 호환)**

`expires: Mon, 01 Jan 1990 00:00:00 GMT`

- 캐시 만료일을 정확한 날짜로 지정
- HTTP 1.0 부터 사용
- 지금은 더 유연한 Cache-Control: max-age 권장
  - `Cache-Control: max-age=60` 초 단위로 지정하는게 더욱 유연
- Cache-Control: max-age와 함께 사용하면 Expires는 무시

### 검증 헤더와 조건부 요청 헤더

- 검증 헤더 (Validator)
  - ETag: "v1.0", ETag: "asid93jkrh2l"
  - Last-Modified: Thu, 04 Jun 2020 07:19:24 GMT
- 조건부 요청 헤더
  - If-Match, If-None-Match: ETag 값 사용
  - If-Modified-Since, If-Unmodified-Since: Last-Modified 값 사용

## 프록시 캐시

**원 서버 직접 접근 - origin 서버**

![image](https://user-images.githubusercontent.com/83503188/209664328-cafcfecc-7db5-4420-a14f-2d91eeafba99.png)
- 미국에 실제 데이터를 가진 서버를 원(origin) 서버라고 한다.
- 클라이언트가 원 서버에 직접 접근하게되면 사용자에게 응답이 너무 느리다.

### 프록시 캐시 도입

**첫 번째 요청**

![image](https://user-images.githubusercontent.com/83503188/209664008-dfd69437-c071-4ab3-a487-4b225b34918b.png)
- 클라이언트에서 원 서버에 직접 접근하는것이 아닌 중간에 프록시 캐시 서버를 두고, 클라이언트의 요청을 처리
- 응답 시간을 개선할 수 있다.
- CDM 서비스

![image](https://user-images.githubusercontent.com/83503188/209664035-acd93604-9d91-4a2c-a58d-db2121a68bfd.png)
- 첫 번째 요청을 보내는 클라이언트는 프록시 캐시 서버에 데이터가 존재하지 않기 때문에 응답이 느리다.
- 혹은 원 서버에서 미리 프록시 캐시 서버에 데이터를 밀어넣는 경우도 존재한다.
- 클라이언트의 웹 브라우저나 로컬을 private 캐시라고 하며, 프록시 캐시 서버를 공용으로 사용하므로 public 캐시라고 한다.


### Cache-Control

**캐시 지시어(directives) - 기타**

- Cache-Control: public
  - 응답이 public 캐시에 저장되어도 됨
- Cache-Control: private
  - 응답이 해당 사용자만을 위한 것임, private 캐시에 저장해야 함(기본값)
- Cache-Control: s-maxage
  - 프록시 캐시에만 적용되는 max-age
- Age: 60 (HTTP 헤더)
  - 오리진 서버에서 응답 후 프록시 캐시 내에 머문 시간(초)

## 캐시 무효화

### Cache-Control

**확실한 캐시 무효화 응답**

Cache 를 적용안해도 웹 브라우저에서 임의로 캐시하는 경우가 존재하기 때문에 무효화가 필요하다.

- Cache-Control: no-cache, no-store, must-revalidate
- Pragma: no-cache
  - HTTP 1.0 하위 호환
  - 과거 HTTP 1.0 요청이 올 수 있기 때문에 넣어준다.

**캐시 지시어(directives) - 확실한 캐시 무효화**

- Cache-Control: no-cache
  - 데이터는 캐시해도 되지만, 항상 원 서버에 검증하고 사용(이름에 주의!)
- Cache-Control: no-store
  - 데이터에 민감한 정보가 있으므로 저장하면 안됨(메모리에서 사용하고 최대한 빨리 삭제)
- Cache-Control: must-revalidate
  - 캐시 만료후 최초 조회시 원 서버에 검증해야함
  - 원 서버 접근 실패시 반드시 오류가 발생해야함 - 504(Gateway Timeout)
  - must-revalidate 는 캐시 유효 시간이라면 캐시를 사용함
- Pragma: no-cache
  - HTTP 1.0 하위 호환

### no-cache vs must-revalidate

no-cache 만 사용해도 항상 원 서버에 검증하는데 must-revalidate 까지 사용해야하는 이유? 

**no-cache 기본 동작**

![image](https://user-images.githubusercontent.com/83503188/209665142-b27b2976-b000-444b-aaa0-0045c4642cf3.png)
- 클라이언트 요청시 no-cache 가 적용되어 있기 때문에 프록시 캐시 서버에서 처리하지 않고 원 서버에 직접 요청한다.

![image](https://user-images.githubusercontent.com/83503188/209665151-53357172-c730-4677-adb5-d4217ba123bf.png)

**no-cache**

![image](https://user-images.githubusercontent.com/83503188/209665183-bfc6182f-8990-4374-ad5d-7d2744121559.png)
- no-cache 이므로 원 서버에 요청을 보내야하는데 네트워크가 단절
- 요청을 전달하는 프록시 캐시 서버에서 설정을 통해 장애보다는 과거 데이터라도 반환할 수 있음

**must-revalidate**

![image](https://user-images.githubusercontent.com/83503188/209665220-41fe2def-7959-42f7-ad0a-8a81d44cbfe3.png)
- must-revalidate 는 네트워크가 단절된 경우 반드시 504 처리

