# Misson 5

# Spring Boot 연습

## Basic Mission

---

사용자 위치 정보 판단

JavaScript를 사용하면 사용자의 위치 정보를 쉽게 받아올 수 있습니다. 사용자의 위치 정보를 받아서, 사용자의 현재 소재지를 파악하는 기능을 구현해 봅시다. Mission 4 Basic 프로젝트에서 시작합니다.

1. HTML Geolocation API를 사용해 봅시다.
    1. 아래의 `getLocation()` 함수를 이용하면 사용자의 현 위치를 alert 창으로 표시합니다.

    ```jsx
    function getLocation() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(showPosition);
        } else {
            alert("Geolocation is not supported by this browser.");
        }
    }
    
    function showPosition(position) {
        alert(`Latitude: ${position.coords.latitude}, Longitude: ${position.coords.longitude}`);
    }
    ```

2. `AreaController` 에 `latitude`와 `longitude`를 인자로 받는 `RequestMapping` 을 하나 작성합니다.
3. `AreaService` 에 `latitude`와 `longitude` 를 인자로 받아서 현재 등록된 `AreaEntity` 를 기준으로 가장 가까운 `AreaEntity` 를 반환하도록 합시다.

### 세부 사항

1. `AreaEntity` 의 경우, 더미 데이터를 우선 활용합니다.
    1. 서울시 서초구 서초동, 37.4877° N, 127.0174° E
    2. 서울시 강남구 역삼동, 37.4999° N, 127.0374° E
    3. 서울시 강남구 삼성동, 37.5140° N, 127.0565° E
2. `index.html` 에 버튼을 추가하여 `getLocation()` 함수를 사용할 수 있도록 합니다.
3. Javascript Fetch API를 이용하면 쉽게 HTTP요청을 보낼 수 있습니다.

    ```jsx
    await fetch(`/get-location-info?latitude=${latitude}&longitude=${longitude}`)
    ```


## Challenge Mission

---

SSO를 활용한 로그인 기능을 다양한 middleware를 활용하여 다듬어 봅시다. Mission 4 Challenge 에서 시작합니다.

1. `auth-sso` 에서 Redis를 이용해 로그인 정보를 저장합시다.
    1. 로그인에 성공하였을때, 사용자의 정보를 Redis에 적재합니다.
    2. `SsoCookieHandler` 의 `onAuthenticationSuccess` 함수를 활용합니다.
    3. `onAuthenticationSuccess` 에서 저장하는 Cookie의 값을, 임의의 UUID로 전환하여 저장합시다.
2. `SsoLoginController` 에 현재 로그인한 사용자의 정보를 반환하는 `RequestMapping` 을 추가합시다.
    1. SSO를 활용하여 로그인을 하는 서비스 (community 같은)가 현 사용자의 정보를 확인하기 위한 `RequestMapping` 입니다.
    2. 사용자의 `Cookie`에 저장된 `likelion_login_cookie` 의 값을 인자로 받으면, Redis에 저장해둔 사용자 정보를 반환합니다.
    3. community의 `SsoAuthFilter` 에서, `WebClient` 를 활용하여 auth-sso로 요청을 보내어 확인하도록 요청합니다.
3. Publish - Subscribe 패턴을 활용하여 SSO 서버에서 로그아웃이 일어났을때, community에서 알 수 있도록 합시다.
    1. auth-sso는 publisher로서, community는 subscriber로서 작동하게 됩니다.
    2. auth-sso에서 로그아웃을 하면, fanout exchange로 메시지를 전달합니다.
    3. community에서 메시지를 받으면, 해당 내용을 저장해두고 다음 사용자의 요청시 쿠키의 정보를 무효화 합니다.
    4. auth-sso에서 전달하는 메시지는 로그아웃한 사용자의 `likelion_login_cookie` 입니다.

### 세부 사항

1. 이전 미션에서 만들었던 SSO를 마무리 합니다. 실제 로그인한 사용자 정보를 확인할 수 있습니다.
2. Redis에 적재하는 내용은 사용자를 쉽게 식별할 수 있는 정보만 담아두어도, JPA를 통해 사용자 정보를 가져올 수 있습니다.
3. Publish - Subscribe 패턴을 이용할때, 메시지를 처리하기 위한 추가 Service를 구현할 수 있도록 합시다.