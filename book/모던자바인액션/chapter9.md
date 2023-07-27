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
