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

병렬 스트림은 네 개의 상점에 대해 하나의 스레드를 할당해 네 개의 작업을 병렬로 수행했다.

만약 스레드는 4개지만 검색할 상점이 5개라면 4개의 상점을 검색하느라 모든 스레드를 사용하기 떄문에 다섯 번째 상점을 처리하는데 추가로 1초 정도의 시간이 소요된다.

4개의 스레드 중 누군가가 작업을 완료해야 다섯 번째 상점을 검색할 수 있다.

`CompletableFuture`에서는 작업에 사용할 `Executor`를 지정할 수 있기 때문에 스레드 풀의 크기를 조절하는 등 애플리케이션에 맞는 최적화된 설정을 할 수 있다.

### 커스텀 Executor 사용하기

- 스레드 풀 크기 조절
  - 스레드 풀의 최적값을 찾는 공식은 다음과 같다.
  - N(스레드) = NCpu _ UCpu _ (1 + W/C)
    - NCpu: `Runtime.getRuntime().availableProcessors()` 반환값
    - UCpu: 0과 1 사이의 값을 갖는 CPU 활용 비율
    - W/C: 대시기산과 계산시간의 비율

```java
      final Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100),
            new ThreadFactory() {
               @Override
               public Thread newThread(Runnable r) {
                  Thread t = new Thread(r);
                  t.setDaemon(true);
                  return t;
               }
            }
      );
```

위처럼 스레드 풀을 설정할 수 있다.

위의 코드에서는 스레드 풀의 스레드들을 **데몬 스레드**로 설정했다.

데몬 스레드는 자바 프로그램이 종료될 때 강제로 실행이 종료될 수 있다.

### 스트림 병렬화와 CompetalbeFuture 병렬화

- I/O가 포함되지 않은 계산 중심의 동작을 실행할 때는 스트림이 가장 구현하기 간단하며 효율적일 수 있다.
- 작업이 I/O를 기다리는 작업을 병렬로 실행한다면 CompetalbeFuture가 더 많은 유연성을 제공하며 대기/계산의 비율에 적합한 스레드 수를 설정할 수 있다

### 비동기 작업 파이프라인 만들기

```java
public class Discount {
   public enum Code {
      NONE(0),
      SILVER(5),
      GOLD(10),
      PLATINUM(15),
      DIAMOND(20);

      private final int percentage;

      Code(int percentage) {
         this.percentage = percentage;
      }
   }

   public static String applyDiscount(Quote quote) {
      return quote.getShopName() + " price is " +
            Discount.apply(quote.getPrice(),
                           quote.getDiscountCode());
   }

   private static void delay() {
      try {
         Thread.sleep(1000L);
      } catch (InterruptedException e) {
         throw new RuntimeException(e);
      }
   }

   private  static String apply(double price, Code code) {
      delay();
      return String.valueOf(price * (100 - code.percentage) / 100);
   }
}

```

할인과 관련한 기능을 추가한다.

```java
   public String getPrice(String product) {
      double price = calculatePrice(product);
      Discount.Code code = Discount.Code.values()[
            random.nextInt(Discount.Code.values().length)
            ];
      return String.format("%s:%.2f:%s", name, price, code);
   }
```

`Shop`클래스의 `getPrice` 를 수정한다.

```java
public class Quote {
   private final String shopName;
   private final double price;
   private final Discount.Code discountCode;

   public Quote(String shopName, double price, Discount.Code discountCode) {
      this.shopName = shopName;
      this.price = price;
      this.discountCode = discountCode;
   }

   public static Quote parse(String s) {
      String[] split = s.split(":");
      String shopName = split[0];
      double price = Double.parseDouble(split[1]);
      Discount.Code discountCode = Discount.Code.valueOf(split[2]);
      return new Quote(shopName, price, discountCode);
   }

   public String getShopName() {
      return shopName;
   }

   public double getPrice() {
      return price;
   }

   public Discount.Code getDiscountCode() {
      return discountCode;
   }
}
```

상점에서 제공하는 문자열을 파싱하는 `Quote` 클래스를 작성한다.

```java
public class NonBlockApiExample {
   public static void main(String[] args) {
      List<Shop> shops = Arrays.asList(
            new Shop("BestPrice"),
            new Shop("LetsSaveBig"),
            new Shop("MyFavorite"),
            new Shop("BuyItAll")
      );
      final Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100),
            new ThreadFactory() {
               @Override
               public Thread newThread(Runnable r) {
                  Thread t = new Thread(r);
                  t.setDaemon(true);
                  return t;
               }
            }
      );


      long start = System.nanoTime();
      long invocationTime = ((System.nanoTime() - start) / 1_000_000);

      System.out.println("Invocation returned after " + invocationTime  + " mesc");

      // 가격 계산 동안 다른 메서드 수행
      System.out.println(findPrices("iPhone", shops));


      long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
      System.out.println("price returned after " + retrievalTime + " mesc");
   }


   public static List<String> findPrices(String product, List<Shop> shops) {
      return shops.stream()
            .map(shop -> shop.getPrice(product))
            .map(Quote::parse)
            .map(Discount::applyDiscount)
            .collect(Collectors.toList());
   }
}
```

`Discount` 서비스를 이용하는 코드를 작성한다.

위의 `findPrices`는 3개의 `map`을 연결한다.

- 각 상점을 요청한 제품의 가격과 할인 코드로 변환
- 변환한 문자열을 파싱해 `Quote` 객체 생성
- `Discount` 서비스에 접근해 최종 할인가격을 계산하고 가격에 대응하는 상점 이름을 포함하는 문자열 반환

위의 코드는 성능 최적화와는 거리가 멀다.

`CompletableFuture`에서 수행하는 태스크를 설정할 수 있는 커스텀 `Executor`를 정의해 CPU 사용을 극대화할 수 있다.

```java
   public static List<String> findPrices(String product, List<Shop> shops, Executor executor) {
      List<CompletableFuture<String>> priceFutures = shops.stream()
            .map(shop -> CompletableFuture.supplyAsync(
                  () -> shop.getPrice(product), executor))
            .map(future -> future.thenApply(Quote::parse))
            .map(future -> future.thenCompose(
                  quote -> CompletableFuture.supplyAsync(
                        () -> Discount.applyDiscount(quote), executor
                  )
            )).collect(Collectors.toList());

      return priceFutures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());
   }
```

1. `CompletableFuture.supplyAsync`에 람다 표현식을 전달해 비동기적으로 상점에서 정보를 조회했다.
2. 1번의 결과 문자열을 `Quote`로 변환했다.
   1. `thenApply` 메서드는 `CompletableFuture`가 끝날 떄까지 블록하지 않는다.
   2. `CompletableFuture`가 동작을 완전히 완료한 다음 `thenApply` 메서드로 전달된 람다 표현식을 적용할 수 있다.
3. `thenCompose`를 이용해 두 개의 비동기 연산을 파이프라인으로 만들 수 있다.
   1. 상점에서 가격 정보를 얻어와 Quote로 변환
   2. Discount 서비스로 전달해 최동가격 획득

`thenCompose`는 첫 번째 연산의 결과를 두 번째 연산으로 전달한다.

`Future`가 여러 상점에서 `Quote`를 얻는 동안 메인 스레드는 다른 유용한 작업을 실행할 수 있다.

`CompletableFuture`가 완료되기를 기다렸다가 `join`으로 값을 추출할 수 있다.

`Async`로 끝나는 메서드가 존재하는데 다음 작업이 다른 스레드에서 실행되도록 스레드 풀로 작업을 제출하는 기능이 있다.

### 독립 CompleableFuture와 비독립 CompletableFuture 합치기

독립적으로 실행된 두개의 `CompleableFuture`를 합쳐야하는 상황이 발생하면

`thenCombine`메서드를 사용한다.

`thenCombine`메서드는 두 개의 `CompletableFuture` 결과를 어떻게 합칠지 정의한 `BiFunction`을 두 번째 인수로 받는다.

```java
Future<Double> futurePriceInUSD =
   CompleableFuture.supplyAsync(() -> shop.getPrice(product))
   .thenCombine(
      CompleableFuture.supplyAsync(
         () -> exchangeService.getRate(Money.EUR, Money.USD),
         (price, rate) -> prce * rate
      )
   );
```

첫 번째 태스크의 결과를 price로 두 번째 태스크의 결과를 rate로 받아 계산한다.

### 타임아웃 효과적으로 사용하기

자바 9에서는 `CompleableFuture`에 다양한 시간관련 기능이 추가되었다.

- `orTimeout`: 지정된 시간이 지난 후 `CompleableFuture`를 `TimeoutException`으로 완료시킨다.

  - 내부적으로 `ScheduledThreadExecutor`를 사용한다.

- `completeOnTimeout`: `CompleableFuture`를 반환하기 떄문에 이 결과를 다른 `CompleableFuture`와 연결할 수 있다.
  - 타임 아웃이 발생하면 미리 지정된 값을 사용할 수 있다.

```java
Future<Double> futurePriceInUSD =
   CompleableFuture.supplyAsync(() -> shop.getPrice(product))
   .thenCombine(
      CompleableFuture.supplyAsync(
         () -> exchangeService.getRate(Money.EUR, Money.USD),
         (price, rate) -> prce * rate
      )
   ).orTimeout(3, TimeUnit.SECONDS);
```

`Future`가 3초 흐에 작업을 끝내지 못할 경우 `TimeoutException`이 발생한다.

```java
Future<Double> futurePriceInUSD =
   CompleableFuture.supplyAsync(() -> shop.getPrice(product))
   .thenCombine(
      CompleableFuture.supplyAsync(
         () -> exchangeService.getRate(Money.EUR, Money.USD))
         .completeOnTimeout(DEFAULT_RATE, 1, TimeUnit.SECONDS),
         (price, rate) -> prce * rate
      )
   ).orTimeout(3, TimeUnit.SECONDS);
```

`completeOnTimeout`를 사용해 설정한 시간에 작업이 끝나지 않을 경우 지정된 값을 사용한다.

### CompletableFuture의 종료에 대응하는 방법
