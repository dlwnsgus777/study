## null 대신 Optional 클래스

---

개발을 하면서 한번씩은 `NullPointerException`을 겪게된다.

`null`은 참조및 예외로 값이 없는 것을 의미한다.

이러한 `null`때문에 발생하는 문제는 다음과 같다.

- 에러의 근원이다.
- 코드를 어지럽힌다.
- 아무 의미가 없다.
  - `null`은 아무 의미도 표현하지 않기 때문에 값이 없음을 표현하기에 적적하지 않다.
- 자바 철학에 위배된다.
  - 자바는 개발자로부터 모든 포인터를 숨겼는데 `null`포인터는 예외이다.
- 형식 시스템에 구멍을 만든다.
  - `null`이 모든 참조 형식에 할당할 수 있기 때문에 처음에 어떤 의미로 `null`이 사용되었는지 알 수 없다.

그루비 같은 언어는 안전 내비게이션 연선자를 도입해 `null`문제를 해결했다.

```javascript
carInsuranceName = person?.car?.insurance?.name;
```

하스켈 같은 함수형 언어에서는 선택형값을 저장할 수 있는 `Maybe`라는 형식을 제공한다.

`Maybe`는 주어진 형식의 값을 갖거나 아무 값도 갖지 않을 수 있다.

자바에서는 `Maybe`와 같은 **선택형값** 개념을 도입했다.

### Optional 클래스

`Optional`클래스는 값을 감싸는 클래스이다.

값이 있으면 해당 값을 감싸는 `Optional` 객체를 반환하고

없으면 빈 `Optional`객체를 반환한다.

`Optional`클래스를 사용하면 모델의 의미가 더 명확해지는 효과가 있다.

```java
private Optional<Car> car; // 자동차가 있을수도 없을수도 있다.
```

### 객체 만들기

- 빈 `Optional`

  - `Optional<Car> optCar = Optional.empty();`
  - `Optional.empty()`를 사용해 빈 Optional을 만든다.

- `null`이 아닌 값으로 Optional 만들기

  - `Optional<Car> optCar = Optional.of(car);`
  - `Optional.of()`를 사용해 Optional을 만든다.
  - 파라미터가 `null`이라면 즉시 `npe`를 발생한다.

- `null`값으로 Optional 만들기
  - `Optional<Car> optCar = Optional.ofNullable(car);`
  - `Optional.ofNullable()`를 사용해 `null`을 저장할 수 있는 Optional을 만든다.
  - 파라미터가 `null`이라면 빈 `Optional`이 반환된다.

### Map 으로 값 추출하고 변환하기.

```java
Optional<Insurance> optInsurance = Optional.ofNullalble(insurance);
Optional<String> name = optInsurance.map(Insurance::getName);
```

스트림의 `map`과 개념적으로 비슷하게 `Optional`객체의 요소의 값을 바꿀 수 있다.

이떄 `Optional`이 비어있으면 **아무 일도 일어나지 않는다.**

`Optional`의 `map`의 결과는 `Optional`이기 떄문에 `map`을 중첩해서 사용할 수 없다.

스트림의 `flatMap`과 마찬가지로 `Optional`의 `flatMap`을 사용해 이차원의 `Optional`을 일차원으로 평준화 할 수 있다.

### Optional 스트림 조작

자바9에서는 `Optional`을 포함하는 스트림을 쉽게 처리할 수 있도록 `Optional`에 `stream` 메서드를 추가했다.

`Optional` 이 비어있는지 아닌지에 따라 0개 이상의 항목을 포함하는 스트림으로 변환한다.

`Optional`의 `stream`은 최대 한 개의 요소를 포함하는 스트림과 같다.

스트림의 `filter`처럼 `Optional`의 `filter`를 이용해 값을 필터링할 수 있다.

이때 값이 존재하지 않으면 빈 `Optional`을 반환한다.

### 디폴트 액션과 언랩

- `get()` : `Optional`에 래핑된 값이 있으면 해당 값을 반환하고 값이 없으면 npe를 발생한다.

  - 결국 null을 사용하는 코드 형식과 비슷해지기 때문에 권장하는 방법은 아니다.

- `orElse(T other)`: `Optional`에 값이 포함되어 있지 않을 때 기본값을 제공한다.
- `orElseGet(Supplier<? extends T> other)`: `orElse`의 게으른 버전이다.
  - 값이 없을 때만 파라미터로 전달된 `Supplier`가 실행된다.
  - 기본 값을 만드는데 시간이 오래 걸리거나 `Optional`이 비어있을 때만 기본값을 생성하고 싶다면 사용한다.
- `orElseThrow(Supplier<? extends X> exceptionSupplier)`: 값이 비어있을 때 넘겨준 예외를 발생시킨다.
  - `get`과는 다르게 발생시킬 예외를 선택할 수 있다.
- `ifPresent(Cunsumer<? super T> consumer)`: 값이 존재할 때 인수로 넘겨준 동작을 실행하고 값이 없으면 아무 일도 일어나지 않는다.
- `ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction)` : 자바9에 추가된 메서드이다.
  - 값이 없을 때 실행할 수 있는 `Runnable`을 인수로 받는다.
