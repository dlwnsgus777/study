## 7 \_ 2

---

일반적으로 애플리케이션에서는 둘 이상의 `forkJoinPool`을 사용하지 않는다.

필요한 곳에서 언제든 가져다 쓸 수 있도록 `ForkJoinPool` 을 한 번만 인스턴스화해서 정적 필드에 싱글턴으로 저장한다.

스레드 수는 실제 프로세서 외에 하이퍼스레딩과 관련된 가상 프로세서도 개수에 포함한다.

### 포크 / 조인 프레임워크를 제대로 사용하는 방법

- `join` 메서드를 태스크에 호출하면 태스크가 생상하는 결과가 준비될 때까지 호출자를 블록시킨다.
  - 때문에 두 서브 태스크가 모두 시작된 다음에 `join`을 호출해야한다.
- `RecusiveTask` 내에서는 포크조인 풀의 `invoke` 메서드를 사용하지 말아야한다.
- 서브태스크에 `fork` 메서드를 호출해서 `ForkJoinPool`의 일정을 조절할 수 있다.
- 포크/조인 프레임워크를 이용하는 병렬 계산은 디버깅하기 어렵다.
- 멀티코에어 포크/조인 프레임워크를 사용하는 것이 순차 처리보다 무조건 빠를 것이라는 생각은 버려야한다.

### 작업 훔치기

포크/조인 프레임워크에서는 **작업 훔치기** 라는 기법을 사용해 모든 스레드를 거의 공정하게 분할한다.

각각의 스레드는 자신에게 할당된 태스크를 포함하는 **이중 연결 리스트**를 참조하면서 작업이 끝날 때 마다 큐의 헤드에서 다른 태스크를 가져와서 작업을 처리한다.

즉, 할일이 없어진 스레드는 유휴 상태로 빠지는게 아니라 다른 스레드 큐의 꼬리에서 작업을 훔쳐온다.

주어진 태스크를 순차 실행할 단계가 될 때까지 이 과정을 **재귀적으로 반복**한다.

### Spliterator 인터페이스

자바 8은 `Spliterator`라는 새로운 인터페이스를 제공한다.

`Spliterator` 는 분할할 수 있는 반복자라는 의미이다.

`Iterator`처럼 소스의 요소 탐색 기능을 제공하지만 병렬 작업에 특화되어 있다는 점이 다르다.

자바 8은 컬렉션 프레임워크에 포함된 모든 자료구조에 사용할 수 있도록 디폴트 `Spliterator` 구현을 제공한다.

```java
public interface Spliterator<T> {
      boolean tryAdvance(Consumer<? super T> action);
      Spliterator<T> trySplit();
      long estimateSize();
      int characteristics();
}
```

- `tryAdvance`: `Spliterator`의 요소를 하나씩 순차적으로 소비하면서 탐색해야 할 요소가 남아있으면 true를 반환한다.
- `trySplit`: `Spliterator`의 일부 요소를 분할해서 두 번째 `Spliterator`를 생성한다.
- `estimateSize`: 탐색해야할 요소 수 정보를 제공한다.
  - 탐색 해야할 요소 수가 정확하지 않아도 제공된 값을 이용해 쉽고 공평하게 분할할 수 있다.
- `characteristics`: 특성 집합을 포함하는 int를 반환한다.

### 분할 과정

분할 과정은 재귀적으로 일어난다.

`trySplit`을 호출하고, 나눠진 `Spliterator`에서 다시 `trySplit`를 호출하는데 결과가 `null`이 나올때 까지 반복한다.

모든 `Spliterator`에 호출한 `trySplit`의 결과가 `null`이면 재귀 분할 과정이 종료된다.

이 분할과정은 `characteristics` 메서드로 정의하는 특성에 영향을 받는다.

### 특성

`Spliterator`를 이용하는 프로그램은 `characteristics`에서 반환하는 특성을 참고해 `Spliterator`를 더 잘 제어하고 최적화할 수 있다.

- `ORDERED`: 요소에 순서가 있으므로 탐색하고 분할할 때 순서에 유의해야한다.
- `DISTINCT`: x, y 두 요소를 방문했을 때` x.equals(y) == false` 를 반환한다.
- `SORTED`: 탐색된 요소는 미리 정의된 정렬 순서를 따른다.
- `SIZED`: 크기가 알려진 소스로 생성했으므로 estimateSize는 정확한 값을 반환한다.
- `NON-NULL`: 탐색하는 모든 요소는 `null`이 아니다.
- `IMMUTABLE`: 해당 `Spliterator`는 불변이다.
  - 탐색하는 동안 요소를 추가,삭제,수정을 할 수 없다.
- `CONCURRENT`: 동기화 없이 소스를 여러스레드에서 동시에 고칠 수 있다.
- `SUBSIZED`: 분할되는 모든 `Spliterator`는 `SIZED` 특성을 갖는다.
