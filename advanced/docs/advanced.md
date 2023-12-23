## ThreadLocal 주의사항

쓰레드 로컬의 값을 사용 후 제거하지 않고 그냥 두면 WAS(톰캣)처럼 쓰레드 풀을 사용하는 경우 심각한 문제가 발생할 수 있다.

**사용자A 저장 요청**

<img width="662" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/779912da-11cb-47af-9b1f-56ba964486af">

1. 사용자A가 저장 HTTP 요청
2. WAS는 쓰레드 풀에서 쓰레드 하나를 조회
3. 쓰레드(thread-A)가 할당되어 사용자A의 데이터를 쓰레드 로컬에 저장
4. 쓰레드 로컬의 thread-A 전용 보관소에 사용자A 데이터를 보관 (remove X)

**사용자A 저장 요청 종료**

<img width="638" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/625b6583-ebe8-41ea-b092-e25e160d0a4d">

1. 사용자A의 HTTP 응답 완료
2. WAS는 사용이 끝난 thread-A를 쓰레드 풀에 반환한다.
3. thread-A는 쓰레드풀에 아직 존재하고, 따라서 쓰레드 로컬의 thread-A 전용 보관소에 사용자A 데이터도 함께 존재

**사용자B 조회 요청**

<img width="669" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/0988e3f1-7425-4864-9c1f-e3b7aea7e7f5">

1. 사용자B가 조회를 위한 새로운 HTTP 요청
2. WAS는 쓰레드 풀에서 쓰레드를 조회하는데 이때 thread-A가 할당
3. 조회 요청에 의해 thread-A는 쓰레드 로컬에서 데이터를 조회
4. 쓰레드 로컬은 thread-A 전용 보관소에 있는 사용자A 데이터를 반환한다
5. 결과적으로 사용자B는 사용자A의 정보를 조회한다

이런 문제를 예방하려면 사용자A의 요청이 끝날 때 쓰레드 로컬의 값을 `ThreadLocal.remove()` 를 통해서 꼭 제 거해야 한다.