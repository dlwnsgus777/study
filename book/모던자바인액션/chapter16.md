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

- 동기 API: 메서드를 호출한 다음 메서드가 계산을 완료할 때까지 기다렸다가 메서드가 반환되면 반환된 값으로 다른 동작을 수행한다.

  - 블록 호출이라고 부른다.

- 비동기 API: 메서드가 즉시 반환되며 끝내지 못한 나머지 작업을 호출자 스레드와 동기적으로 실행될 수 있도록 다른 스레드에 할당한다.
  - 비블록 호출이라고 부른다.

### 비동기 API 구현

```java
public class Shop {
   private Random random = new Random();
   public double getPrice(String product) {
      return calculatePrice(product);
   }

   public static void delay() {
      try {
         Thread.sleep(1000L);
      } catch (InterruptedException e) {
          throw new RuntimeException(e);
      }
   }

   private double calculatePrice(String product) {
      delay();
      return random.nextDouble() * product.charAt(0) + product.charAt(1);
   }
}
```

`getPrice()`는 호출시 1초의 지연 후 값을 반환하는 메서드이다.

이 메서드를 비동기 메서드로 변환하기 위해서는

```java
   public Future<Double> getPriceAsync(String product) {
      CompletableFuture<Double> futurePrice = new CompletableFuture<>();
      new Thread(() -> {
         double price = calculatePrice(product);
         futurePrice.complete(price);
      }).start();

      return futurePrice;
   }
```

위와 같이 반환값을 변환해야한다.

`Future`는 결과 값의 핸들일 뿐이며 계산이 완료되면 `get`메서드로 결과를 얻을 수 있다.

```java
public class NonBlockApiExample {
   public static void main(String[] args) {
      Shop shop = new Shop("BestShop");
      long start = System.nanoTime();
      Future<Double> futurePrice = shop.getPriceAsync("test product");
      long invocationTime = ((System.nanoTime() - start) / 1_000_000);

      System.out.println("Invocation returned after " + invocationTime  + " mesc");

      // 가격 계산 동안 다른 메서드 수행
      try {
         double price = futurePrice.get();
         System.out.printf("Price is %.2f%n", price);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
      long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
      System.out.println("price returned after " + retrievalTime + " mesc");
   }
}
```

위의 코드처럼 사용할 수 있다.

### 에러 처리 방법

다른 스레드에서 작업 중 에러가 발생하면 **해당 스레드에만 영향을 미친다.**

에러가 발생한다면 `Future`의 `get`을 영원히 기다리게 될 수도 있다.

이떄 다음 두가지 방법이 있다.

- 일정 시간 내에 결과값을 받지 못하면 `TimeoutException`을 일으킨다.
- `CompletableFutre` 내부에서 발생한 예외를 `completeExceptionally` 메서드를 이용해 클라이언트로 전달한다.

### 팩토리 메서드 supplyAsync로 CompletableFutre 만들기

```java
   public Future<Double> getPriceAsync(String product) {
      return CompletableFuture.supplyAsync(() -> calculatePrice(product));
   }
```

`supplyAsync` 메서드는 `Supplier`를 인수로 받아 `CompletableFuture`를 반환한다.

인수로 받은 `Supplier`를 실행해 비동기적으로 결과를 생성한다.

`ForkJoinPool`의 `Executor` 중 하나가 실행하게 된다.

두 번째 인수로 다른 `Executor` 를 지정할 수도 있다.

### 비블록 코드 만들기

```java
   public static void main(String[] args) {
      List<Shop> shops = Arrays.asList(
            new Shop("BestPrice"),
            new Shop("LetsSaveBig"),
            new Shop("MyFavorite"),
            new Shop("BuyItAll")
      );

      long start = System.nanoTime();
      long invocationTime = ((System.nanoTime() - start) / 1_000_000);

      System.out.println("Invocation returned after " + invocationTime  + " mesc");

      System.out.println(findPrices("iPhone", shops));

      long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
      System.out.println("price returned after " + retrievalTime + " mesc");
   }

   public static List<String> findPrices(String product, List<Shop> shops) {
      return shops.stream()
            .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
            .collect(Collectors.toList());
   }
```

여러개의 상점에서 가격을 계산하는 코드이다.

4개의 상점에서 가격을 검색하는 시간이 각각 1초의 대기시간이 있기 때문에 전체 결과는 4초 대로 나온다.

### 병렬 스트림으로 요청 병렬화하기

리스트에스 가격을 검색하는 요청을 병렬로 처리해 성능을 개선할 수 있다.

```java
   public static List<String> findPrices(String product, List<Shop> shops) {
      return shops.parallelStream() // 병렬 스트림
            .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
            .collect(Collectors.toList());
   }
```

결과를 확인해보면 4개의 상점에서 검색하는데 1초 대의 시간이 걸린다.

### CompletableFuture로 비동기 호출 구현하기

`CompletableFuture` 의 기능을 활용해 동기 호출을 비동기 호출로 바꿀 수 있다.

```java
   public static List<String> findPrices(String product, List<Shop> shops) {
      List<CompletableFuture<String>> priceFutures = shops.stream()
            .map(shop -> CompletableFuture.supplyAsync(
                  () -> shop.getName() + " price is " + shop.getPrice(product)
            )).collect(Collectors.toList());

      return priceFutures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());
   }
```

`join`은 `get`과는 다르게 아무런 예외를 발생시키지 않기 때문에 `try-catch`로 감쌀 필요가 없다.

두 map 연산을 하나의 스트림 처리에서 하지 않고 두개의 스트림 파이프라인으로 처리했다.

스트림은 게으른 특성이 있으므로 하나의 파이프라인으로 연산을 처리했다면 모든 가격 정보 요청 동작이 **동기적, 순차적**으로 이루어진다.

하지만 만족할만한 결과는 나오지 않았다.

### 더 확장성이 좋은 해결 방법
