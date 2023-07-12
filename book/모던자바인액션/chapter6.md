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

- 첫 번째 인수: 리듀싱 연산의 시작값, 스트림에 인수가 없을 때는 반환값
- 두 번째 인수: 변환 함수
- 세 번째 인수: 같은 종류의 두 항목을 하나의 값으로 더하는 `BinaryOperator`

```java
Optional<Dish> mostCalorieDish = menu.stream().collect(Collectors.reducing((d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2));
```

한 개의 인수를 갖는 `reducing` 팩토리 메서드도 있다.

이때는 시작 요소로 첫 번째 스트림, 두 번째 인수로 자신을 그대로 반환하는 **항등 함수**를 받는다.

만약 빈 스트림이 넘겨졌을 때 시작값이 설정되지 않는 상황이 벌어지므로 `Optional`을 반환한다.

### collect와 reduce

`collect`는 도출하려는 결과를 **누적하는 컨테이너로 바꾸도록 설계**된 메서드인 반면 `reduce`는 **두 값을 하나로 도출하는 불면형 연산** 이라는 점에서 차이가 있다.

따라서 가변 컨테이너 관련 작업이면서 병렬성을 확보하기 위해서는 `collect` 메서드로 리듀싱 연산을 구현해야한다.

---

### 같은 연산도 다양한 방식으로 수행

칼로리 합계를 구하는 예제에서 람다 표현식 대신 `Integer` 클래스의 `sum` 메서드를 참조하면 코드를 좀 더 단순화 할 수 있다.

```java
int totalCalories = menu.stream().collect(Collectors.reducing(
            0,                 // 초기값
            Dish::getCalories, // 변환 함수
            Integer::sum       // 합계 함수
      ));
```

### 자신의 상황에 맞는 최적의 해법 선택

함수형 프로그래밍에서는 하나의 연산을 다양한 방법으로 해결할 수 있다.

또한 컬렉터를 이용하면 스트림 인터페이스에서 제공하는 메서드를 이용하는 것보다 코드가 더 복잡한 대신에 **재사용성과 커스터마이즈 가능성** 을 제공하는 높은 수준의 추상화와 일반화를 얻을 수 있다.

### 그룹화

명령형으로 그룹화를 구현하라면 까다롭고 해야할 일이 많지만 함수형을 이용하면 가독성 있게 처리할 수 있다.

``Collectors.groupingBy` 를 이용한다.

```java
Map<Dish.Type, List<Dish>> dishesByType = menu.stream().collect(Collectors.groupingBy(Dish::getType));
```

위의 코드는 각 요리에서 `Dish.Type` 과 일치하는 모든 요리를 추출하여 `Map` 으로 그룹화한다.

`Dish::getType` 함수를 기준으로 스트립이 그룹화되므로 이를 **분류 함수**라고 한다.

그룹화 연산의 결과로 그룹화 함수가 반환하는 키, 키에 대응하는 스트림의 모든 항목 리스트를 값으로 갖는 `Map` 이 반환된다.

### 그룹화된 요소 조작

요소를 그룹화 한 다음에는 각 결과 그룹의 요소를 조작하는 연산이 필요하다.

그룹화한 요소 중 500 칼로리가 넘는 요리만 필터링 한다고 했을 때 다음과 같이 그룹화 하기 전에 필터링을 적용한 코드 작성할 수 있다.

```java
      Map<Dish.Type, List<Dish>> caloricDishedByType = menu.stream()
            .filter(dish -> dish.getCalories() > 500)
            .collect(Collectors.groupingBy(Dish::getType));
```

하지만 위의 코드에는 문제가 있다.

위의 코드가 동작하면 필터링 조건을 만족하지 못하는 `Dish.Type` 은 결과 맵에서 해당 키 자체가 **사라진다.**

이 문제를 해결하기 위해서는 두 번째 인수를 갖도록 `groupingBy` 메서드를 오버로드해야 한다.

```java
Map<Dish.Type, List<Dish>> caloricDishedByType = menu.stream()
            .collect(Collectors.groupingBy(Dish::getType,
                  Collectors.filtering(dish -> dish.getCalories() > 500, Collectors.toList())));
```

`Collectors.filtering` 메서드는 전달받은 프레디케이트를 사용해 각 그룹의 요소와 필터링 된 요소를 재그룹화한다.

그룹화된 요소를 조작하는 다른 방법은 **맵핑 함수**를 사용하는 것이다.

```java
Map<Dish.Type, List<String>> dishNamesByType = menu.stream().collect(
            Collectors.groupingBy(Dish::getType, Collectors.mapping(Dish::getName, Collectors.toList()))
      );
```

위의 코드는 그룹의 각 요라를 관련 이름 목록으로 변환하는 코드이다.

마찬가지로 `flatmap` 변환도 수행할 수 있다.

### 다수준 그룹화

두 인수를 받는 팩토리 메서드 `Collectors.groupingBy` 를 이용하면 항목을 다수준으로 그룹화할 수 있다.

```java
Map<Dish.Type, Map<CaloricLevel, List<Dish>>> dishesByTypeCaloricLevel = menu.stream().collect(
            Collectors.groupingBy(
                  Dish::getType,
                  Collectors.groupingBy(dish -> {
                     if (dish.getCalories() <= 400) {
                        return CaloricLevel.DIET;
                     } else if (dish.getCalories() <= 700) {
                        return CaloricLevel.NORMAL;
                     } else {
                        return CaloricLevel.FAT;
                     }
                  })
            )
      );
```

위의 코드에서 바깥쪽 `groupingBy`에 항목을 분류할 **두 번째 기준**을 정의하는 `groupingBy`를 전달해 두 수준으로 스트림의 항목을 그룹화할 수 있다.

### 서브그룹으로 데이터 수집

다수준 그룹화를 위해 바깥쪽 `groupingBy`에 전달하는 컬렉터의 형식에는 제한이 없다.

```java
Map<Dish.Type, Long> typesCount = menu.stream().collect(
            Collectors.groupingBy(Dish::getType, Collectors.counting())
      );
```

위의 코드처럼 두 번째 인수로 `counting` 컬렉터를 전달해 메뉴에서 요리의 수를 종류별로 계산할 수 있다.

분류 함수 한개의 인수를 갖는 `groupingBy` 는 `groupingBy(f, toList())`의 축약형이다.

### 컬렉터 결과를 다른 형식에 적용하기

그룹화의 결과로 반환되는 `Optional`을 삭제할 수 있다.

`Collectors.collectingAndThen`을 사용하면 컬렉터가 반환한 결과를 다른 형식으로 활용할 수 있다.

```java
Map<Dish.Type, Dish> mostCaloricByType = menu.stream().collect(
            Collectors.groupingBy(
                  Dish::getType,
                  Collectors.collectingAndThen(
                        Collectors.maxBy(Comparator.comparingInt(Dish::getCalories)), // 감싸인 컬렉터
                        Optional::get // 변환 함수
                  )
            )
      );

```

`Collectors.collectingAndThen` 메서드는 적용할 컬렉터와 변환 함수를 인수로 받아 다른 컬렉터를 반환한다.

반환되는 컬렉터는 기존 컬렉터의 래퍼 역할을 하며 `collect`의 마지막 과정에서 변환 함수로 자신이 반환하는 값을 매핑한다.

### 분할

분할은 **분할 함수**라 불리는 프레디케이트를 분류 함수로 사용하는 특수한 그룹화 기능이다.

분할 함수는 불리언을 반환하므로 `Map`의 `key`형식은 `Boolean`이다.

따라서 결과로 나오는 그룹화 맵은 최대 두 개의 그룹으로 분류된다.

```java
Map<Boolean, List<Dish>> partitionedMenu = menu.stream().collect(Collectors.partitioningBy(Dish::isVegetarian));
```

위의 코드는 메뉴에서 채식인 메뉴와 채식이 아닌 메뉴를 분류하는 코드이다.

분할 함수가 반환하는 참, 거짓 두 가지 요소의 스트림 리스트를 모두 유지한다는 것이 분할의 장점이다.

```java
Map<Boolean, Map<Dish.Type, List<Dish>>> vegetarianDishedByType = menu.stream().collect(
            Collectors.partitioningBy(Dish::isVegetarian,
                  Collectors.groupingBy(Dish::getType))
      );
```

위의 코드처럼 컬렉터를 두 번쨰 인수로 전달할 수 있는 오버로드된 버전의 `partitioningBy` 메서드도 있다.

`groupingBy` 에서 다수준으로 그룹화를 한 것처럼 `partitioningBy`를 사용해 다수준으로 분할하는 기법도 존재한다.

```java
menu.stream().collect(Collectors.partitioningBy(Dish::isVegetarian,
      Collectors.partitioningBy(d -> d.getCalories() > 500)));
```

---

### Collector 인터페이스

`Collector` 인터페이스는 리듀싱 연산(컬렉터)을 어떻게 구현할지 제공하는 메서드 집합으로 구성된다.

`Collector` 인터페이스를 구현하는 리듀싱 연산을 직접 만들 수 있다.

`Collector` 인터페이스의 시그니처는 다음과 같다

```java
public interface Collector<T, A, R> {
    Supplier<A> supplier();
    BiConsumer<A, T> accumulator();
    BinaryOperator<A> combiner();
    Function<A, R> finisher();
    Set<Characteristics> characteristics();
}
```

- T: 수집될 스트림 항목의 제네릭 형식
- A: 누적자, 수집 과정에서 중간 결과를 누적하는 객체의 형식
- R: 수집 연산 결과 객체의 형식(주로 컬렉션 형식이다.)

`List` 로 수집하는 `Collector` 클래스를 구현한다면 다음과 같이 선언할 수 있다.

```java
public class ToListCollector<T> implements Collector<T, List<T>, List<T>>
```

보통 누적 과정에서 사용되는 객체가 수집 과정의 최종 결과로 사용된다.

### Collector 인터페이스의 메서드
