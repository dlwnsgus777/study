## 리팩터링, 테스팅, 디버깅

---

### 코드 가독성 개선

코드 가독성이 좋다는 것은 **어떤 코드를 다른 사람도 쉽게 이해할 수 있음**을 의미한다.

자바 8의 새 기능을 통해 코드의 가독성을 높을 수 있다.

### 익명 클래스를 람다 표현식으로 리팩터링하기

**하나의 추상 메서드를 구현하는 익명 클래스**는 람다 표현식으로 리팩터링할 수 있다.

```java
Runnable r1 = new Runnable() {
  public void run() {
    System.out.println("Hello");
  }
};
```

위의 코드를 다음과 같이 리팩터링 할 수 있다.

```java
Runnable r1 = () -> System.out.println("Hello");
```

주의할 점은

- 익명 클래스에서 사용한 `this`와 `super`는 람다의 `this`와 `super`와 다르다.
  - 익명클래스에서는 익명 클래스 자신을 가리킨다.
  - 람다에서는 람다를 감싸는 클래스를 가리킨다.
- 익명 클래스는 감싸고 있는 클래스의 변수를 가릴 수 있다(Shadow Variable)
  - 람다 표현식에서는 변수를 가릴 수 없다.

```java
      int a = 10;
      Runnable r1 = () -> {
         int a = 2; // 컴파일 에러 발생
         System.out.println(a);
      };

      Runnable r2 = new Runnable() {
         @Override
         public void run() {
            int a = 2;
            System.out.println(a);
         }
      };
```

익명 클래스는 인스턴스화할 때 명시적으로 형식이 정해지는 반면 람다의 형식은 콘텍스트에 따라 달라지게 된다.

이로인해 모호함이 초래될 수 있는데 **명시적 형변환**을 이용해서 이를 해결할 수 있다.

```java
doSomething((Task)() -> System.out.println("some thing"));
```

### 람다 표현식을 메서드 참조로 리팩터링하기

람다 표현식 대신 메서드 참조를 이용해 가독성을 더욱 높일 수 있다.

또한 `comparing`, `maxBy` 같은 정적 헬퍼 메서드를 활용하면 코드 자체로 문제를 더 명확하게 설명할 수 있다.

### 명령형 데이터 처리를 스트림으로 리팩터링하기

이론적으로는 기존의 반복자를 사용하여 컬렉션을 처리하는 코드들을 스트림 API로 바꾸어야한다.

스트림 API를 사용하면 데이터 처리 파이프라인의 의도를 더 명확하게 보여주고 쇼트서킷과 게으름이라는 최적화와 멀티코어 아키텍처를 활용할 수 있다.

### 코드 유연성 개선

람다 표현식을 활용해 동작 파라미터화를 구현하여 유연성있는 코드를 구성할 수 있다.

람다 표현식을 사용하려면 **함수형 인터페이스**가 필요하다.

### 조건부 연기 실행

```java
        if(logger.isLoggable(Log.FINER)) {
            logger.finer("log");
        }
```

위의 코드는 두가지 문제점이 있따.

- `logger`의 상태를 클라이언트에 노출하는 점
- 메시지를 로깅할 때마다 객체의 상태를 매번 확인한다.

위의 코드는 `Supplier` 함수형 인터페이스를 사용해 다음과 같이 리팩터링할 수 있다.

```java
   public void log(Level level, Supplier<String> msgSupplier) {
       if (logger.isLoggable(level)) {
           log(level, msgSupplier.get());
       }
   }
```

해당 메서드를 호출시

```java
logger.log(Level.FINER, () -> "log");
```

위와 같이 호출하여 사용한다.

### 실행 어라운드

매번 같은 준비, 종료 과정을 반복적으로 수행하는 코드가 있다면 이를 람다로 표현할 수 있다.

```java
   public static void main(String[] args) {
       String oneLine = processFile(b -> b.readLine());
       String twoLine = processFile(b -> b.readLine( + b.readLine()));
   }

   
   public static String processFile(BufferedReaderProcessor p) throws IOException {
       try(BufferedReader br = new BufferedReader(new FileReader("file.txt"))) {
           return p.process(br);
       }
   }
   
   public interface BufferedReaderProcessor {
       String process(BufferedReader b) throws IOException;
   }
```

### 람다 테스팅

람다는 익명이므로 테스트 코드 이름을 호출할 수 없다.

따라서 필요하다면 람다를 필드에 저장해 재사용하며 람다의 로직을 테스트할 수 있다.

람다 표현식은 함수형 인터페이스의 인스턴스를 생성한다.

람다의 목표는 **정해진 동작을 다른 메서드에서 사용할 수 있도록** 하나의 조각으로 캡슐화하는 것이다.

이를위해 세부 구현을 포함하는 람다 표현식은 공개하지 말아야 한다.

때문에 람다를 사용하는 메서드의 동작을 테스트하는 것에 집중해야한다.

### 디버깅

예외가 발생했을 때 어디에서 발생했는지에 대한 정보는 **스택 프레임**에서 얻을 수 있다.

하지만 람다의 표현식은 이름이 없기 때문에 복잡한 스택 트레이스가 생성된다.

메서드 참조에서도 마찬가지로 스택 트레이스에 메서드 명이 나타나지 않는다.

이러한 부분은 자바 컴파일러가 개선해야할 부분이다.

### 스트림 디버깅

스트림의 파이프라인 연산을 디버깅하기 위해서 `peek`메서드를 사용한다.

`peek`메서드는 스트림의 각 요소를 소비한 것처럼 동작을 실행한다.

파이프라인의 각 동작 전후의 중간값을 출력하는데 활용할 수 있다.

```java
numbers.stream()
  .peek(x -> System.out.println(x))
  .map(x -> x + 17)
  .peek(x -> System.out.println(x))
  .collect(toList());
```


