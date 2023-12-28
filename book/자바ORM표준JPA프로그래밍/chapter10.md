
## JPQL

---

JPQL 의 특징은 다음과 같다.

- 객체지향 쿼리 언어이다. 테이블을 대상으로 쿼리를 하는게 아닌 엔티티 객체를 대상으로 쿼리한다.
- SQL을 추상화해서 특정 데이터 베이스 SQL에 의존하지 않는다.
- 결국 SQL로 변환된다.

### 기본 문법과 쿼리

JPQL도 `select`, `update`, `delete` 문을 사용할 수 있다.

엔티티 저장시에는 `persist()`를 사용하면 되므로 `insert`문은 없다.

### SELECT 문

```jpql
select m from Member as m where m.username = 'Hello'
```

- 엔티티와 속성은 대소문자를 **구분**한다. 
  - select, from, as 같은 키워드는 대소문자를 구분하지 않는다.
- JPQL에서 사용한 `Member`는 클래스 명이 아니라 **엔티티 명**이다.
- JPQL에서 별칭을 필수로 사용한다. 별칭 없이 작성하면 잘못된 문법이라는 오류가 발생한다.

### TypeQuery, Query

작성한 JPQL을 실행하려면 쿼리 객체를 만들어야 한다.

쿼리 객체는 `TypeQuery`와 `Query` 가 있는데 

반환할 타입을 명확히 지정할 수 있으면 `TypeQuery`, 그렇지 않으면 `Query` 객체를 사용하면 된다.

```java
TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class);

List<Member> resultList = query.getResultList();
```

```java
Query query = em.createQuery("select m.name, m.age from Member m");

List resultList = query.getResultList();

for (Object o : resultList) {
    Object[] result = (Object[]) o;
    System.out.println(result[0]);
    System.out.println(result[1]);
}
```

`SELECT`절에서 여러 엔티티나 컬럼을 선택할 때는 `Query` 객체를 사용해야 한다.

타입을 변환할 필요가 없는 `TypeQuery`가 더 편리하다.

### 결과 조회

다음 메서드들을 호출하면 실제 쿼리를 실행해서 데이터베이스를 조회한다.

- `query.getResultList()`: 결과를 반환한다. 결과가 없으면 빈 컬렉션을 반환한다.
- `query.getSingleResult()`: 결과가 정확히 하나일 때 사용한다.
  - 결과가 없으면 예외가 발생한다.
  - 결과가 1개보다 많으면 예외가 발생한다.

### 파라미터 바인딩

JPQL은 이름 기준 파라미터 바인딩도 지원한다.

```java
String nameParam = "name1";
TypedQuery<Member> query = em.createQuery("select m from Member m where m.name = :name", Member.class);
query.setParameter("name", nameParam);

List<Member> resultList = query.getResultList();
```
이름 기준 파라미터는 앞에 `:`를 사용한다.


```java
String nameParam = "name1";
TypedQuery<Member> query = em.createQuery("select m from Member m where m.name = ?1", Member.class);
query.setParameter(1, nameParam);

List<Member> resultList = query.getResultList();
```

위치 기준 파라미터를 사용하려면 `?:` 다음에 위치 값을 주면 된다. 

위치 값은 1부터 시작이다.

직접 문자열을 더해 JPQL을 작성하는 것은 SQL 인젝션 공격을 당할 수 있다.

또한 파라미터 바인딩을 사용하면 SQL로 파싱한 결과를 재사용할 수 있고, 데이터베이스 내부에서도 파싱한 값을 재사용하기 때문에

애플리케이션과 데이터베이스 모두 성능이 향상된다.

때문에 파라미터 바인딩은 **선택이 아닌 필수다**

### 프로젝션

SELECT절에 조회할 대상을 지정하는 것을 **프로젝션**이라 한다.

프로젝션 대상은 엔티티, 엠비디드 타입, 스칼라 타입이 있다.

- 엔티티 프로젝션
  - 조회한 엔티티는 영속성 컨텍스트에서 관리된다.
- 임베디드 타입 프로젝션
  - 임베디드 타입은 조회의 시작점이 될 수 없다는 제약이 있다.
  - 임베디드 타입은 엔티티 타입이 아닌 값 타입이기 때문에 영속성 컨텍스트에서 관리되지 않는다.
- 스칼라 타입 프로젝션
  - 숫자, 문자, 날짜와 같은 기본 데이터 타입을 스칼라 타입이라 한다.

### 여러 값 조회

꼭 필요한 데이터들만 선택해서 조회해야할 때도 있는데 프로젝션에 여러 값을 선택하면

`TypeQuery`를 사용할 수 없고 `Query`를 사용해야 한다.

스칼라 타입뿐만 아니라 엔티티 타입도 여러 값을 함께 조회할 수 있다.

이때 조회한 엔티티는 영속성 컨텍스트에서 관리한다.

### NEW 명령어

실제 여러 값을 조회할 때 `Object[]`를 사용하는 것보다 `DTO`를 만들어 의미 있는 객체로 변환해서 사용한다.

```java
public class UserDto {
    private String name;
    private int age;

    public UserDto() {
    }

    public UserDto(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

```java
TypedQuery<UserDto> query = em.createQuery("select new learn.jpa.model.UserDto(m.name, m.age) from Member m", UserDto.class);

List<UserDto> resultList = query.getResultList();
```

`NEW`명령어를 사용한 클래스로 `TypedQuery`를 사용할 수 있기 때문에 객체 변환 작업을 줄일 수 있다.





