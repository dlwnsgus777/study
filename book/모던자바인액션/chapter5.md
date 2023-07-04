## 스트림 활용

---

5장에서는 스트림 API가 지원하는 다양한 연산을 살펴본다.

---

### 필터링

스트림 인터페이스는 `filter` 메서드를 지원한다.

`filter` 는 **프레디케이트** 를 인수로 받아 일치하는 모든 요소를 포함하는 **스트림**을 반환한다.

```java
List<Dish> vegetarianMenu = menu.stream()
																.filter(Dish::isVegetarian)
																.collect(toList());
```

스트림은 **고유 요소** 로 이루어진 즉, 중복을 제거해주는 `distinct` 메서드도 지원한다.

```java
List<Integer> vegetarianMenu = numbers.stream()
																.filter(i -> i % 2 == 0)
																.distinct()
																.collect(toList());
```

### 스트림 슬라이싱

**자바 9** 에서는 스트림의 요소를 효과적으로 선택할 수 있도록

`takeWhile` , `dropWhile` 이 추가되었다.

`takeWhile` 은 무한 스트림을 포함한 모든 스트림에 **프레디케이트**를 적용해 스트림을 슬라이스할 수 있다.

`dropWhile` 은 프레디케이트가 **처음으로 거짓이 되는 지점** 까지 발견된 요소를 버린다.

스트림은 주어진 값 이하의 크기를 갖는 새로운 **스트림** 을 반환하는 `limit` 메서드를 지원한다.

```java
List<Dish> vegetarianMenu = menu.stream()
																.filter(Dish::isVegetarian)
																.limit(3)
																.collect(toList());
```

위의 코드는 `filter`의 프레디케이트와 일치하는 처음 세가지 요소를 선택한 다음 즉시 결과를 반환한다.

스트림은 처음 n개 요소를 제외한 스트림을 반환하는 `skip` 메서드를 지원한다.

### 매핑

매핑은 특정 객체에서 특정 데이터를 **선택**하는 작업에 사용되는 연산이다.

`map` 메서드는 인수로 제공된 함수를 각 요소에 적용하며 적용한 결과가 새로운 요소로 매핑된다.

이떄 **기존의 값을 고친다는 개념보다는 새로운 버전을 만든다**라는 개념에 가까우므로 **변환에 가까운 매핑** 이라는 단어를 사용한다.

```java
List<String> dishNames = menu.stream()
																.map(Dish::getName)
																.collect(toList());
```

위의 예제는 Dish의 `getName()` 메서드를 `map` 메서드에 전달해 스트림의 요리명을 추출하는 코드이다.

`flatMap` 메서드는 **스트림 평면화** 를 할때 사용하는 메서드이다.

`flatMap` 메서드를 사용하면 생성된 스트림들을 **하나의 스트림으로 평면화** 된 스트림으로 반환한다.

### 검색과 매칭

- `anyMatch`: 주어진 스트림에서 적어도 한 요소와 일치하는지 확인할 때 사용(boolean 반환)
- `allMatch`: 주어진 스트림의 모든 요소가 일치하는지 검사할 때 사용(boolean 반환)
- `noneMatch`: 주어진 스트림에서 프레디케이트와 일치하는 요소가 없는지 검사할 때 사용(boolean 반환)

위 3가지 메서드는 **쇼트 서킷** 기법을 활용한다.

**쇼트 서킷** 이란 모든 스트림의 요소를 처리하지 않고도 결과를 반환하는 것을 말한다.

예를들어 `and` 연산으로 연결된 평가라면 하나라도 거짓이라며 나머지 결과와 상관없이 전체 결과가 거짓이 되는 상항을 말한다.

**쇼트 서킷**은 원하는 요소를 찾았으면 즉시 결과를 반환할 수 있다.

때문에 `limit` 메서드도 **쇼트서킷 연산**이다.

### 요소 검색

`findAny` 메서드는 현재 스트림에서 임의의 요소를 반환한다.

```java
Optional<Dish> dish = menu.stream()
													.filter(Dish::isVegetarian)
													.findAny();
```

`findAny`는 쇼트서킷을 이용해 결과를 찾는 즉시 실행을 종료한다.

`Optional` 은 값이 존재하는지 확인하고 값이 없을 때 어떻게 처리할지 강제하는 기능을 제공한다.

일부 스트림에는 **논리적인 아이템 순서**가 정해져 있을 수 있다.

이런 스트림에서 첫 번째 요소를 찾으려면 다음과 같다.

```java
Optional<Integer> dish = someNumbers.stream()
													.map(n -> n * n)
													,filter(n -> n % 3 == 0)
													.findFirst();
```

`findAny`와 `findFirst` 의 차이는 **병렬성**이다.

병렬 실행에서는 첫 번째 요소를 찾기 어렵기 때문에 요소의 반환 순서가 상관없다면 병렬 스트림에서는 제약이 적은 `findAny`를 사용한다.

### 리듀싱

Integer 같은 결과가 나올 때까지 스트림의 모든 요소를 반복적으로 처리하는 연산을 **리듀싱 연산**이라고 한다.

함수형 프로그래밍에서는 종이를 작은 조각이 될 때까지 반복해서 접는 것과 비슷하다하여 **폴드**라고 부른다.

`reduce` 메서드는 두 개의 파라미터를 받는다.

- 초깃값
- 모든 요소를 조합해 새로운 값을 만드는 `BinaryOperator<T>`

```java
int sum = numbers.stream().reduce(0, (a, b) -> a + b);
```

`reduce`를 사용해 모든 요소를 더하는 메서드이다.

초깃값을 받지 않도록 오버라이드된 `reduce`도 있다.

이 `reduce` 는 `Optional` 을 반환한다.

```java
Optional<Integer> sum = numbers.stream().reduce((a, b) -> a + b);
```

`reduce` 메서드는 내부 반복이 추상화되면서 내부 구현에서 **병렬**로 실행할 수 있게 된다.

다만 병렬로 실행하기 위해서는 몇가지 제약이 따른다.

- `reduce`에 넘겨준 람다(인스턴스 변수)의 상태가 바뀌지 말아야한다.
- 연산이 어떤 순서로 실행되더라도 결과가 바뀌지 않는 구조여야한다.

`reduce`, `sum`, `max` 같은 연산은 결과를 누적할 **내부 상태**가 필요하다.

이 내부 상태는 스트림의 요소 수와 상관없이 **한정**되어 있다.

반면 `sorted`, `distinct` 같은 연산은 스트림 연산의 이력을 가지고 있어야한다.

예를 들어 어떤 요소를 출력 스트림으로 추가하려면 **모든 요소가 버퍼에 추가**되어 있어야한다.

이러한 연산을 수행하는데 필요한 저장소 크기는 정해져있지 않다.

이러한 연산을 **내부 상태를 갖는 연산** 이라 한다.

```java
public class ModernJavaChapter5 {
   public static void main(String[] args) {
      Trader raoul = new Trader("Raoul", "Cambridge");
      Trader mario = new Trader("Mario", "Milan");
      Trader alan = new Trader("Alan", "Cambridge");
      Trader brian = new Trader("Brian", "Cambridge");

      List<Transaction> transactions = Arrays.asList(
            new Transaction(brian, 2011, 300),
            new Transaction(raoul, 2012, 1000),
            new Transaction(raoul, 2011, 400),
            new Transaction(mario, 2012, 710),
            new Transaction(mario, 2012, 700),
            new Transaction(alan, 2012, 950)
      );

      // 1. 2011 년에 일어난 모든 트랜잭션
      List<Transaction> answerOne = transactions.stream()
            .filter(transaction -> transaction.getYear() == 2011)
            .sorted(Comparator.comparing(Transaction::getValue))
            .collect(Collectors.toList());

      // 2. 거래자가 근무하는 모든 도시를 중복 없이 나열
      List<String> answerTwo = transactions.stream()
            .map(transaction -> transaction.getTrader().getCity())
            .distinct()
            .collect(Collectors.toList());

      // 3. 케임브리지에서 근무하는 모든 거래자를 찾아서 이름순으로 정렬
      List<Trader> answerThree = transactions.stream()
            .map(Transaction::getTrader)
            .filter(trader -> trader.getCity().equals("Cambridge"))
            .distinct()
            .sorted(Comparator.comparing(Trader::getName))
            .collect(Collectors.toList());

      // 4. 모든 거래자의 이름을 알파벳순으로 정렬해서 반환
      String answerFour = transactions.stream()
            .map(transaction -> transaction.getTrader().getName())
            .distinct()
            .sorted()
            .reduce("", (n1, n2) -> n1 + n2);

      // 5. 밀라노에 거래자가 있는가?
      boolean answerFive = transactions.stream()
            .anyMatch(transaction -> transaction.getTrader()
                                          .getCity()
                                          .equals("Milan"));

      // 6. 케임브리지에서 거주하는 거래자의 모든 트랜잭션값을 출력
      transactions.stream()
            .filter(transaction -> transaction.getTrader().getCity().equals("Cambridge"))
            .map(Transaction::getValue)
            .forEach(System.out::println);


      // 7. 전체 트랜잭션 중 최댓값
      Optional<Integer> answerSeven = transactions.stream()
            .map(Transaction::getValue)
            .reduce(Integer::max);


      // 8. 전체 트랜잭션 중 최솟값
      Optional<Transaction> answerEight = transactions.stream()
            .reduce((t1, t2) -> t1.getValue() < t2.getValue() ? t1 : t2);
   }

   @Getter
   @AllArgsConstructor
   @ToString
   private static class Trader {
      private final String name;
      private final String city;
   }

   @Getter
   @AllArgsConstructor
   @ToString
   private static class Transaction {
      private final Trader trader;
      private final int year;
      private final int value;
   }
}
```

---

### 숫자형 스트림

```java
int calories = menu.stream()
									.map(Dish::getCalories)
									.reduce(0, Integer::sum);
```

위의 연산에서는 int를 Integer로 변환하는 **박싱 비용**이 발생한다.

스트림의 인터페이스에서는 숫자를 다룰 메서드를 제공하지 않는다.

하지만 숫자 스트림을 효율적으로 처리할 수 있도록 **기본형 특화 스트림**을 제공한다.

자바 8 에서는 3가지 기본형 특화 스트림을 제공한다.

1. IntStream
2. DoubleStream
3. LongStream

각각의 인터페이스는 `sum`, `max` 같이 자주 사용하는 연산 수행 메서드를 제공한다.

특화 스트림은 **오직 박싱 과정에서 일어나는 효율성** 과 관련있으며 스트림에 추가 기능을 제공하지는 않는다.

스트림을 특화 스트림으로 변환할 때는 `mapToInt`, `mapToDouble`, `mapToLong` 세 가지 메서드를 가장 많이 사용한다.

```java
int calories = menu.stream()
									.mapToInt(Dish::getCalories)
									.sum();
```

숫자 스트림을 만들고 다시 일반 스트림으로 복원하기 위해서는 `boxed` 메서드를 사용한다.

```java
Stream<Integer> stream = menu.stream()
									.mapToInt(Dish::getCalories)
									.boxed();
```

숫자 스트림에서 최댓값을 찾을 때는 0이라는 기본값 때문에 잘못된 결과가 도출될 수 있다.

이러한 상황을 방지하고자 `Optional` 을 세 가지 특화 스트림 번전에 맞게 제공한다.

- `OptionalInt`
- `OptionalDouble`
- `OptionalLong`

### 숫자 범위

`IntStream` 과 `LongStream` 에서는 `range`와 `rangeClosed` 두가지 정적 메서드를 제공한다.

두 메서드 모두 **시작값과 종료값**을 파라미터로 받으며

`range` 메서드는 시작값과 종료값이 결과에 포함되지 않고

`rangeClosed` 메서드는 시작값과 종료값이 결과에 포함된다.

```java
IntStream numbers = IntStream.rangeClosed(1, 100)
												.filter(n -> n % 2 == 0);
```

### 스트림 만들기

다양한 방식으로 스트림을 얻는 방법을 소개한다.

### 값으로 스트림 만들기

`Stream.of` 라는 정적 메서드를 사용해 스트림을 만들 수 있다.

```java
Stream<String> stream = Stream.of("Modern", "Java")
							.map(String::toUpperCase);
```

`Stream.empty()` 를 사용해 스트림을 비울 수 있다.

### null이 될 수 있는 객체로 스트림 만들기

null 이 될 수 있는 객체를 스트림으로 만들어야하는 상황이 있을 수 있다.

```java
Stream<String> homeValueStream =
	homeValue == null ? Stream.empty() : Stream.of(value)
```

위의 코드를

```java
Stream<String> homeValueStream = Stream.ofNullable(homeValue)
```

`Stream.ofNullable()` 사용해 줄일 수 있다.

### 배열로 스트림 만들기

배열을 인수로 받는 정적 메서드 `Arrays.stream` 을 이용해 스트림을 만들 수 있다.

```java
int sum = Arrays.stream(numberArray).sum();
```

기본형인 `int[]` 배열을 전달하면 `IntStream` 을 만들 수 있다.

### 파일로 스트림 만들기

`java.nio.file.Files`의 많은 정적 메서드가 **스트림을 반환**한다.

예를들어 `Files.lines` 는 주어진 파일의 행 스트림을 문자열로 반환한다.

`Stream` 인터페이스는 `AutoCloseable` 인터페이스를 구현하기 때문에 `try` 블록 내의 자원은 자동으로 관리된다.
