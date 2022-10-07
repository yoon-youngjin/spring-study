# 암호화 처리를 위한 Encryption과 Decryption

### 대칭키와 비대칭키

- Encryption(암호화): 일반적인 데이터(plane text)를 암호화해서 사람이 알 수 없도록 변경하는 작업
- Decryption(복호화): Encryption 데이터를 원래 데이터로 바꾸는 작업

#### Encryption types

- Symmetric Encryption (Shared) -> 대칭 암호화방식
  - 암호화에 사용한 키와 복호화에 사용한 키를 동일하게 사용하는 방식

- Asymmetric Encryption (RSA Keypair) -> 비대칭 암호화방식
  - 암호화에 사용한 키와 복호화에 사용한 키를 다르게 사용하는 방식
  - 비대칭 암호화 방식에서 사용되는 각각의 키를 private key, public key라고 한다.
  - 암호화시에 private key를 사용하고 복호화시에 public key를 사용한다고 정의되지 않았고 다만 복호화시에 암호화에 사용되지 않은 키를 사용한다.

![image](https://user-images.githubusercontent.com/83503188/194226097-b69f0b9d-a087-4c93-abc5-8d33222859c5.png)

일반적인 평문 데이터를 yml에서 보관하게되는데, 데이터베이스 암호, IP 주소와 같은 데이터는 암호화되어 저장해야 한다.

사용하는 시점에 암호화된 데이터를 복호화하여 사용하는 흐름


### 대칭키를 이용한 암호화 

#### Config Server

라이브러리 추가
- bootstrap

암호화를 위해서는 키값이 필요하다.

**bootstrap.yml**

```yml
encrypt:
  key: asdfasdfsadfsdf
```

**git repository: spring-cloud-config**

**user-service.yml**

```yml
datasource:
  driver-class-name: org.h2.Driver
  url: jdbc:h2:mem:testdb
  username: sa
  password: '{cipher}ea7a2a3c1a09c0e96aa851ce2666ca5b37474260c8a8a4fb5819e232b96d1821'
```

#### Users Microservice

users-service의 application.yml, bootstrap.yml 수정 -> Config의 user-service.yml로 이동

데이터베이스를 연동하는 Datasource 부분을 Config에서 별도의 파일로 분리 

**application.yml**

```yml
spring:
  ...

#  datasource:
#    driver-class-name: org.h2.Driver
#    url: jdbc:h2:mem:testdb
```

**bootstrap.yml**

```yml
spring:
  cloud:
    config:
      uri: http://127.0.0.1:8888
      name: user-service 
```

**encrypt**

![image](https://user-images.githubusercontent.com/83503188/194230602-45120d38-49d2-4548-913b-7a8a1437c53d.png)

**decrypt**

![image](https://user-images.githubusercontent.com/83503188/194230736-ebc3699b-f75a-4b16-a238-778b962d3121.png)

**결과**

![image](https://user-images.githubusercontent.com/83503188/194231828-d45ce91a-594a-4812-bb59-9298a0266f8c.png)

- 웹 브라우저에서 읽혀질 때는 복호화된 데이터
- 각각의 Microservice에서 읽힐 때 복호화된 데이터로 읽힌다.

**암호화된 데이터를 임의로 변경**

**user-service.yml**

```yml
datasource:
  driver-class-name: org.h2.Driver
  url: jdbc:h2:mem:testdb
  username: sa
  password: '{cipher}ea7a2a3c1a09c0e96aa851ce2666ca5b37474260c8a8a4fb5819e232b96d1821_wrong'
  
token:
  expiration_time: 86400000 # 만료 기간 -> 하루
  secret: secret-key # 토큰 키
gateway:
  ip: 127.0.0.1

```

![image](https://user-images.githubusercontent.com/83503188/194232278-eb663e4b-2ba2-43ff-9b8f-3d67c379765b.png)


### 비대칭키를 이용한 암호화

JDK keytool 이용

- mkdir ${user.home}/Desktop/Work/keystore

**키 생성 - private (암호화 또는 복호화 가능)**

```text
keytool -genkeypair -alias apiEncryptionKey -keyalg RSA -dname "CN=Youngjin Yoon, OU=API Development, O=yoon.co.kr, L=Seoul, C=KR" - keypass "yoon1234" -keystore apiEncryptionKey.jks -storepass "yoon1234"
```
- alias를 통해서 호출, 사용
- dname을 통해서 서명정보 추가, 부가정보 입력
- 사용되는 알고리즘 RSA

**공개키 생성** 

위에서 만든 키로 부터 공개키를 생성

```text
keytool -export -alias apiEncryptionKey -keystore apiEncryptionKey.jks -rfc -file trustServer.cer
```

![image](https://user-images.githubusercontent.com/83503188/194248362-60a9c2af-d23f-42fd-ae0d-c4a4f78886f1.png)

**인증서 파일(.cer)을 jks 파일로 변경**

```text
keytool -import -alias trustServer -file trustServer.cer -keystore publicKey.jks
```

#### Config Server

이전의 config 정보를 변경

**bootstrap.yml**

```yml
encrypt:
#  key: asdfasdfsadfsdf
  key-store:
    location: file:///{user.home}/WorkSpace/SSS/msa-with-spring-cloud/keystore/apiEncryptionKey.jks
    password: yoon1234
    alias: apiEncryptionKey
```


![image](https://user-images.githubusercontent.com/83503188/194250174-b71e3885-816e-4fa3-92b6-eae6a9b48b4f.png)

![image](https://user-images.githubusercontent.com/83503188/194250439-73cdea2d-f25b-41ab-9a80-0dfe878b8f43.png)

**user-service.yml**

```yml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:testdb
    username: sa
    password: '{cipher}AQAGAHR558KD3Y6dyjTaKvLwsMa5G4FdTSttuvxS6YN5qc5MYJB/LV41k18K+QgKOkKp6GfnfrqkUqqzPmUoV6M5Ms11JnpRQ5arsmY5oo+WOXNmUT+PAKWZ051atYngI6gNURNTYw2eHWzTqgywzgTDLj1NT8X94q3Ir5KuWeJMLCIzw442zUpPcDvfpl4YSE2n1KZ2vqncslNDfqPxeZGMJDKceIRspMRtnBbrtNA6hvqsX2XFYPQlf7HpBZ4XorMcLe+Ki+GJKcWJYE/4YPkgpBuNlwK9Y4WQQPqbAI7ghfAqmADyJSkFQ+nG+S0ho0lmmgJcTw7EwSkL/vd3Br6k3u0kCBa9Mylrp9tyRBxwDQMvR3bRDHAKDiils0pu8wQ='
  
token:
  expiration_time: 86400000 # 만료 기간 -> 하루
  secret: secret-key # 토큰 키
gateway:
  ip: 127.0.0.1
```

![image](https://user-images.githubusercontent.com/83503188/194251050-08082c86-6707-4a44-98f6-71f4427ee7c8.png)


### Gateway의 token secret 정보 암호화

**apigateway-service : ecommerce.yml**

```yml
token:
  expiration_time: 86400000 # 만료 기간 -> 하루
  secret: '{cipher}AQBr7zVkg9ilfUqJYsHQag/30IeJ0V7rfEnYFdviZP4OD2giykMG4o8cXH0JyFuCEqJCnp4V3Nm1/KH4GwAKddRH010ggTo3ltvBtcTRCs1hKaWlcn0NFB28/Ri7n83QRZou2V2FUsOoM52meynjZ6I9i6grDfzMVQXm8TtXe47xqmeLp0vT1vxZYVfnHWOeFn+0Iix+W70dtsSm+1A9FvQsaqVqkfoB0ECue0JriG9LbLXjYvFg/eo08clL5SAIqY46uLATBc2teN30v1nv5nZ+u8DAgVFkiIhN2nalaNprji9nsP4JHsHaJ+RpgsFY7BZThkXKxlRQNfkzf8TbONiIBvCx90YgQWn/LL5lDQ6x5qCAeiBPlyo5j/09bLHAGtE=' # 토큰 키
gateway:
  ip: 127.0.0.1
```

yml을 하나로 모아서 처리할 수도 있다.

![image](https://user-images.githubusercontent.com/83503188/194256591-914f8a64-bc6c-4d45-b0c8-bcd35c336b4b.png)

![image](https://user-images.githubusercontent.com/83503188/194256682-890f6c06-657d-4611-9125-994e7f3afa82.png)

`user-service.yml` 와 `ecommerce.yml` 모두 `application.yml`이라는 상위 설정을 가지고 있으므로 토큰 정보와 같은 공통되는 설정은 `application.yml`에 기입한다.






