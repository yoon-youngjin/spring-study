# CompletableFuture

- 비동기 프로그래밍을 쉽게 다루고 복잡한 비동기 작업을 효과적으로 처리할 수 있도록 해주는 도구로 자바 8에 도입되었다. 
- CompletableFuture는 값과 상태를 명시적으로 완료시킬 수 있는 Future로써 코드의 가독성을 높이고 비동기 작업의 조합을 간단하게 처리할 수 있다.
  - 기존의 Future는 값과 상태를 지정하는 api가 없었다.

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/0c6639ac-b983-4bc6-96c5-d61cf14b1cea)

- Future: submit 하는 즉시 Future를 반환하고 Future의 get을 통해서 결과를 기다리는 형태
- CompletableFuture: Main에서 CompletableFuture를 바로 접근하게되고 callable을 전달할 수 있는 api가 존재한다.

### Future & CompletableFuture

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/866f4d33-0efd-48fe-8d93-2303e7660ae4)

- 기존 Future를 사용한 코드와 마찬가지로 결과를 받아야하는 대기 시간이 존재하는것은 마찬가지이다.
- 단, Future보다 나은 가독성이 좋은 api를 제공하고, 예외 처리도 간편한 api를 제공한다.

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/fe2b6ec8-bebd-4523-9571-3cb4cca4cea2)

- CompletableFuture는 기본 스레드풀로 ForkJoinPool을 채택하여 병렬 처리를 기본으로 하는 반면에 Future는 동시성 처리를 위한 스레드 풀인 ExecutorService를 사용한다. (단, 추가 작업을 통해 병렬 처리도 가능하다.)
- Future의 isDone()은 정상이든 오류든 true를 반환한다.

## CompletableFuture Api 구조

- CompletableFuture는 비동기 작업과 함수형 프로그래밍의 콜백 패턴을 조합한 Future라고 할 수 있으며 2가지 유형의 API로 구분할 수 있다.
- CompletableFuture는 Future와 CompletionStage를 구현한 클래스로써 Future + CompletionStage라고 정의할 수 있다.

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/7d62c2ac-c93b-430b-8649-71811bdcbca7)

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/c1f07924-c6aa-4517-b515-c4bdc315b9ee)

### CompletionStage

- 비동기 작업을 위한 콜백 함수 API를 제공하며 어떤 작업이 완료된 후에 실행되어야 하는 후속 작업들을 정의하는 데 사용된다.
- 여러 비동기 작업들을 연속적으로 연결하여 실행할 수 있게 해준다.

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/cead131f-be41-405b-a25d-24ae468d2d9d)

### 비동기 작업 유형

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/9e0f9de8-5a95-4651-b284-c4bfee2a9b26)

## 비동기 작업 시작 - supplyAsync(), runAsync()

- CompletableFuture는 비동기 작업을 생성하고 실행하는 시작 메서드로 supplyAsync()와 runAsync()를 제공한다.
- CompletableFuture는 비동기 작업을 실행하기 위해 내부적으로 ForkJoinPool.commonPool()의 스레드 풀을 사용하며 서택적으로 ThreadPoolExecutor를 사용할 수 있다.

### supplyAsync()

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/321df2c6-71d7-4fb9-bed0-05c92be3ea0c)

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/d2449dd1-5649-4090-8494-0e581d23656e)

- CompletableFuture의 비동기작업을 위한 모든 api는 새로운 CompletableFuture를 생성하고 여기에 결과를 저장한다.
- AsyncSupply 객체가 WorkQueue에 저장되었다가 Thread가 꺼내서 실행하는 흐름이다.

### runAsync()

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/e025777e-2d0d-414f-9e8d-a74010638dd6)

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/39176e11-50c7-4490-abe0-24aed52bb8f8)

- supplyAsync와 거의 동일하지만 CompletableFuture에 결과를 저장하지 않고, null을 의미하는 AltResult 객체를 저장한다.





