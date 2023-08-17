## 리액티브 프로그래밍 컨셉의 기초

---

앞으로 만들 웹 애플리케이션은 다양한 소스의 콘텐츠를 가져와 합치는 매시업 형태가 될 가능성이 크다.

다양한 서비스의 응답을 기다리는 동안 연산이 블록되거나 CPU 클록 사이클 자원을 낭비하고 싶지는 않다.

이때 이 작업을 여러 하위 태스크로 나눠 CPU의 다른 코어 또는 다른 머신에서 태스크를 **병렬**로 실행한다.

반면 연관된 작업을 같은 CPU에서 동작하는 것 또는 코어를 바쁘게 유지하는 것이 목표인 **동시성을 필요로 하는 상황**에서는 스레드를 블록함으로 인한 연산 자원을 낭비하는 일은 피해야한다.

이런 환경에서 사용할 수 있는 두 가지 도구를 자바에서 제공한다.

- 자바 8의 `CompletableFuture`
- 자바 9에서 추가된 리액티브 프로그램이 개념의 Flow API

동시성과 병렬성의 차이는 다음과 같다.

- 동시성은 단일 코어 머신에서 발생할 수 있는 프로그래밍 속성
- 병렬성은 병렬 실행을 하드웨어 수준에서 지원

### 동시성을 구현하는 자바 지원의 진화

처음 자바는 `Runnable`, `Thread` 를 동기화된 클래스와 메서드를 이용해 **잠궜다.**

자바 5에서는 스레드 실행과 태스크 제출을 분리하는 `ExcuterService` 인터페이스와 `Runnable`, `Thread`의 변형을 반환하는 `Callable<T>`, `Future<T>`, 제네릭을 지원했다.

자바 7에서는 **포크/조인 프레임워크**와 `RecursiveTask`가 추가되었고

자바 8에서는 스트림과 새로 추가된 람다 지원에 기반한 병렬 프로세싱이 추가되었다.

또한 동시성을 강화하기 위해 `Future`를 조합하는 기능을 자바 8에 추가했고, 자바 9에서는 분산 비동기 프로그래밍을 명시적으로 지원한다.

`CompletableFuture`와 `Flow API`의 궁극적인 목표는 **가능한한 동시에 실행할 수 있는 독립적인 태스크를 만들면서 멀티코어 또는 여러 기기를 통해 제공되는 병렬성을 쉽게 이용하는 것**이다.

### 스레드와 높은 수준의 추상화

스레드는 본인이 가진 프로세스와 같은 주소 공간을 공유하는 프로세스이다.

멀티코어 설정에서는 스레드의 도움없이 프로그램이 컴퓨팅 파워를 모두 활용할 수 없다.

프로그램이 스레드를 사용하지 않는다면 효율성을 고려해 여러 프로세서 코어 중 한 개만을 사용할 것이다.

스트림을 이용해 스레드 사용 패턴을 추상화할 수 있다.

### Excutor와 스레드 풀

자바 5는 `Excutor` 프레임워크와 스레드 풀을 통해 태스크 제출과 실행을 분리할 수 있는 기능을 제공했다.

자바 스레드는 운영체제 스레드에 접근한다.

운영 체제 스레드를 사용하기 위해서는 비싼 비용이 드는데 운영체제 스레드의 숫자는 제한되어 있다.

운영체제가 지원하는 스레드 수를 초과해 사용하면 자바 애플리케이션이 예상치 못한 방식으로 크래시될 수 있다.

그러므로 기존 스레드가 실행되는 상태에서 계속 새로운 스레드를 만드는 상황이 일어나지 않도록 주의해야한다.

보통 운영체제와 자바의 스레드 개수가 하드웨어 스레드 개수보다 많으므로 일부 운영체제 스레드가 블록되거나 자고있는 상황에서 모든 하드웨어 스레드가 코드를 실행하도록 할당되도록 만들 수 있다.

**프로그램에서는 미리 사용할 스레드를 추측하지 않는 것이 좋고, 사용할 최적의 자바 스레드 개수는 하드퀘어 코어의 개수에 따라 달라진다.**

자바의 `ExcutorService`는 태스크를 제출하고 나중에 결과를 수집할 수 있는 인터페이스를 제공한다.

팩토리 메서드 중 하나를 이용해 스레드 풀을 만들어 사용한다.

스레드 풀에서 사용하지 않은 스레드로 제출된 태스크를 먼저 온 순서대로 실행한다.

**하드웨어에 맞는 수의 태스크를 유지함과 동시에 수 천개의 태스크를 스레드 풀에 아무 오버헤드 없이 제출할 수 있다**는 장점이 있다.

**태스크를 제공하면 스레드가 이를 실행한다.**

스레드 풀을 사용할 때 두가지 사항을 주의해야 한다.

1.  K 스레드를 가진 스레드 풀은 오직 K만큼의 스레드를 동시에 실행할 수 있다.

- 초과로 제출된 태스크는 큐에 저장되며 이전에 실행된 태스크 중 하나가 종료되기 전까지 스레드에 할당하지 않는다.
- 이때 다른 태스크에서 sleep이나 블록된 상황이 발생하면 작업의 효율성이 떨어지게 된다.

2. 중요한 코드를 실행하는 스레드가 죽는 일이 발생하지 않도록 main이 반환하기 전에 모든 스레드 작업이 끝나길 기다린다.
   - 프로그램을 종료하기 전에 모든 스레드 풀을 종료하는 습관을 갖는 것이 중요하다.

### 스레드의 다른 추상화 : 중첩되지 않은 메서드 호출

스레드 생성과 `join()`이 한 쌍처럼 중첩된 메서드 호출 내에 추가하는 것을 **엄격한 포크/조인**이라 부른다.

시작된 태스크를 내부 호출이 아닌 외부 호출에서 종료하도록 기다리는 좀더 여유로운 방식의 **포크/조인** 사용법도 있다.

여유로운 방식은 사용자의 메서드 호출에 의해 스레드가 생성되고 메서드를 벗나 계속 실행되는 **동시성 형태**이다.

메서드 호출자에 기능을 제공하도록 메서드가 반환된 후에도 만들어진 태스크 실행이 계속되는 메서드를 **비동기 메서드**라고 한다.

이런 비동기 메서드를 사용할 때는 두 가지 위험성이 따른다.

1. 스레드 실행은 메서드를 호출한 다음의 코드와 동시에 실행되므로 데이터 경쟁 문제를 일으키지 않도록 주의해야한다.
2. 기존 실행 중이던 스레드가 종료되지 않은 상황에서 자바의 `main` 메서드가 반환하면 다음 두가지 방법이 있는데 둘다 안전하지 못하다.
   1. 모든 스레드의 실행이 끝날 때까지 애플리케이션을 종료하지 못한다.
   2. 스레드를 강제 종료시키고 애플리케이션을 종료한다.

이러한 문제를 피하기 위해서는 애플리케이션 종료 전 스레드 풀을 포함한 모든 스레드를 종료하는 것이 좋다.

자바 스레드는 `setDaemon()` 메서드를 이용해 **데몬** 또는 비데몬으로 구분시킬 수 있다.

데몬 스레드는 애플리케이션이 종료될 때 강제 종료되고, `main()` 메서드는 모든 비데몬 스레드가 종료될 때까지 프로그램을 종료하지 않고 기다린다.

### 스레드에 무엇을 바라는가?

모든 하드웨어 스레드를 활용해 병렬성의 장점을 극대화하도록 프로그램을 작은 태스크 단위로 구조화하는 것이 목표이다.

### 동기 API와 비동기 API

```java
int y = f(x);
int z = g(x);

System.out.println(y + z);
```

다음 f와 g 두 개의 함수를 실행하는데 오랜시간이 걸린다고 가정하면

두 개의 함수를 별도의 CPU 코어로 실행하는 것으로 두 작업중 더 오래 걸리는 작업의 시간으로 합계를 구하는 시간을 단축할 수 있다.

하지만 이는 단순했던 코드를 복잡하게 변화시킨다.

이를 명시적 반복으로 병렬화를 수행하던 코드를 스트림을 이용해 내부 반복으로 바꾼것처럼 비슷한 방법으로 이 문제를 해결해야 한다.

**비동기 API**를 사용해 해결할 수 있다.

### Future 형식 API

`Future` 를 사용하면 f, g의 시그니처가 다음처럼 바뀐다.

```java
Future<Integer> f(int x);
Future<Integer> g(int x);
```

```java
Future<Integer> y = f(x);
Future<Integer> z = g(x);
System.out.println(y.get() + z.get());
```

메서드 f는 호출 즉시 자신의 원래 바디를 평가하는 태스크를 포함하는 `Future`를 반환한다.

하지만 조금 더 큰 프로그램에서는 두 가지 이유로 이런 방식을 사용하지 않는다.

1. 다른 상황에서는 g에도 Future 형식이 필요할 수 있으므로 API 형식을 통일하는 것이 바람직하다.
2. 병렬 하드웨어로 프로그램 실행 속도를 극대화하려면 여러 작은 태스크로 나누는 것이 좋다.

### 리액티브 형식 API

f와 g의 시그니처를 바꿔서 콜백 형식의 프로그래밍을 이용하는 방법이다.

```java
void f(int x, IntCunsumer dealWithResult);
```

f에 추가 인수로 콜백을 전달해 바디에서 `return`문으로 결과를 반환하는 것이 아닌 **결과가 준비되면 이를 호출하는 태스크를 만드는 것**이다.

하지만 f와 g의 호출 합계를 정확하게 출력하지 않고 상황에 따라 다른 값을 출력한다.

락을 사용하지 않아 값을 두 번 출력할 수도 있고, 다음 라인의 코드가 호출되기 전에 업데이트 될수도 있다.

다음 두 가지 방법으로 이 문제를 보완할 수 있다.

1. `if-then-else`를 이용해 적절한 락을 사용해 콜백이 모두 호출되었는지 확인한다.
2. 리랙티브 형식의 API는 보통 한 결과가 아니라 일련의 이벤트에 반응하도록 설계되었으므로 `Future`를 이용하는 것이 더 적절하다.

리액티브 형식의 API는 일련의 값을 `Future`는 일회성의 값을 처리하는데 적합하다.

다음 두가지 상황에서 비동기 API를 사용하면 효율성이 크게 향상된다.

- 계산이 오래 걸리는 메서드
- 네트워크나 사람의 입력을 기다리는 메서드

### 잠자기 (그리고 기타 블로킹 동작)는 해로운 것으로 간주

스레드는 잠들어도 여전히 **시스템 자원을 점유**한다.

스레드 풀에서 잠을 자는 태스크는 다른 태스크가 시작되지 못하게 막는다.

이때 운영 체제가 태스크를 관리하기 때문에 일단 스레드로 할당된 태스크는 중지시키지 못한다.

이는 모든 블록 동작에서도 마찬가지이다.

태스크를 앞과 뒤 두 부분으로 나누고 블록되지 않을 때만 뒷부분을 자바가 스케쥴링하도록 요청할 수 있다.

```java
   public static void main(String[] args) {
      ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);


      work1();
      scheduledExecutorService.schedule(
              ModernJavaChapter15::work2,
              10,
              TimeUnit.SECONDS
      );// work1이 끝난 후 10초 뒤에 work2를 개별 태스크로 스케쥴
      scheduledExecutorService.shutdown();

   }

   public static void work1() {
      System.out.println("work1!");
   }

   public static void work2() {
      System.out.println("work2!");
   }
```

위의 방식은 다른 작업이 실행될 수 있도록 스레드를 점유하지 않는다는 장점이 있다.

태스크가 실행되면 귀중한 자원을 점유하므로 태스크가 끝나서 자우너을 해제하기 전까지 태스크를 계속 실행해야 한다.

태스크를 블록하는 것보다는 다음 작업을 태스크로 제출하고 현재 태스크는 종료하는 것이 바람직하다.

자바 `CompleableFuture` 인터페이스는 `Future`에 `get`을 이용해 명시적으로 블록하지 않고 콤비네이터를 사용함으로 위와 같은 코드를 런타임 라이브러리 내에 추상화한다.

### 비동기 API에서 예외는 어떻게 처리하는가?

비동기 API에서 메서드의 실제 바디는 별도의 스레드에서 호출되며 이때 발생하는 에러는 이미 호출자의 실행 범위와는 관계가 없는 상황이 된다.

`CompleableFuture` 에서는 예외를 처리할 수 있는 기능을 제공하며

리액티브 형식의 API 에서는 Return대신 기존 콜백이 호출되므로 예외가 발생했을 때 실행될 추가 콜백을 만들어 인터페이스를 바꿔야한다.

자바 9의 플로API 에서는 여러 콜백을 한 객체로 감싼다.

### CompleableFuture 와 콤비네이터를 이용한 동시성

일반적으로 `Future`는 실행해서 `get()`으로 결과를 얻을 수 있는 `Callable`로 만들어진다.

하지만 `CompleableFuture`는 실행할 코드 없이 `Future`를 만들 수 있도록 허용하며 `complete()` 메서드를 이용해 나중에 어떤 값을 이용해 다른 스레드가 이를 완료할 수 있고 `get()`으로 값을 얻을 수 있도록 허용한다.

f(x)와 g(x) 를 동시에 질행해 합계를 구한 코드는 다음과 같다.

```java
   public static void main(String[] args) {
      ExecutorService executorService = Executors.newFixedThreadPool(10);
      int x = 1337;

      CompletableFuture<Integer> a = new CompletableFuture<>();
      executorService.submit(() -> a.complete(f(x)));
      int b = g(x);
      System.out.println(a.get() + b);

      executorService.shutdown();
   }
```

a 대신 b를 `complete` 하는 방법도 있다.

f(x)의 실행이 끝나지 않거나 실행이 끝나지 않은 상황에서 `get`을 기다려야 하므로 프로세싱 자원을 낭비할 수 있다.

`compose()`, `andThen()` 같은 메서드를 두 `Function`에 이용해 다른 `Function`을 얻을 수 있다.

`CompletableFuture<T>`에 `thenCombine` 메서드를 사용함으로 두 연산 결과를 효과적으로 더할 수 있다.

```java
   public static void main(String[] args) {
      ExecutorService executorService = Executors.newFixedThreadPool(10);
      int x = 1337;

      CompletableFuture<Integer> a = new CompletableFuture<>();
      CompletableFuture<Integer> b = new CompletableFuture<>();
      CompletableFuture<Integer> c = a.thenCombine(b, (y, z) -> y + z);
      executorService.submit(() -> a.complete(f(x)));
      executorService.submit(() -> b.complete(g(x)));

      System.out.println(c.get());

      executorService.shutdown();
   }
```

`Future` a와 `Future` b의 결과를 알지 못한 상황에서 `thenCombine`은 두 연산이 끝났을 때 스레드 풀에서 실행된 연산을 만든다.

결과를 추가하는 세 번째 연산 c는 다른 두 작업이 끝날 때 까지는 스레드에서 실행되지 않는다.

따라서 기존의 코드에서 발생했던 블록 문제가 일어나지 않는다.

많은 수의 `Future`를 사용해야하는 상황이라면 `CompletableFuture`와 콤비네이터를 이용해 `get()`에서 블록하지 않을 수 있고, 병렬 실행의 효율성은 높이고 데드락은 피할 수 있다.

### 발행 - 구독 그리고 리액티브 프로그래밍

`Future` 와 `CompletableFuture` 은 독립적 실행과 병렬성이라는 정식적 모델에 기반한다.

`Future`는 **한 번**만 실행해 결과를 제공한다.

리액티브 프로그래밍은 시간이 흐르면서 여러 `Future`같은 객체를 통해 여러 결과를 제공한다.

리액티브 프로그래밍은 어떠한 값에 **반응**하는 부분이 존재하기 때문에 리액티브라 부른다.

자바 플로 API는 다음 세 가지로 정리된다.

- 구독자가 구독할 수 있는 발행자
- 이 연결을 구독(subscription)이라 한다.
- 이 연결을 이용해 메시지(또는 이벤트)를 전송한다.

엑셀의 두 셀을 더하는 수식을 포함하는 셀을 생각해본다.

`=C1+C2` 공식을 사용해 `C3`을 만들면 `C1`과 `C2` 의 값이 갱신되면 `C3`에도 새로운 값이 반영된다.

이를 `Publisher(셀의 이벤트에 구독할 수 있음)`와 `Subscribe(다른 셀의 이벤트에 반응함)`을 사용해 코드로 표현하면 다음과 같다.

```java
public class ModernJavaChapter15_1 {
   public static void main(String[] args) {
      SimpleCell c2 = new SimpleCell("c2");
      SimpleCell c1 = new SimpleCell("c1");
      SimpleCell c3 = new SimpleCell("c3");

      c1.subscribe(c3);

      c1.onNext(10);
      c2.onNext(20);
      c1.onNext(50);

      System.out.println(c3.value);
   }

   public static class SimpleCell implements Publisher<Integer>, Subscriber<Integer> {
      private int value = 0;
      private String name;
      private List<Subscriber> subscribers = new ArrayList<>();

      public SimpleCell(String name) {
         this.name = name;
      }

      @Override
      public void subscribe(Subscriber<? super Integer> subscriber) {
         subscribers.add(subscriber);
      }

      private void notifyAllSubscribers() {
         subscribers.forEach(subscriber -> subscriber.onNext(this.value));
      }

      @Override
      public void onNext(Integer integer) {
         this.value = integer;
         System.out.println(this.name + " : " + this.value);
         notifyAllSubscribers();
      }
   }

   public interface Publisher<T> {
      void subscribe(Subscriber<? super T> subscriber);
   }

   public interface Subscriber<T> {
      void onNext(T t);
   }

}
```

`C3`은 `C1`과 `C2`를 **구독**하기 때문에 값이 바뀔 때마다 반응한다.

`C3=C1+C2`를 구현하면 다음과 같다.

```java
public class ModernJavaChapter15_2 {
   public static void main(String[] args) {
      SimpleCell c2 = new SimpleCell("c2");
      SimpleCell c1 = new SimpleCell("c1");
      ArithmeticCell c3 = new ArithmeticCell("c3");

      c1.subscribe(c3::setLeft);
      c2.subscribe(c3::setRight);

      c1.onNext(10);
      c2.onNext(20);
      c1.onNext(50);

   }

   public static class ArithmeticCell extends SimpleCell {
      private int left;
      private int right;

      public ArithmeticCell(String name) {
         super(name);
      }

      public void setLeft(int left) {
         this.left = left;
         onNext(left + this.right);
      }

      public void setRight(int right) {
         this.right = right;
         onNext(right + this.left);
      }
   }

   public static class SimpleCell implements Publisher<Integer>, Subscriber<Integer> {
      private int value = 0;
      private String name;
      private List<Subscriber> subscribers = new ArrayList<>();

      public SimpleCell(String name) {
         this.name = name;
      }

      @Override
      public void subscribe(Subscriber<? super Integer> subscriber) {
         subscribers.add(subscriber);
      }

      private void notifyAllSubscribers() {
         subscribers.forEach(subscriber -> subscriber.onNext(this.value));
      }

      @Override
      public void onNext(Integer integer) {
         this.value = integer;
         System.out.println(this.name + " : " + this.value);
         notifyAllSubscribers();
      }
   }

   public interface Publisher<T> {
      void subscribe(Subscriber<? super T> subscriber);
   }

   public interface Subscriber<T> {
      void onNext(T t);
   }

}
```

데이터바 발행자(생상자)에서 구독자(소비자)로 흐르는 것에 착안해 개발자는 이를 **업스트림**과 **다운스트림**으로 부른다.

`onNext`외에도 `onError`나 `onComplete` 같은 메서드를 사용해 데이터 흐름에서 예외가 발생하거나 데이터 흐름이 종료되었음을 알 수 있다.

### 압력

매 초마다 수천 개의 메시지가 `onNext`로 전달되는 상황을 **압력**이라 부른다.

자바 9의 플로 API는 발행자가 무한의 속도로 아이템을 방출하는 대신 요청했을 때만 다음 아이템을 보내도록 `request()` 메서드를 제공한다.

자바 9 플로 API의 `Subscriber`인터페이스는 네 번째 메서드를 포함한다.

```java
void onSubscribe(Subscription subscription);
```

`Publisher`와 `Subscriber` 사이에 채널이 연결되면 첫 이벤트로 이 메서드가 호출된다.

```java
interface Subscription {
   void cancel();
   void request(long n);
}
```

`Publisher`는 `Subscription` 객체를 만들어 `Subscriber`로 전달하면 `Subscriber`는 이를 이용해 `Publisher`로 정보를 보낼 수 있다.

한번에 한 개의 이벤트를 처리하도록 발행-구독 연결을 구성하려면 다음 작업이 필요하다.

- Subscriber가 OnSubscribe로 전달된 Subscription 객체를 subscription 같은 필드에 로컬로 저장한다.
- Subscriber가 수많은 이벤트를 받지 않도록 onSubscribe, onNext, onError의 마지막 동작에 channel.request(1)을 추가해 오직 한 이벤트만 요청한다.
- 요청을 보낸 채널에만 onNext, onError 이벤트를 보내도록 Publisher의 notifyAllSubscribers 코드를 바꾼다.

구현은 간단해 보이지만 역압력을 구현하려면 여러가지 장단점을 고려해야한다.

- 여러 Subscriber가 있을 때 이벤트를 가장 느린 속도로 보낼 것인가? 아니면 각 Subscriber에게 보내지 않은 데이터를 저장할 별도의 큐를 가질 것인가?
- 큐가 너무 커지면 어떻게 해야 할까?
- Subscriber가 준비가 안 되었다면 큐의 데이터를 폐기할 것인가?

이는 데이터를 성격에 따라 달라진다. 하나의 온도 데이터를 잃어버리는 것은 큰 문제가 아니지만 은행 계좌에서 크레딧이 사라지는 것은 큰일이다.

당김 기반 리액티브 역압력이라는 개념은 Subscriber가 Publisher로부터 요청을 당긴다는 의미애서 **리액티브 당김 기반**이라 불린다.

### 리액티브 시스템 VS 리액티브 프로그래밍

리액티브 시스템이 가져야 할 공식적인 속성은 다음과 같이 요약할 수 있다.

- 반응성
  - 큰 작업을 처리하느라 간단한 질의의 응답을 지연하지 않고 실시간으로 입력에 반응하는 것
- 회복성
  - 한 컴포넌트의 실패로 전체 시스템이 실패하지 않음
- 탄력성
  - 자신의 작업 부하에 맞게 적응하며 작업을 효율적으로 처리함

이렇한 시스템은 리액티브 프로그래밍을 이용해 구현할 수 있다.
