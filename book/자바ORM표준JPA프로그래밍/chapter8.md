
## 프록시와 연관관계 관리

---


### 프록시

엔티티를 조회할 때 연관된 엔티티들이 항상 사용되는 것은 아니다.

```java
@Entity
public class Member {

  @Id
  private Long id;

  private String username;

  @ManyToOne
  private Team team;
}
```

```java
@Entity
public class Team {
  @Id
  private Long id;
  
  public String name;
}
```

```java
public void printUserAndTeam(String memberId) {
    Member member = em.find(Member.class, memberId);
    Team team = member.getTeam();
    System.out.println("회원 이름 : " + member.getUsername());
    System.out.println("소속 팀 : " + team.getName());
}
```

위의 `printUserAndTeam` 메서드는 회원 엔티티를 찾아 팀의 이름도 출력한다.

이 때는 회원 엔티티를 조회할 때 팀 엔티티까지 함께 조회하는 게 효율적이다.

반면에 회원 엔티티만 사용할 때는 팀 엔티티까지 DB에서 조회하는 것은 효율적이지 못하다.

`JPA`는 이런 문제를 해결하기 위해 **엔티티가 실제 사용될 때까지 데이터베이스 조회를 지연하는 방법을 제공**한다.

이것을 **지연 로딩**이라고 한다.

지연 로딩은 JPA 표준 명세에서 JPA 구현체에게 위임했기 때문에 앞으로의 내용은 `하이버네이트` 구현체에 대한 내용이다.


### 프록시 기초

JPA에서 식별자로 엔티티 하나를 조회할 때 `EntityManager.find()`를 사용한다.

이때 영속성 컨텍스트에 엔티티가 없으면 데이터베이스에서 조회한다.

엔티티를 실제 사용하는 시점까지 데이터베이스 조회를 미루고 싶으면

`EntityManager.getReference()`메서드를 사용하면 된다.

이 메서드를 호출할 때 데이터베이스를 조회하지 않고 실제 엔티티 객체도 생성하지 않는 대신 데이터베이스 접근을 위한 **프록시 객체**를 반환한다.

- 프록시의 특징

프록시 클래스는 실제 클래스를 상속 받아서 만들어지므로 실제 클래스와 겉 모양이 같다.

사용하는 입장에서는 진짜 객체인지 프록시 객체인지 구분하지 않아도 된다.

프록시 객체의 메서드를 호출하면 프록시 객체는 실제 객체의 메서드를 호출한다.

- 프록시 객체의 초기화

프록시 객체는 실제 사용될 때 데이터베이스를 조회해 실제 엔티티 객체를 생성한다.

이를 **프록시 객체의 초기화**라고 한다.

프록시 객체의 초기화 과정은 다음과 같다.

1. 프록시 객체에 엔티티를 사용하는 메서드를 호출한다.
2. 프록시 객체에서 실제 데이터를 조회한다.
3. 실제 엔티티가 생성되어 있지 않으면 영속성 컨텍스트에 실제 엔티티를 생성을 요청한다.
   1. 이를 초기화라 한다.
4. 영속성 컨텍스트는 데이터 베이스를 조회해 실제 엔티티 객체를 생성한다.
5. 프록시 객체는 생성된 실제 엔티티의 객체의 참조를 target 멤버변수에 보관한다.
6. 프록시 객체는 실제 엔티티 객체의 메서드를 호출해 결과를 반환한다.

- 프록시의 특징

프록시의 특징은 다음과 같다.

- 프록시 객체는 처음 사용할 때 한 번만 초기화된다.
- 프록시 객체를 초기화한다고 프록시 객체가 실제 엔티티로 바뀌는 것은 아니다. 프록시 객체가 초기화되면 프록시 객체를 통해 실제 엔티티에 접근할 수 있다.
- 프록시 객체는 원본 엔티티를 상속받은 객체이므로 타입 체크 시 주의해야 한다.
- 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 데이터베이스를 조뢰할 필요가 없기 때문에 프록시가 아닌 실제 엔티티를 반환한다.
- 초기화는 영속성 컨텍스트의 도움을 받아야 가능하다.
  - 준영속 상태의 프록시를 초기화하면 에러가 발생한다.

### 프록시와 식별자

엔티티를 프록시로 조회할 때 식별자 값을 파라미터로 전달하는데 프록시 객체는 이 식별자 값을 보관한다.

프록시 객체는 식별자 값을 가지고 있으므로 식별자 값을 조회하는 `getId()`를 호출해도 프록시를 초기화하지 않는다.

단, 엔티티 접근 방식을 프로퍼티로 설정한 경우에만 초기화하지 않는다.

프록시는 연관관계를 설정할 때 유용하게 사용할 수 있다.

연관관계를 설정할 때는 식별자 값만 사용하므로 프록시를 사용하면 데이터베이스 접근 횟수를 줄일 수 있다.

연관관계를 설정할 때 엔티티 접근 방식을 필드로 설정해도 프록시를 초기화하지 않는다.

### 프록시 확인

JPA가 제공하는 `PersistenceUnitUtil.isLoaded(Object entity)` 메서드를 사용하면 프록시 인스턴스의 초기화 여부를 확인할 수 있다.

아직 초기화되지 않은 프록시 인스턴스는 `false`를 반환한다.

이미 초기화되었거나 프록시 인스턴스가 아니면 `true`를 반환한다.

### 즉시 로딩과 지연 로딩

프록시 객체는 주로 연관된 엔티티를 지연 로딩할 때 사용한다.

JPA는 개발자가 연관된 엔티티의 조회 시점을 선택할 수 있도록 다음 두 가지 방법을 제공한다.

- 즉시 로딩: 엔티티를 조회할 때 연관된 엔티티도 함께 조회한다.
  - 설정 방법: `@ManyToOne(fetch = FetchType.EAGER)`
- 지연 로딩: 연관된 엔티티를 실제 사용할 때 조회한다.
  - 설정 방법: `@ManyToOne(fetch = FetchType.LAZY)`

### 즉시 로딩

즉시 로딩을 사용하려면 `@ManyToOne`의 `fetch`속성을 `FetchType.EAGER`로 지정한다.

```java
@Entity
public class Member {

  @Id
  private Long id;

  private String username;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "TEAM_ID")
  private Team team;
}
```

회원을 조회하는 순간 팀도 함께 조회한다.

이 때 쿼리를 2번 실행할 것 같지만 대부분의 JPA 구현체는

**즉시 로딩을 최적화하기 위해 가능하면 조인 쿼리를 사용한다.**

회원 테이블에 `TEAM_ID`외래 키는 `NULL`값을 허용하고 있기 때문에 팀에 소속되지 않은 회원이 있을 가능성이 있다.

팀에 소석하지 않은 회원과 팀을 내부 조인하면 팀은 물론이고 회원 데이터도 조회할 수 없다.

JPA는 이런 상황을 고려해서 외부 조인을 사용한다.

외부 조인보다 내부 조인이 성능 최적화에 유리하다.

외래 키에 `NOT NULL`제약 조건을 설정하면 값이 있는 것을 보장하기 때문에 내부 조인을 사용해도 된다.

`@JoinColumn`에 `nullable = false`를 설정해서 해당 외래 키는 `NULL`값을 허용하지 않는다고 JPA에게 알려주어야한다.

이렇게하면 JPA는 외부 조인 대신 내부 조인을 사용한다.

**nullable 설정에 따른 조인 전략**
- `@JoinColumn(nullable = true)`: `NULL`허용, 외부 조인 사용
- `@JoinColumn(nullable = false)`: `NULL`허용하지 않음, 내부 조인 사용

`@ManyToOne(optional = false)` 처럼 `optional` 옵션을 `false`로 설정해도 내부 조인을 사용한다.

JPA는 선택적 관계면 외부 조인을 필수 관계면 내부 조인을 사용한다.

### 지연 로딩

지연 로딩을 사용하려면 `@ManyToOne`의 `fetch` 속성을 `FetchType.LAZY`로 지정한다.

```java
@Entity
public class Member {

  @Id
  private Long id;

  private String username;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "TEAM_ID")
  private Team team;
}

```

회원과 팀을 지연 로딩으로 설정했다.

회원을 조회하면 회원만 조회하고 팀은 조회하지 않는다.

대신 조회한 회원의 `team` 멤버변수에 프록시 객체를 넣어둔다.

이 프록시 객체는 실제 사용될 때까지 데이터 로딩을 미룬다 실제 데이터가 필요한 순간이 되어서야 데이터베이스를 조회해서 프록시 객체를 초기화한다.

team 엔티티가 영속성 컨텍스트에 이미 로딩되어 있다면 프록시가 아닌 실제 엔티티를 반환하게 된다.

### 즉시 로딩, 지연 로딩 정리

연관된 엔티티에 대한 즉시로딩, 지연로딩은 상황에 따라 어떤 것이 좋은지가 다르다.

- 지연 로딩: 연관된 엔티티를 프록시로 조회한다. 프록시를 실제 사용할 때 초기화하면서 데이터베이스를 조회한다.
- 즉시 로딩: 연관된 엔티티를 즉시 조회한다. 하이버네이트는 가능하면 SQL 조인을 사용해서 한 번에 조회한다.

### 프록시와 컬렉션 래퍼

하이버네이트는 엔티티를 영속 상태로 만들 때 엔티티에 컬렉션이 있으면 컬렉션을 추적하고 관리할 목적으로 원본 컬렉션을 하이버네이트가 제공하는 내장 컬렉션으로 변경한다.

이것을 컬렉션 래퍼라 한다.

엔티티를 지연 로딩하면서 프록시 객체를 사용해 지연 로딩을 수행하지만 컬렉션은 컬렉션 래퍼가 지연 로딩을 처리해준다.

### JPA 기본 페치 전략

`fetch` 속성의 기본 설정값은 다음과 같다.

- `@ManyToOne`, `@OneToOne`: 즉시 로딩(fetchType.EAGER)
- `@OneToMany`, `@ManyToMany`: 지연 로딩(fetchType.LAZY)

JPA의 기본 페치 전략은 연관된 엔티티가 하나면 `즉시 로딩`을 컬렉션이면 `지연 로딩`을 사용한다.

컬렉션을 로딩하는 것은 비용이 많이 들고 너무 많은 데이터를 로딩할 수 있기 때문이다.

**추천하는 방법은 모든 연관관계에 지연 로딩을 사용하는 것이다.**

애플리케이션 개발이 어느 정도 완료단계에 왔을 때 실제 사용하는 상황을 보고 꼭 필요한 곳에만 즉시 로딩을 사용하도록 최적화하면 된다.

### 컬렉션에 FetchType.EAGER 사용 시 주의 점

컬렉션에 `FetchType.EAGER`를 사용할 경우에 주의할 점은 다음과 같다.

- 컬렉션을 하나 이상 즉시 로딩하는 것은 권장하지 않는다.
  - 서로 다른 컬렉션을 2개 이상 조인할 때 SQL 실행 결과가 N * M이 되서 너무 많은 데이터를 반환할 수 있고 애플리케이션 성능이 저하될 수 있다.
  - JPA는 이렇게 조회된 결과를 메모리에서 필터링해 반환한다.
  - 2개 이상의 컬렉션을 즉시 로딩으로 설정하는 것은 권장하지 않는다.
- 컬렉션 즉시 로딩은 항상 외부 조인을 사용한다.
  - JPA는 일대다 관계를 즉시 로딩할 때 항상 외부 조인을 사용한다.

### 영속성 전이: CASCADE

특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속 상태로 만들고 싶으면 영속성 전이 기능을 사용하면 된다.

JPA는 `CASCADE` 옵션으로 영속성 전이를 제공한다.

영속성 전이를 사용하면 부모 엔티티를 저장할 때 자식 엔티티도 함께 저장할 수 있다.

JPA에서 엔티티를 저장할 때 연관된 모든 엔티티는 영속 상태여야 한다. 

### 영속성 전이: 저장

영속성 전이를 활성화하는 옵션을 적용한다.

```java
@Entity
public class Parent {
  // 생략
  
  @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST)
  private List<Child> children = new ArrayList<>();
}
```

부모만 영속화하면 `CascadeType.PERSIST` 로 설정한 자식 엔티티까지 함께 영속화해서 저장한다.

### 영속성 전이: 삭제

`CascadeType.REMOVE`로 설정하면 부모 엔티티만 삭제하면 연관된 자식 엔티티도 함께 삭제된다.

```java
Parent findParent = em.find(Parent.class, 1L);
em.remove(findParent);
```
위의 코드를 실행하면 `DELETE` SQL을 실행한다.

부모는 물론 연관된 자식도 모두 삭제한다.

삭제 순서는 외래 키 제약조건을 고려해 자식을 먼저 삭제하고 부모를 삭제한다.

`CascadeType.REMOVE`를 설정하지 않으면 부모 엔티티만 삭제하게 되므로 외래 키 제약 조건으로 데이터베이스에서 외래 키 무결성 예외가 발생한다.

### 고아 객체

JPA는 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제하는 기능을 제공한다.

**부모 엔티티의 컬렉션에서 자식 엔티티의 참조만 제거하면 자식 엔티티가 자동으로 삭제된다.**

```java
@Entity
public class Parent {
  // 생략
  
  @OneToMany(mappedBy = "parent", orphanRemoval = true)
  private List<Child> children = new ArrayList<>();
}
```

`orphanRemoval = true`를 설정해 고아 객체 제거 기능을 활성화한다.

고아 객체 제거는 **참조가 제거된 엔티티는 다른 곳에서 참조하지 않는 고아 객체로 보고 삭제하는 기능**이다.

이 기능은 참조하는 곳이 **하나**일 때만 사용해야 한다.

특정 엔티티가 개인 소유하는 엔티티에만 이 기능을 적용해야 하기 때문에 `orphanRemoval`은 `@OneToOne`, `@OneToMany`에만 사용할 수 있다.

고아 객체 제거는 부모를 제거하면 자식 객체는 고아가 되기 때문에 부모를 제거하면 자식 객체도 같이 제거된다.

### 영속성 전이 + 고아 객체, 생명 주기

`CascadeType.ALL` + `orphanRemoval = true`를 동시에 사용하면 부모 엔티티를 통해 자식의 생명주기를 관리할 수 있다.
