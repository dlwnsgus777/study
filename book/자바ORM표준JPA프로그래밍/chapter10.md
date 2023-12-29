
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

`NEW` 명령어를 사용할 때는 다음 2가지를 주의해야 한다.

- 패키지 명을 초함한 전체 클래스 명을 입력해야 한다.
- 순서와 타입이 일치하는 생성자가 필요하다.

### 페이징 API

JPA는 페이징을 다음 두 API로 추상화했다.

- `setFirstResult(int startPosition)`: 조회 시작 위치 (0부터 시작)
- `setMaxResults(int maxResult)`: 조회할 데이터 수

```java
TypedQuery<UserDto> query = em.createQuery("select new learn.jpa.model.UserDto(m.name, m.age) from Member m", UserDto.class);
query.setFirstResult(1);
query.setMaxResults(1);

List<UserDto> resultList = query.getResultList();
```

JPA에서는 데이터 베이스 방언을 사용해 데이터 베이스마다 다른 페이징처리를 API로 처리할 수 있다.

### 집합과 정렬

집합 함수 사용시 참고사항

- `null`값은 무시하므로 통계에 잡히지 않는다.
- 값이 없는데 `SUM`, `AVG` 같은 함수를 사용하면 `null`값이 된다.
- `DISTINCT`를 집합 함수 안에 사용해 중복된 값을 젝저하고 나서 집합을 구할 수 있다.
- `DISTINCT`를 `count`에서 사용할 때 임베디드 타입은 지원하지 않는다.

### group by, HAVING

`GROUP BY`는 통계 데이터를 구할 때 특정 그룹끼리 묶어준다.

`HAVING`은 `GROUP BY`와 함께 사용하는데 `GROUP BY`로 그룹화한 통계 데이터를 기준으로 필터링한다.

통계 쿼리는 보통 전체 데이터를 기준으로 처리하므로 실시간으로 사용하기엔 부담이 많기 때문에 결과가 많다면 결과만 저장하는 테이블을 별도로 만들고 사용자가 적은 시간에 통계 쿼리를 실행해 그 결과를 보관하는 것이 좋다.

### JPQL 조인

JPQL도 조인을 지원하는데 SQL 조인과 기능은 같고 문법만 약간 다르다.

#### 내부 조인

내부 조인은 `INNER JOIN` 을 사용한다. `INNER`는 생략할 수 있다.

```java
SELECT m
FORM Member m INNER JOIN m.team t
WHERE t.name = :teamName
```

JPQL 조인의 가장 큰 특징은 연관 필드를 사용한다는 것이다.

`m.team`이 연관 필드이다.

연관 필드는 다른 엔티티와 연관관계를 가지기 위해 사용하는 필드를 말한다.

JPQL은 JOIN 명령어 다음에 조인할 객체의 연관 필드를 사용한다.

#### 외부 조인

JPQL의 외부 조인은 다음과 같이 사용한다.

```java
SELECT m
FROM Member m LEFT JOIN m.team t
```

외부 조인은 기능상 SQL의 외부 조인과 같다. OUTER는 생략 가능하다.

#### 컬렉션 조인

일대다 관계나 다대다 관계처럼 컬렉션을 사용하는 곳에 조인하는 것을 **컬렉션 조인**이라 한다.

#### 세타 조인

`WHERE`절을 사용해 세타 조인을 할 수 있다.

**세타 조인은 내부 조인만 지원한다.**

```java
SELECT count(m)
FROM Member m, Team t
WHERE m.username = t.name
```

### JOIN ON 절

JPA 2.1부터 조인할 때 `ON`절을 지원한다.

`ON`절을 사용하면 조인 대상을 필터링하고 조인할 수 있다.

보통 `ON`절은 외부 조인에서만 사용한다.

모든 회원을 조회하면서 회원관 연관된 팀도 조회할 때 팀 이름이 A인 팀만 조회하는 쿼리이다.

```java
SELECT m, t FROM Member m
LEFT JOIN m.team t on t.name = 'A'
```

### 페치 조인

페치 조인은 JPQL에서 성능 최적화를 위해 제공하는 기능이다.

연관된 엔티티나 컬렉션을 한 번에 같이 조회하는 기능이다.

### 엔티티 페치 조인

페치 조인을 사용해서 회원 엔티티를 조회하면서 연관된 팀 엔티티도 함께 조회하는 JPQL이다.

```java
SELECT m
FROM Member m JOIN FETCH m.team
```

`join fetch`를 사용하면 연관된 엔티티나 컬렉션을 **함께 조회한다.**

**페치 조인은 별칭을 사용할 수 없다.**

페치 조인으로 조회한 연관된 엔티티는 **프록시가 아닌 실제 엔티티**이다.

따라서 지연 로딩이 일어나지 않는다.

또한 프록시가 아닌 실제 엔티티이므로 준영속 상태가 되어도 연관된 엔티티를 조회할 수 있다.

### 컬렉션 페치 조인

일대다 관계인 컬렉션을 페치 조인하는 JPQL이다.

```java
SELECT t
FROM Team t join fetch t.members
WHERE t.name = 'A'
```

이때 조인 결과 테이블을 보면 동일한 팀 엔티티가 여러개일 수 있다.

일대다 조인은 결과가 증가할 수 있지만 일대일, 다대일 조인은 결과가 증가하지 않는다.

### 페치 조인과 DISTINCT

JPQL의 `DISTINCT` 명령어는 SQL에 `DISTINCT`를 추가하고 애플리케이션에서 한 번 더 중복을 제거한다.

```java
SELECT distinct t
FROM Team t join fetch t.members
WHERE t.name = 'A'
```

`SELECT distinct t` 는 팀 엔티티의 중복을 제거하라는 것이다.

### 페치 조인과 일반 조인의 차이

JPQL은 결과를 반환할 때 연관관계까지 고려하지 않는다.

SELECT 절에서 지정한 엔티티만 조회할 뿐이다.

엔티티에 즉시 조인이 설정되었을 시 일반 조인시 프록시 객체나 아직 초기화하지 않은 컬렉션 래퍼를 반환한다.

반면 페치 조인을 사용하면 연관된 엔티티도 **함께 조회**하기 때문에 실제 엔티티가 반환된다.

### 페치 조인의 특징과 한계

페치 조인을 사용하면 SQL 한 번으로 연관된 엔티티들을 함께 조회할 수 있어 성능을 최적화할 수 있다.

엔티티에 직접 적용하는 로딩 전략은 애플리케이션 전체에 영향을 미치므로 **글로벌 로딩 전략**이라 부른다.

**페치 조인은 글로벌 로딩 전략보다 우선한다.**

페치 조인을 사용하면 일부는 빠를 수 있지만 전체로 보면 사용하지 않는 엔티티를 자주 로딩하므로 오히려 성능에 악영향을 미칠 수 있다.

글로벌 로딩 전략은 **지연 로딩**으로하고 최적화가 필요하면 페치 조인을 적용하는 것이 효과적이다.

페치 조인은 다음과 같은 한계가 있다.

- 페치 조인 대상에는 별칭을 줄 수 없다.
  - 따라서 SELECT, WHERE, 서브 쿼리에 페치 조인 대상을 사용할 수 없다.
- 둘 이상의 컬렉션을 페치할 수 없다.
- 컬렉션을 페치 조인하면 페이징 API를 사용할 수 없다.

### 경로 표현식

경로 표현식은 `.`을 찍어 객체 그래프를 탐색하는 것이다.

- 상태 필드: 단순히 값을 저장하기 위한 필드
- 연관 필드: 연관 관계를 위한 필드, 임베디드 타입 포함
  - 단일 값 연관 필드 : @ManyToOne, @OneToOne
  - 컬렉션 값 연관 필드: @OneToMany, @ManyToMany

상태 필드는 단순히 값을 저장하는 필드이고, 연관 필드는 객체 사이의 연관관계를 맺기 위해 사용하는 필드다.

### 경로 표현식과 특징

JPQL에서 경로 표현식을 사용하려면 각 경로에 따라 어떤 특징이 있는지 알아야 한다.

- 상태 필드 경로: 경로 탐색의 끝. 더는 탐색할 수 없다.
- 단일 값 연관 경로: 묵시적으로 내부 조인이 일어난다. 단일 값 연관 경로는 계속 탐색할 수 있다.
- 컬렉션 값 연관 경로: 묵시적으로 내부 조인이 일어난다. 더는 탐색할 수 없다. from 절에서 조인을 통해 별칭을 얻으면 별칭으로 탐색할 수 있다.

단일 값 연관 필드로 경로 탐색을 하면 SQL에서 내부 조인이 일어나는데 이를 **묵시적 조인**이라 한다.

### 경로 탐색을 사용한 묵시적 조인 시 주의사항

묵시적 조인이 발생했을 때 주의사항은 다음과 같다.

- 항상 내부 조인이다.
- 컬렉션에서 경로 탐색을 하려면 명시적으로 조인을 해서 별칭을 얻어야 한다.
- 묵시적 조인으로 인해 SQL의 FROM절에 영향을 준다.

묵시적 조인은 조인이 일어나는 상황을 파악하기 어렵기 때문에 묵시적 조인보다는 명시적 조인을 사용하자.

### 서브 쿼리 

JPQL에서 서브 쿼리는 `SELECT`, `FROM`절에서는 사용할 수 없다.

서브 쿼리는 `EXISTS`, `ALL`, `ANY`, `SOME`, `IN` 과 함께 사용할 수 있다.

### 컬렉션 식

컬렉션 식은 컬렉션에만 사용하는 특별한 기능이다.

컬렉션은 컬렉션 식 이외에 다른 식은 사용할 수 없다.

`is null`처럼 컬렉션 식이 아닌 것은 사용할 수 없다.

- 빈 컬렉션 비교 식
  - `m.orders is not empty`
- 컬렉션의 멤버 식
  - 엔티티나 값이 컬렉션에 포함되어 있으면 참
  - `where :param member of t.members`

### 스칼라 식

스칼라는 숫자, 문자, 날짜, case, 엔티티 타입 같은 가장 기본적인 타입들을 말한다.

### 기타 정리

- enum은 `=` 비교 연산만 지원한다.
- 임베디드 타입은 비교를 지원하지 않는다.
- JPA 표준은 ``을 길이 0인 empty String으로 정했지만 데이터베이스마다 null로 사용하는 경우도 있으니 확인해야 한다.
  
### 엔티티 직접 사용

- 기본 키 값
  - JPQL에서 엔티티 객체를 직접 사용하면 SQL에서는 해당 엔티티의 기본 키 값을 사용한다.
- 외래 키 값

### Named 쿼리: 정적 쿼리

JPQL 쿼리는 크게 동적 쿼리와 정적 쿼리로 나눌 수 있다.

- 동적 쿼리: JPQL을 문자로 완성해 직접 넘기는 것
- 정적 쿼리: 미리 정의한 쿼리에 이름을 부여해 필요할 때 사용하는 것

Named 쿼리는 애플리케이션 로딩 시점에 JPQL 문법을 체크하고 미리 파싱해 둔다.

파싱된 결과를 재사용하므로 성능상 이점도 있다.

Named 쿼리는 `@NamedQuery`를 사용해 자바 코드나 XML 문서에 작성할 수 있다.

```java
@NamedQuery(
    name = "Member.findByName",
    query = "select m from Member m where m.name = :name
)
public class Member {

}
```

```java
TypedQuery<Member> query = em.createNamedQuery("Member.findByName", Member.class);

query.setParameter("name", "name1");

List<Member> resultList = query.getResultList();
```

`em.createNamedQuery()`를 사용해 Named 쿼리를 사용한다.

Named 쿼리는 영속성 유닛 단위로 관리되므로 충돌을 방지하기 위해 엔티티 이름을 앞에 주는 것이 좋다.

하나의 엔티티에 2개 이상의 Named 쿼리를 정의하려면 `@NamedQueries`를 사용하면 된다.

`@NamedQuery`에는 다음과 같은 속성이 있다.

- `lockMode`: 쿼리 실행 시 락을 건다.
- `hints` 2차 캐시를 다룰 때 사용한다.

만약 XML과 어노테이션에 같은 설정이 있을 경우 **XML**이 우선권을 가진다.





