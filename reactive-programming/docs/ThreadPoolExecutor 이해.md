# ThreadPoolExecutor 이해

ThreadPoolExecutor는 ExecutorService를 구현한 클래스로써 매개변수를 통해 다양한 설정과 조정이 가능하며 사용자가 직접 컨트롤 할 수 있는 스레드 풀이다.

## corePoolSize & maximumPoolSize - 기본 스레드 & 최대 스레드

<img width="730" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/c8a12d92-b14e-4210-a4b7-00095ac556b7">

- 자바에서는 기본 전략으로 작업이 제출 될 때 corePoolSize 미만의 스레드가 실행 중이면 corePoolSize가 될 때까지 스레드를 생성한다.
  - 옵션을 통해 미리 corePoolSize 만큼 스레드를 생성해놓을 수도 있다. (prestartCoreThread, prestartAllCoreThreads)
- maximumPoolSize 만큼 스레드를 늘리는 전략은 만약 현재 스레드 풀의 모든 쓰레드가 활성 상태에서 새로운 작업이 들어온다고 늘리는 것이 아니다.
  - 만약 큐 사이즈가 남아있다면 일단 큐에 작업을 넣는다. 
  - 큐가 가득찬 경우에 maximumPoolSize 까지 스레드를 생성한다.
- RejectedExecutionHandler: 큐에서 더 이상 작업을 받을 수 없는 경우 해당 작업에 대한 처리를 handler에 명시하여 처리할 수 있다.

```kotlin
fun main() {
    val corePoolSize = 2
    val maximumPoolSize = 4
    val keepAliveTime = 0L // corePoolSize를 제외한 나머지 유휴 상태의 스레드를 제거하는데 까지 기다리는 시간
//    val workQueue = LinkedBlockingQueue<Runnable>() 
    val workQueue = ArrayBlockingQueue<Runnable>(4)
//    val taskNum = 7 
//    val taskNum = 8 
    val taskNum = 9 
    val executor = ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue)

    for (i in 0..<taskNum) {
        executor.execute {
            Thread.sleep(1000)
            println("${Thread.currentThread().name} 가 태스트 $i 를 실행하고 있습니다.")
        }
    }
    executor.shutdown()
}
```

- LinkedBlockingQueue는 개수 제한이 없으므로 maximumPoolSize 만큼 스레드 풀이 생성될 수 없다. 
- taskNum = 7: corePoolSize + QueueSize = 6 이므로 task 개수가 1만큼 더 크기 때문에 쓰레드를 하나 추가한다. -> 2개의 corePool 쓰레드가 작업 2개를 가져가고, 나머지 4개의 작업이 작업 큐에 쌓여있는데, 마지막 작업이 큐에 못들어오는 상황이므로 스레드를 생성
- taskNum = 8: corePoolSize + QueueSize = 6 이므로 task 개수가 2만큼 더 크기 때문에 쓰레드를 두개를 추가한다.
- taskNum = 9: maximumPoolSize 만큼 스레드를 늘려도 하나의 작업이 큐에 들어오지 못하므로 에러가 발생한다. -> handler를 정의해서 처리할 수 있다.
  - ThreadPoolExecutor 생성할 때 별도의 handler를 지정하지 않고 생성하면 DefaultHandler로 세팅한다.
  - DefaultHandler는 Reject Policy를 AbortPolicy를 사용하는데 해당 정책은 Reject된 task가 RejectedExecutionException을 던진다. (따라서 오류 발생)

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/1f1f7f26-8ba6-4fd4-a4e2-3f83a78a0f90)

- prestart 옵션을 주지 않은 경우에는 작업 실행 전 poolSize = 0

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/e166c376-e85a-4176-b1bf-66ca51182656)

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/33da0d17-5451-4802-b00b-6e63563e67f4)

- prestart 옵션을 주는 경우 
  - prestartCoreThread: 스레드 1개만 생성
    - 해당 옵션은 호출할 때마다 1개씩 생성하는데 최대 corePoolSize 만큼만 생성 가능하다.
  - prestartAllCoreThreads: corePoolSize 만큼 생성

## keepAliveTime - 스레드 유휴 시간 설정

- corePoolSize 보다 더 많은 스레드가 존재하는 경우 각 스레드가 keepAliveTime 보다 오랜 시간 동안 유휴 상태였다면 스레드는 종료된다.
- keep-alive 정책은 corePoolSize 스레드 보다 많은 스레드가 있을 때만 적용 되지만 allowCoreThreadTimeout(boolean) 메서드를 사용하여 core 스레드에도 적용할 수 있다.
- Executors.newCachedThreadPool() 로 풀이 생성된 경우 대기 제한 시간이 60초이며 Executors.newFixedThreadPool()로 생성된 경우 제한 시간이 없다.
  - Executors.newFixedThreadPool()로 생성된 스레드 풀은 corePoolSize와 maximumPoolSize가 동일하기 때문

```kotlin
fun main() {
    val corePoolSize = 2
    val maximumPoolSize = 4
    val keepAliveTime = 1L
    val workQueue = LinkedBlockingQueue<Runnable>(2)
    val executor = ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue)

    val taskNum = 6

    for (i in 0..<taskNum) {
        executor.execute {
            Thread.sleep(2000)
            println("${Thread.currentThread().name} 가 태스트 $i 를 실행하고 있습니다.")
        }
    }

//    executor.allowCoreThreadTimeOut(true) // corePool도 제거

    Thread.sleep(4000)
    executor.shutdown()
}
```

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/930c7b02-34a6-4d5f-90ce-96c1e8964b8f)

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/273d616a-d181-4dbe-b37a-b153c773f77f)

- executor.allowCoreThreadTimeOut(true)로 설정하면 sleep(4000) 이후 모든 쓰레드가 제거된다.

## BlockingQueue

- 기본적으로 스레드 풀은 작업이 제출되면 corePoolSize 의 새 스레드를 추가해서 작업을 할당하고 큐에 작업을 바로 추가하지 않는다.
  - 이 경우에는 큐에 쌓이지 않고 바로 쓰레드가 작업을 가져간다.
- corePoolSize 를 초과해서 스레드가 실행 중이면 새 스레드를 추가해서 작업을 할당하는 대신 큐에 작업을 추가한다. (큐가 가득찰 때까지)
- 큐에 공간이 가득차게 되고 스레드가 maxPoolSize 이상 실행 중이면 더 이상 작업은 추가되지 않고 거부된다.
- 블로킹 큐는 내부적으로 락을 통해 동기화 처리가 되어 있다.

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/87d8cc0f-3190-408b-aea4-22ca508325db)

> LinkedBlockingQueue도 capacity를 할당 할 수 있다.

### LinkedBlockingQueue

<img width="728" alt="image" src="https://github.com/yoon-youngjin/spring-study/assets/83503188/e54b810f-13d0-4df2-80f8-f8e0e688b10f">

- 큐가 가득찬 경우 add나 offer 메서드를 사용한다면 예외 또는 오류가 발생하지만 put은 큐에 데이터를 넣을때까지 블록된다.
- 쓰레드 풀 관점에서 쓰레드들은 큐에 작업이 없다면 작업을 가져갈때까지 블록된다. 따라서 take() 메서드를 사용

## RejectedExecutionHandler - 스레드 풀 포화 정책

- execute(Runnable)로 제출된 작업이 풀의 포화로 인해 거부될 경우 execute 메서드는 RejectedExecutionHandler.rejectedExecution() 메서드를 호출한다.
- 미리 정의된 네 가지 핸들러 정책 클래스가 제공되며 직접 사용자 정의 클래스를 만들어 사용할 수 있다.
  - ThreadPoolExecutor.AbortPolicy: 기본값, 작업 거부 시 RejectedException 예외 발생
  - ThreadPoolExecutor.CallerRunsPolicy: Executor 가 종료 되지 않은 경우 execute를 호출한 스레드에서 작업을 실행한다.
    - task를 호출한 쓰레드에서 작업을 처리함 (아래 그림에서는 Main 쓰레드)
  - ThreadPoolExecutor.DiscardPolicy: 거부된 작업을 그냥 무시한다.
  - ThreadPoolExecutor.DiscardOldestPolicy: Executor 가 종료되지 않은 경우 대기열의 맨 앞에 있는 작업을 삭제하고 실행이 재시도 된다. 재시도가 실패하면 반복 될 수 있다.

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/b9289036-2354-4711-9105-eb0098e4d0c1)


```java
public static class AbortPolicy implements RejectedExecutionHandler {
  // ...
  public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
    throw new RejectedExecutionException("Task " + r.toString() +
            " rejected from " +
            e.toString());
  }
}
```

```java
public static class DiscardPolicy implements RejectedExecutionHandler {
        // ...
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        }
    }
```

```java
public static class CallerRunsPolicy implements RejectedExecutionHandler {
    // ...
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        if (!e.isShutdown()) {
            r.run();
        }
    }
}
```

```java
public static class DiscardOldestPolicy implements RejectedExecutionHandler {
    // ...    
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                e.getQueue().poll();
                e.execute(r);
            }
        }
    }
```

## ThreadPoolExecutor Hook - 스레드 풀 Hook 메서드

- ThreadPoolExecutor 는 스레드 풀을 관리하고 작업 실행 시점에 특정 이벤트를 처리하기 위한 Hook 메서드를 제공한다.
- ThreadPoolExecutor 를 상속하고 Hook 메서드를 재정의하여 작업 스레드 풀의 동작을 사용자 정의할 수 있으며 3개의 Hook 메서드가 있다.
  - beforeExecute(Thread t, Runnable r): 작업 스레드가 작업을 실행하기 전에 호출되는 메서드로서 제출된 각 작업마다 한번씩 호출되고 작업 실행 전에 동작을 추가할 수 있다.
  - afterExecute(Runnable r, Throwable t): 작업 스레드가 작업을 실행한 후에 호출되는 메서드로서 제출된 각 작업마다 한번씩 호출되고 작업 실행 후에 동작을 추가하거나 예외 처리를 수행할 수 있다.
    - 정상적인 경우에는 Throwable null
  - terminated(): 스레드 풀이 완전히 종료된 후에 호출되는 메서드로서 스레드 풀이 종료되면 이 메서드를 재정의하여 클린업 작업을 수행할 수 있다.

## 생명 주기와 상태 & ThreadPoolExecutor 아키텍처 이해 

ThreadPoolExecutor 는 다양한 생명 주기와 상태를 가지며 이러한 상태에 따라 작업 스레드 풀의 동작이 결정된다.

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/4d0defdb-1994-45e2-bf5f-29ee7e661843)

- SHUTDOWN 상태: shutdown() 호출
- STOP 상태: shutdownNow() 호출

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/6db1b187-ce66-4104-96c8-75347fe36145)

### ThreadPoolExecutor 흐름도

![image](https://github.com/yoon-youngjin/spring-study/assets/83503188/355d66be-17c2-4689-957d-abb26cd09864)

- 현재 스레드 풀의 사이즈가 corePoolSize 미만이라면 Queue에 담지 않고 바로 즉시 쓰레드를 생성해서 실행한다.
- Worker는 실제 스레드는 아니고 스레드가 실행할 작업을 가진 클래스이다.
  - Worker 내부에서 ThreadFactory를 가지고 Thread를 생성하고, 이렇게 생성된 Thread가 Runnable 타입의 Worker를 실행한다.


```java
private final class Worker
        extends AbstractQueuedSynchronizer
        implements Runnable {
    // ...
    Worker(Runnable firstTask) {
        setState(-1); // inhibit interrupts until runWorker
        this.firstTask = firstTask;
        this.thread = getThreadFactory().newThread(this);
    }
   // ...
   final void runWorker(Worker w) {
     Thread wt = Thread.currentThread();
     Runnable task = w.firstTask;
     w.firstTask = null;
     w.unlock(); // allow interrupts
     boolean completedAbruptly = true;
     try {
       while (task != null || (task = getTask()) != null) {
         w.lock();
         // If pool is stopping, ensure thread is interrupted;
         // if not, ensure thread is not interrupted.  This
         // requires a recheck in second case to deal with
         // shutdownNow race while clearing interrupt
         if ((runStateAtLeast(ctl.get(), STOP) ||
                 (Thread.interrupted() &&
                         runStateAtLeast(ctl.get(), STOP))) &&
                 !wt.isInterrupted())
           wt.interrupt();
         try {
           beforeExecute(wt, task);
           try {
             task.run();
             afterExecute(task, null);
           } catch (Throwable ex) {
             afterExecute(task, ex);
             throw ex;
           }
         } finally {
           task = null;
           w.completedTasks++;
           w.unlock();
         }
       }
       completedAbruptly = false;
     } finally {
       processWorkerExit(w, completedAbruptly);
     }
   }
}
```

- this.thread = getThreadFactory().newThread(this); : Runnable 타입의 this(Worker)을 Thread의 파라미터로 전달한다. 
- 또한 Runnable을 구현하였으므로 run 메서드를 구현하고 있으며, Thread는 run 메서드를 실행한다.
- ThreadPoolExecutor 역시 여러 쓰레드에서 접근이 가능하므로 addWorker 메서드 내부에서 락을 통해 동기화 작업을 하고있다.