## CompletableFuture: 안정적 비동기 프로그래밍

---

### Future의 단순 활용

자바 5부터는 미래의 어느 시점에 결과를 얻는 모델에 활용할 수 있도록 `Future` 인터페이스를 제공하고 있다.

비동기 계산을 모델링하는데 이용할 수 있고, 계산이 끝났을 때 결과에 접근할 수 있는 참조를 제공한다.

시간이 걸릴 수 있는 작업을 `Future` 내부로 설정하면 호출자 스레드가 결과를 기다리는 동안 다른 작업을 수행할 수 있다.

`Future`는 저수준의 스레드에 비해 직관적으로 이해하기 쉽다는 장점이 있다.

`Future`를 사용하려면 시간이 오래 걸리는 작업을 `Callable` 객체 내부로 감싼 다음에 `ExecutorService`에 제출해야한다.

```java
   public static void main(String[] args) {
      ExecutorService executorService = Executors.newCachedThreadPool();

      // Callable을 ExecutorService에 제출
      Future<Double> future = executorService.submit(new Callable<Double>() {
         public Double call() {
            // 시간이 오래 걸리는 작업을 다른 스레드에서 비동기적으로 실행
            return doSomeLongComputation();
         }
      });

      // 비동기 작업을 수행하는 동안 다른 작업을 수행할 수 있다.
      doSomethingElse();
      try {
         Double result = future.get(1, TimeUnit.SECONDS);
      } catch (ExecutionException ee) {
         // 계산 중 예외 발생
      } catch (InterruptedException ie) {
         // 현재 스레드에서 대기 중 인터럽트 발생
      } catch (TimeoutException te) {
         // Future가 완료되기 전에 타임아웃 발생
      }
   }
```

`ExecutorService` 에서 제공하는 스레드가 시간이 오래 걸리는 작업을 처리하는 동안 우리 스레드로 다른 작업을 동시에 실행할 수 있다.

`Future`의 `get()` 메서드로 결과를 가져올 수 있다.

이 때 계산이 완료되어 결과가 준비되었다면 즉시 결과를 반환하지만 준비가 되지 않았다면 작업이 완료될 때까지 우리 스레드를 **블록시킨다.**

### Future 제한

`Future`인터페이스는 비동기 계산이 끝났는지 확인할 수 있는 `isDone` 메서드와 계산이 끝나길 기다리는 `get`메서드 등을 제공하지만 이런 메서드들만으로는 간결한 동시 실행 코드를 구현하기에 충분하지 않다.

여러 `Future`의 결과가 있을 때 이들의 의존성은 표현하기 어렵다.

오래 걸리는 A의 계산이 끝나면 그 결과를 다른 계산 B로 전달하는 등의 요구사항을 구현하기가 쉽지않다.

`Future`를 선언형으로 쉽게 사용하기 위해 자바 8에서는 `Future`인터페이스의 구현체인 `CompletableFuture` 클래스를 제공한다.

`CompletableFuture` 는 람다 표현식과 파이프라이닝을 활용한다.

### ComputableFuture로 비동기 애플리케이션 만들기
