## 리액티브 프로그래밍

---

### 리액티브 매니패스토

리액티브 매니패스토는 리액티브 애플리케이션과 시스템 개발의 핵심 원칙을 공식적으로 정의한다.

- 반응성: 리액티브 시스템은 빠를 뿐 아니라 일정하고 예상할 수 있는 반응 시간을 제공한다.
- 회복성: 장애가 발생해도 시스템은 반응해야 한다.
- 탄력성: 무거운 작업 부하가 발생하면 자동으로 관련 컴포넌트에 할당된 자원 수를 늘린다.
- 메시지 주도: 회복성과 탄력성을 지원하기 위해서는 약한 결합, 고립, 위치 투명성 등을 지워할 수 있도록 비동기 메시지를 전달해 컴포넌트 끼리의 통신이 이루어진다.

### 애플리케이션 수준의 리액티브

애플리케이션 수준의 리액티브 프로그래밍의 주요 기능은 **비동기로 작업을 수행**할 수 있다는 것이다.

리액티브 프레임워크와 라이브러리를 이용하면 동기 블록, 경쟁 조건 같은 저 수준의 멀티스레드 문제를 직접 처리할 필요가 없어진다는 장점이 있다.

### 시스템 수준의 리액티브

리액티브 시스템은 여러 애플리케이션이 한 개의 일관적인, 회복할 수 있느 플랫폼을 구성할 수 있게 해주고, 애플리케이션 중 하나가 실패해도 전체 시스템은 계속 운영될 수 있도록 도와주는 **소프트웨어 아키텍쳐**이다.

리액티브 시스템의 주요 속성으로는 **메시지 주도**가 있다.

고립과 비결합이 회복성의 핵심이라면 탄력성의 핵심은 위치 투명성이다.

위치 투명성은 리액티브 시스템의 모든 컴포넌트가 수신자의 위치에 상관없이 다른 모든 서비스와 통신할 수 있음을 의미한다.

### 리액티브 스트림과 플로 API

리액티브 프로그래밍은 **리액티브 스트림**을 사용하는 프로그래밍이다.

**리액티브 스트림**은 잠재적으로 무한의 비동기 데이터를 순서대로 블록하지 않은 역압력을 전제해 처리하는 표준 기술이다.

비동기 API를 이용하면 하드웨어 사용률을 극대화할 수 있지만 다른 느린 다운스트림 컴포넌트에 너무 큰 부하를 줄 가능성이 생긴다.

이런 상황을 방지하기 위해 역압력이나 제어 흐름 기법이 필요하다.

### Flow 클래스 소개

자바 9에서는 리액티브 프로그래밍을 제공하는 `Flow`를 추가했다.

Flow 클래스는 중첩된 인터페이스 네 개를 포함한다.

- `Publisher`: 발행자
- `Subscriber`: 소비자
- `Subscription`: `Publisher`와 `Subscriber` 사이에 제어 흐름, 역압력을 관리
- `Processor`: `Publisher`를 구독한 다음 수신한 데이터를 가공해 다시 제공한다.

```java
public interface Publisher<T> {
        public void subscribe(Subscriber<? super T> subscriber);
    }
```
```java
    public interface Subscriber<T> {
        public void onSubscribe(Subscription subscription);
        public void onNext(T item);
        public void onError(Throwable throwable);
        public void onComplete();
    }
```
```java
public interface Subscription {
        public void request(long n);
        public void cancel();
    }
```

```java
   public interface Processor<T,R> extends Subscriber<T>, Publisher<R> {
    }
```
`Publisher`가 이벤트를 발행할 때 호출 할 콜백 메서드 네 개를 `Subscriber`에서 정의한다.

이벤트는 다음 프로토콜에서 정의한 순서로 지정된 메서드 호출을 통해 발행되어야한다.

```java
onSubscribe onNext* (onError | onComplete)?
```

`onSubscribe`가 항상 처음 호출되고 이어서 `onNext`가 여러번 호출될 수 있다.

이벤트 스트림은 영원히 지속되거나 `onComplete`콜백을 통해 더 이상 데이터가 없고 종료됨을 알린다.

`Publisher`에서 장애가 발생했을 때는 `onError`를 호출할 수 있다.


`Subscriber`가 `Publisher`에 자신을 등록할 때 `Publisher`는 처음으로 `onSubscribe`를 호출해 `Subscription`객체를 전달한다.


`request`는 주어진 개수의 이벤트를 처리할 준비가 되었음을 알리고

`cancel`을 호출해 구독을 취소할 수 있다.

자바 9의 플로 명세서에서는 인터페이스들이 어떻게 협력해야 하는지 설명하는 규칙 집합을 정의한다.

- `Publisher`는 반드시 `Subscription`의 `request` 메서드에 정의된 개수 이하의 요소만 `Subscriber`에게 전달해야 한다.
  - 동작이 성공적으로 끝났으면 `onComplete`를 호출한다.
  - 문제가 발생하면 `onError`를 호출해 `Subscription`을 종료할 수 있다.
- `Subscriber`는 요소를 받아 처리할 수 있음을 `Publisher`에게 알려야한다.
  - `onComplete`, `onError` 신호를 처리하는 상황에서 `Subscriber`는 `Subscription`이나 `Publisher`의 어떤 메서드도 호출할 수 없다.
  - `request` 호출 없이도 언제든 종료 시그널을 받을 준비가 되어있어야 한다.
  - `cancel`이 호출되더라도 한 개 이상의 `onNext`를 받을 준비가 되어있어야 한다.
- `Publisher`와 `Subscriber`는 `Subscription`을 공유해야 하며 각각이 고유한 역할을 수행해야 한다.
  - `onSubscribe`와 `onNext` 메서드에서 `request` 메서드를 동기적으로 호출할 수 있어야한다.
  - `cancel`를 몇 번을 호출해도 한 번 호출한 것과 같은 효과를 가져야한다. 여러 번 호출해도 스레드에 안전해야 한다.
  - 같은 `Subscriber` 객체에 다시 가입하는 것은 권장하지 않지만 강제하지는 않는다.
  
### Reactor 예제코드

```java
public class TempInfo {
   public static final Random random = new Random();

   private final String town;
   private final int temp;

   public TempInfo(String town, int temp) {
      this.town = town;
      this.temp = temp;
   }

   public static TempInfo fetch(String town) {
      if (random.nextInt(10) == 0) {
         throw new RuntimeException("Error");
      }

      return new TempInfo(town, random.nextInt(100));
   }

   @Override
   public String toString() {
      return "TempInfo{" +
            "town='" + town + '\'' +
            ", temp=" + temp +
            '}';
   }

   public String getTown() {
      return town;
   }

   public int getTemp() {
      return temp;
   }
}
```

```java
Flux.interval(Duration.ofSeconds(1L))
            .subscribeOn(Schedulers.boundedElastic())
//            .doOnError(e -> log.info("Error")) doOnError는 오류 신호를 살펴보는데만 사용됨
            .subscribe(i -> log.info(TempInfo.fetch("New York").toString()), e -> log.info("에러 발생"));
```

```java
 // flux merge

      var one = Flux.just(1);
      var two = Flux.just(2);
      var three = Flux.just(3);

      Flux.merge(one, two, three)
                      .subscribe(i -> log.info(TempInfoNormal.fetch("New York", i).toString()), e -> log.info("에러 발생"));

```

```java
 // map
      Flux.just(1)
              .map(i -> i + 30)
              .subscribe(i -> log.info(TempInfoNormal.fetch("New York", i).toString()), e -> log.info("에러 발생"));
```