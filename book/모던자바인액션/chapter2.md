## 동작 파라미터화 코드 전달하기

---

### 동작 파라미터

**동작 파라미터화**란 아직은 어떻게 실행할 것인지 결정하지 않은 코드 블럭을 의미한다.

**동작 파라미터화** 를 이용하면 자주 바뀌는 요구사항에 효과적으로 대응할 수 있다.

### 변화하는 요구사항에 대응하기

녹색 사과를 필터링하는 코드를 작성한다면 다음과 같다.

```java
public enum Color {
   RED, GREEN
}
```

```java
	@Getter
	@AllArgsConstructor
	private static class Apple {
		private final Color color;
		private final int weight;
	}
```

```java
	public static List<Apple> filterGreenApples(List<Apple> inventory) {
		List<Apple> result = new ArrayList<>();
		for (Apple apple : inventory) {
			if (Color.GREEN.equals(apple.getColor())) {
				result.add(apple);
			}
		}
		return result;
	}
```

고객의 요구사항이 **빨간색 사과를 필터링하도록** 변경된다면 다음과 같이 작성할 수 있다.

```java
	public static List<Apple> filterColorApples(List<Apple> inventory, Color color) {
		List<Apple> result = new ArrayList<>();
		for (Apple apple : inventory) {
			if (color.equals(apple.getColor())) {
				result.add(apple);
			}
		}
		return result;
	}
```

메서드의 파라미터로 `Color` 를 전달해 변화하는 요구사항에 대해 유연하게 대처하도록 코드를 변경했다.

하지만 **무게에 대해서도 필터링을 진행** 하라는 요구사항이 온다면 새로운 메서드를 만들어야한다.

```java
	public static List<Apple> filterApplesByWeight(List<Apple> inventory, int weight) {
		List<Apple> result = new ArrayList<>();
		for (Apple apple : inventory) {
			if (apple.getWeight() > weight) {
				result.add(apple);
			}
		}
		return result;
	}
```

색과 무게를 필터링하는 메서드를 합치는 방법도 있지만

**코드가 장황해지고 요구사항의 변경에 유연하게 대처할 수 없게된다.**

이때 사용하는 필터링하는 방법에 대해 **동작 파라미터화** 를 이용해 **유연성** 을 얻을 수 있다.

### 동작 파라미터화

참 또는 거짓을 반환하는 함수는 **프레디케이트** 라고한다.

선택 조건을 정의하는 프레디케이트 인터페이스를 만들면 다음곽 같다.

```java
	public interface ApplePredicate {
		boolean test(Apple apple);
	}
```

위의 인터페이스를 구현해 다양한 조건을 정의할 수 있다.

```java
	public class AppleGreenColorPredicate implements ApplePredicate {

		@Override
		public boolean test(Apple apple) {
			return Color.GREEN.equals(apple.getColor());
		}
	}
```

이렇게 각 알고리즘을 캡슐화하는 알고리즘 패밀리를 정의해둔 다음 런타임에 알고리즘을 선택하는 기법을 **전략 패턴** 이라고 한다.

`ApplePredicate` 가 알고리즘 패밀리이고 `AppleGreenColorPredicate` 가 전략이다.

```java
	public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate applePredicate) {
		List<Apple> result = new ArrayList<>();
		for (Apple apple : inventory) {
			if (applePredicate.test(apple)) {
				result.add(apple);
			}
		}
		return result;
	}
```

이렇게 `ApplePredicate` 를 파라미터로 받아 다양한 동작을 수행할 수 있도록 한다.

`ApplePredicate` 하는 **동작** 을 파라미터로 받는 것이다.

우리가 전달한 `ApplePredicate` 의 구현체에 의해 필터의 동작이 결정되기 때문에 유연한 코드를 작성할 수 있다.

이렇게 했을 때 컬렉션 탐색 로직과 각 항목에 적용할 동작을 **분리** 할 수 있다는게 **동작 파라미터화의 강점** 이다.

### 복잡한 과정 간소화

위의 방식에도 문제점이 있는데 **새로운 필터링이 추가될 때마다 인터페이스르 구현** 해야 한다는 점이다.

이떄 **익명 클래스** 를 사용하면 코드의 양을 줄일 수 있다.

```java
List<Apple> redApples = filterApples(inventory, new ApplePredicate() {
	@Override
	public boolean test(Apple apple) {
		return Color.RED.equals(apple.getColor());
	}
});
```

그러나 익명 클래스 역시 많은 공간을 차지하고 많은 프로그래머들이 익명 클래스 사용에 익숙하지 않다는 단점이 있다.

### 람다 표현식 사용

이러한 문제점들은 자바8의 람다 표현식을 사용하면 해결할 수 있다.

```java
List<Apple> redApples = filterApples(inventory, (Apple apple) -> Color.RED.equals(apple.getColor()));
```

### 리스트 형식으로 추상화

```java
   public interface Predicate<T> {
      boolean test(T t);
   }

   public static <T> List<T> filterApples(List<T> list, Predicate<T> predicate) {
      List<T> result = new ArrayList<>();
      for (T t : list) {
         if (predicate.test(t)) {
            result.add(t);
         }
      }
      return result;
   }
```

더 다양한 클래스에서 필터를 사용하기 위해 위처럼 추상화를 진행할 수 있다.

```java
      List<Apple> redApples = filter(inventory, (Apple apple) -> Color.RED.equals(apple.getColor()));


     List<Integer> filterdInteger = filter(integers, (Integer i) -> i % 2 == 0);
```

이렇게 자바8을 사용하면 유연함과 간결함이라는 두 마리 토끼를 잡을 수 있다.
