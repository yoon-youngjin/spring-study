## 페이징 API 만들기
-	페이징 처리는 거의 모든 웹 개발에서 사용
-	그렇게 복잡하고 어려운 구현은 아니나 실제 쿼리로 작성할 때는 상당히 번거로운 작업
-	Spring Data JPA에서는 이러한 문제를 아주 쉽게 해결 가능

### 기초 작업
![image](https://user-images.githubusercontent.com/83503188/159871703-1cf9d230-31ec-4a59-ba5b-53a25679941d.png)
-	`CrudRepository`를 상속하는 것보다 하위 클래스인 `JpaRepository`를 상속 받아 `Repository`를 구현하는 것이 좋습니다
-	페이징 처리하는 메서드: 매개변수로 `Pageable`을 받아 Page<T>으로 리턴

```

@RestController
@RequestMapping("accounts")
public class AccountController {
    @GetMapping
    public Page<AccountDto.Res> getAccounts(final Pageable pageable) {
        return accountService.findAll(pageable).map(AccountDto.Res::new);
    }
}

@Service
@Transactional
@AllArgsConstructor
public class AccountService {
    ...
    @Transactional(readOnly = true)
    public Page<Account> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }
}
```
-	컨트롤러에서 `Pageable` 인터페이스를 받고 `repository` 메서드 `findAll(pageable)`로 넘김

### 요청
```
curl -X GET \
  http://localhost:8080/accounts
```

### 응답
```
{
  "content": [
    {
      "email": {
        "value": "test001@test.com"
      },
      "password": {
        "value": "$2a$10$tI3Y.nhgC.73LYCszoCaLu3nNEIM4QgeACiNseWlvr1zjrV5NCCs6",
        "expirationDate": "+20120-01-20T00:00:00",
        "failedCount": 0,
        "ttl": 1209604,
        "expiration": false
      },
      "fistName": "first",
      "lastName": "last",
      "address": {
        "address1": "address1",
        "address2": "address2",
        "zip": "002"
      }
    }
    ...
  ],
  "pageable": {
    "sort": {
      "sorted": false,
      "unsorted": true,
      "empty": true
    },
    "offset": 0,
    "pageSize": 20,
    "pageNumber": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true, // 마지막 페이지 여부
  "totalPages": 1, // 전체 페이지가 1개
  "totalElements": 13, // 모든 요소는 13 개
  "size": 20, // 한 페이지에서 보여줄 사이즈의 갯수, size를 제한하지 않으면 기본적으로 20으로 초기화 된다.
  "number": 0,
  "sort": {
    "sorted": false,
    "unsorted": true,
    "empty": true
  },
  "numberOfElements": 13,
  "first": true, // 첫 패이지 여부
  "empty": false // 리스트가 비어 있는지 여부
}
```

### 다양한 요청
![image](https://user-images.githubusercontent.com/83503188/159873270-c1fb78fc-80c2-48cf-a964-ac53e70a27e2.png)

```
{
    "content": [
        {
            "email": {
                "value": "test013@test.com"
            },
            "password": {
                "value": "$2a$10$tI3Y.nhgC.73LYCszoCaLu3nNEIM4QgeACiNseWlvr1zjrV5NCCs6",
                "expirationDate": "+20120-01-20T00:12:00",
                "failedCount": 0,
                "ttl": 1209604,
                "expiration": false
            },
            "fistName": "first",
            "lastName": "last",
            "address": {
                "address1": "address1",
                "address2": "address2",
                "zip": "002"
            }
        },
        {
            "email": {
                "value": "test012@test.com"
            },
            "password": {
                "value": "$2a$10$tI3Y.nhgC.73LYCszoCaLu3nNEIM4QgeACiNseWlvr1zjrV5NCCs6",
                "expirationDate": "+20120-01-20T00:11:00",
                "failedCount": 0,
                "ttl": 1209604,
                "expiration": false
            },
            "fistName": "first",
            "lastName": "last",
            "address": {
                "address1": "address1",
                "address2": "address2",
                "zip": "002"
            }
        }
    ],
    "pageable": {
        "sort": {
            "unsorted": false,
            "sorted": true,
            "empty": false
        },
        "offset": 0,
        "pageSize": 2,
        "pageNumber": 0,
        "paged": true,
        "unpaged": false
    },
    "last": false,
    "totalPages": 7,
    "totalElements": 13,
    "size": 2,
    "number": 0,
    "sort": {
        "unsorted": false,
        "sorted": true,
        "empty": false
    },
    "numberOfElements": 2,
    "first": true,
    "empty": false
}
```
-	`Pageable`은 다양한 요청 이용해서 기본적인 정렬 기능을 제공
-	`page`는 실제 페이지
-	`size`는 `content`의 size
-	`sort`는 페이징을 처리 시 정렬 -> `id,DESC`는 id 기준으로 내림차순 정렬

### 개선

위의 `Pageable`의 개선할 점이 있습니다. 우선 `size`에 대한 limit이 없습니다. 위의 API에서 `size`값을 20000을 넘기면 실제 데이터베이스 쿼리문이 200000의 조회할 수 있습니다. 그 밖에 page가 0부터 시작하는 것들도 개선하는 것이 필요해 보입니다.


#### PageRequest
```
public final class PageRequest {

    private int page;
    private int size;
    private Sort.Direction direction;

    public void setPage(int page) {
        this.page = page <= 0 ? 1 : page;
    }

    public void setSize(int size) {
        int DEFAULT_SIZE = 10;
        int MAX_SIZE = 50;
        this.size = size > MAX_SIZE ? DEFAULT_SIZE : size;
    }

    public void setDirection(Sort.Direction direction) {
        this.direction = direction;
    }
    // getter

    public org.springframework.data.domain.PageRequest of() {
        return org.springframework.data.domain.PageRequest.of(page -1, size, direction, "createdAt");
    }
```
`Pageable`을 대체하는 `PageRequest` 클래스를 작성합니다.

* `setPage(int page)` 메서드를 통해서 0보다 작은 페이지를 요청했을 경우 1 페이지로 설정합니다.
* `setSize(int size)` 메서드를 통해서 요청 사이즈 50 보다 크면 기본 사이즈인 10으로 바인딩 합니다.
* `of()` 메서드를 통해서 `PageRequest` 객체를 응답해줍니다. 페이지는 0부터 시작하니 `page -1` 합니다. 본 예제에서는 sort는 `createdAt` 기준으로 진행합니다.

### 컨트롤러
```java
@RestController
@RequestMapping("accounts")
public class AccountController {
    ...

    @GetMapping
    public Page<AccountDto.Res> getAccounts(final PageRequest pageable) {
        return accountService.findAll(pageable.of()).map(AccountDto.Res::new);
    }
}
```
컨트롤러 영역은 간단합니다. `Pageable` -> `PageRequest` 교체하면 됩니다.
