## 스트림으로 데터 수집

---

중간 연산은 한 스트림을 다른 스트림으로 변환하는 연산으로써 여러 연산은 연결할 수 있다. 중간 연산은 스트림 파이프라인을 구성하며, 스트림의 요소롤 **소비하지 않는다.**

반면, 최종 연산은 스트림의 요소를 소비해 **최종 결과를 도출한다.**

가령 컬렉션의 요소들을 `Map`으로 그룹화하기 위해서는 간단한 문제라도 복잡한 코드가 작성되기 마련인데 스트림을 사용하면 간단하게 이 문제를 해결할 수 있다.

```java
Map<Currency, List<Transaction>> transactionsByCurrencies = transactions.stream().collect(groupingBy(Transaction::getCurrency));
```

---

### 컬렉터란 무엇인가?

`Collector` 인터페이스의 구현은 스트림의 요소를 어떤 식으로 도출할지 지정한다.

`groupingBy` 는 각 키를 버킷으로 각 버킷에 대응하는 요소 리스트를 값으로 포함하는 `Map`을 만든다.

다수준으로 그룹화를 수행할 때 명령형 프로그래밍에서는 다중 루프와 조건문을 추가하기 때문에 가독성과 유지 보수성이 떨어지지만 함수형 프로그래밍에서는 손쉽게 컬렉터를 추가할 수 있다.

스트림에 `collect`를 호출하면 내부적으로 **리듀싱 연산**이 일어난다.

보통은 컬렉터를 이용해 값을 저장할 자료구조에 값을 **누적한다.**

`Collectors` 유틸리티 클래스는 자주 이용하는 컬렉터 인스턴스를 생성할 수 있는 정적 팩토리 메서드를 제공한다.

예를들어 `toList`는 모든 요소를 리스트로 수집한다.

`Collectors` 에서 제공하는 메서드의 기능은 크게 세 가지로 구분한다.

- 스트림 요소를 하나의 값으로 리듀스하고 요약
- 요소 그룹화
- 요소 분할

```java
long howManyDishes = menu.stream().collect(Collectors.counting());
```

예를들어 `counting` 을 사용해 손쉽게 요소의 갯수를 계산할 수 있다.

이렇게 스트림에 있는 객체의 숫자 필드의 합계나 평균 등을 반환하는 연산에도 리듀싱 기능이 사용된다.

이러한 연산을 **요약** 연산이라고 부른다.

`Collectors.summingInt`라는 특별한 요약 팩터리 메서드가 있는데 인수로 전달된 함수는 객체를 `int`로 매핑한 컬렉터를 반환한다.

그리고 `summingInt` 가 `collect` 메서드로 전달되면 요약 작업을 수행한다.

다음은 메뉴 리스트의 총 칼로리를 계산하는 코드이다.

```java
int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));
```

칼로리로 매핑된 각 요리의 값을 탐색하면서 초깃값으로 설정되어 있는 누적자에 칼로리를 더한다.

이와 같은 방식으로 `summingLong`과 `summingDouble` 메서드도 제공한다.

단순 합계 이외에도 `averagingInt` 와 같은 평균값 계산 등의 연산도 요약 기능으로 제공된다.

종종 두 개 이상의 연산을 한번에 수행해야하는 경우가 있는데 이러한 경우에는 `summarizingInt` 가 반환하는 컬렉터를 사용할 수 있다.

```java
IntSummaryStatistics menuStatistics = menu.stream().collect(summarizingInt(Dish::getCalories));
```

위의 코드의 반환값은 `IntSummaryStatistics` 인데 출력하면 다음과 같은 정보를 확인할 수 있다.

```java
IntSummaryStatistics{count=9, sum=400, min=200, average=477.777778, max=800}
```

마찬가지로 `long`이나 `double`에 대응하는 `summarizingLong`, `summarizingDouble`, `LongSummaryStatistics`, `DoubleSummaryStatistics` 클래스도 있다.

---

### 문자열 연결

컬렉터에 `joining` 팩토리 메서드를 이용하면 스트림의 각 객체에 `toString` 메서드를 호출해 추출한 모든 문자열을 하나의 문자열로 연결해서 반환한다.

```java
String shortMenu = menu.stream().map(Dish::getName).collect(Collectors.joining());
```

`joining` 메서드는 내부적으로 `StringBuilder`를 이용해 문자열을 하나로 만든다.

`Dish` 클래스가 요리명을 반환하는 `toString` 을 포함하고 있다면 `map` 을 사용해 요리명을 추출하는 과정을 생략할 수 있다.

연결된 요소 사이에 구분 문자열을 넣을 수 있는 오버라이드 된 `joining` 팩토리 메서드도 있다.

```java
String shortMenu = menu.stream().map(Dish::getName).collect(Collectors.joining(", "));
```

---

### 범용 리듀싱 요약 연산

지금까지 살펴본 모든 컬렉터는 범용 `Collectors.reducing` 팩토리 메서드로도 정의할 수 있다.

그럼에도 특화된 컬렉터를 사용한 이유는 **프로그래밍적 편의성** 때문이다.

```java
      int totalCalories = menu.stream().collect(Collectors.reducing(
            0, Dish::getCalories, (i, j) -> i + j
      ));
```

위의 코드는 `reducing`을 사용해 메뉴의 모든 칼로리 합계를 계산하는 코드이다.

`reducing` 은 세 개의 인수를 받는다.
