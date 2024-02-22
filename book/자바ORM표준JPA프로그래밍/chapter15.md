
## 고급 주제와 성능 최적화

---

### 예외 처리

JPA 표준 예외들은 `PersistenceException`의 자식 클래스다.

이 예외 클래스는 `RuntimeException`의 자식이다. 

따라서 **JPA 예외는 모두 언체크 예외다.**

JPA 표준 예외는 크게 2가지로 나눌 수 있다.

- 트랜잭션 롤백을 표시하는 예외
  - 이 예외가 발생하면 트랜잭션을 강제로 커밋해도 트랜잭션이 커밋되지 않는다.
- 트랜잭션 롤백을 표시하지 않는 예외
  - 개발자가 트랜잭션을 커밋할지 롤백할지를 판단한다.

### 스프링 프레임워크의 JPA 예외 반환

서비스 계층에서 JPA의 예외를 직접 사용하면 JPA에 의존하게 된다.

스프링은 이런 문제를 해결하기 위해 데이터 접근 계층에 대한 예외를 추상화해서 개발자에게 제공한다.

### 스프링 프레임워크에 JPA 예외 변환기 적용

JPA 예외를 스프링이 제공하는 추상화된 예외로 변경하려면 `PersistenceExceptionTranslationPostProcessor`를 빈으로 등록하면 된다.

이것은 `@Repository`를 사용한 곳에 예외 변환 AOP를 적용해 JPA 예외를 스프링이 추상화한 예외로 변환해준다.

만약 예외를 변환하지 않고 그대로 반환하려면 `throws`절에 그대로 반환할 JPA 예외나 JPA 예외의부모 클래스를 직접 명시하면 된다.

### 트랜잭션 롤백 시 주의사항

트랜잭션을 롤백하는 것은 데이터베이스의 반영사항만 롤백하는 것이지 **수정한 자바 객체까지 원상태로 복구해주지는 않는다.**

엔티티를 조회해서 수정하는 중 트랜잭션을 롤백하면 데이터베이스의 데이터는 원래대로 복구되지만 객체는 수정된 상태로 영속성 컨텍스트에 남아 있다.

따라서 트랜잭션이 롤백된 영속성 컨텍스트를 그대로 사용하는 것은 위험하다.

새로운 영속성 컨텍스트를 사용하거나 `em.clear()`를 호출해서 영속성 컨텍스트를 초기화한 다음에 사용해야 한다.

기본 전략인 트랜잭션당 영속성 컨텍스트 전략은 문제가 발생하면 트랜잭션 AOP 종료 시점에

트랜잭션을 롤백하면서 영속성 컨텍스트도 함께 종료한다.

OSIV처럼 영속성 컨텍스트의 범위를 트랜잭션 범위보다 넓게 사용해 여러 트랜잭션이 하나의 영속성 컨텍스트를 사용할때 문제가 발생한다.

스프링은 영속성 컨텍스트의 범위를 트랜잭션의 범위보다 넓게 설정하면 트랜잭션 롤백시 영속성 컨텍스트를 초기화해서 잘못된 영속성 컨텍스트를 사용하는 문제를 예방한다.

### 엔티티 비교

영속성 컨텍스트 내부에는 엔티티 인스턴스를 보관하기 위한 1차 캐시가 있다.

1차 캐시는 영속성 컨텍스트와 생명주기를 같이 한다.

영속성 컨텍스트를 더 정확히 이해하기 위해서는 1차 캐시의 가장 큰 장점인 **애플리케이션 수준의 반복 가능한 읽기** 를 이해해야 한다.

같은 영속성 컨텍스트에서 엔티티를 조회하면 항상 같은 엔티티 인스턴스를 반환한다.

동등성 비교 수준이 아닌 주소값이 같은 인스턴스를 반환한다.

### 영속성 컨텍스트가 같을 때 엔티티 비교

영속성 컨텍스트가 같으면 다음 3가지 조건을 모두 만족한다.

- 동일성: `==` 비교가 같다.
- 동등성: `equals()` 비교가 같다.
- 데이터베이스 동등성: `@Id`인 데이터베이스 식별자가 같다.

`@Transaction`의 기본 전략은 먼저 시작된 트랜잭션이 있으면 그 트랜잭션을 그대로 이어 받아 사용하고 없으면 새로 시작한다.

또한 테스트 클래스에 `@Transaction`을 적용하면 테스트가 끝날 때 트랜잭션을 커밋하지 않고 트랜잭션을 강제로 롤백한다.

하지만 롤백시에는 영속성 컨텍스트를 플러시하지 않기 때문에 플러시 시점에 어떤 SQL이 실행되는지 콘솔에 남지 않는다.

콘솔을 통해 보고 싶으면 테스트 마지막에 `em.flush()`를 강제로 호출하면 된다.

### 영속성 컨텍스트가 다를 때 엔티티 비교

영속성 컨텍스트가 다르면 인스턴스는 다르지만 같은 데이터베이스 로우를 가르키고 있기 때문에 사실상 같은 엔티티로 보아야 한다.

영속성 컨텍스트가 다를 때 엔티티 비교는 다음과 같다.

- 동일성: `==` 비교가 실패한다.
- 동등성: `equals()` 비교를 만족한다. 단, `equals`를 구현해야 한다.
- 데이터베이스 동등성: `@Id`인 데이터베이스 식별자가 같다.

엔티티를 비교할 때는 비즈니스 키를 활용한 동등성 비교를 권장한다.

동일성 비교는 같은 영속성 컨텍스트의 관리를 받는 영속 상태의 엔티티에만 적용할 수 있다.

그렇지 않을 때는 비즈니스 키를 사용한 동등성 비교를 해야 한다.

### 프록시 심화 주제

프록시는 원본 엔티티를 상속받아 만들어지므로 엔티티를 사용하는 클라이언트는 엔티티가 프록시인지 원본 엔티티인지 구분하지 않고 사용할 수 있다.

하지만 프록시를 사용하는 방식의 기술적인 한계로 인해 예상하지 못한 문제들이 발생한다.

### 영속성 컨텍스트와 프록시

영속성 컨텍스트는 자신이 관리하는 영속 엔티티의 동일성을 보장한다.

때문에 영속성 컨텍스트는 프록시로 조회된 엔티티에 대해서 같은 엔티티를 찾는 요청이 오면 

원본 엔티티가 아닌 처음 조회된 프록시를 반환한다.

따라서 프록시를 먼저 조회하고 원본 엔티티를 조회해도 영속성 컨텍스트는 영속 엔티티의 동일성을 보장한다.

원본 엔티티를 먼저 조회하면 영속성 컨텍스트는 원본 엔티티를 이미 데이터베이스에서 조회했으므로 프록시를 반환할 이유가 없다.

따라서 원본 엔티티를 먼저 조회하는 경우에도 영속성 컨텍스트는 자신이 관리하는 영속 엔티티의 동일성을 보장한다.

### 프록시 타입 비교

프록시는 원본 엔티티를 상속 받아서 만들어지므로 프록시로 조회한 엔티티의 타입을 비교할 때는 `==` 비교를 하면 안 된다.

프록시는 원본 엔티티의 자식 타입이므로 `instanceof`를 사용해야 한다.

### 프록시 동등성 비교

엔티티의 동등성을 비교하려면 비즈니스 키를 사용해 `equals()` 메서드를 오버라이팅하고 비교하면 된다.

IDE나 외부 라이브러리를 사용해 구현한 `equals()` 메서드로 엔티티를 비교할 때 비교 대상이 프록시면 문제가 발생할 수 있다.

```java
@Entity
public class Member {
	@Id
	private String id;
	private String name;

	...

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (this.getClass() != obj.getClass()) return false; // 1

		Member member = (Member) obj;

		if (this.name != null ? !this.name.equals(member.name) : member.name != null) // 2
			return false;

		return true;
	}
}
```

1. 주석 1번을 보면 `!=` 로 타입을 동등성 비교한다.
   - 프록시는 원본을 상속받은 자식 타입이므로 프록시 타입을 비교할 때는 `instanceof`를 사용해야 한다.
2. 주석 2번을 보면 `member.name`으로 프록시의 멤버변수에 직접 접근한다.
   - `equals()` 메서드를 구현할 때는 일반적으로 멤버변수를 직접 비교하는데 프록시의 경우 문제가 된다.
   - 프록시의 멤버변수에 직접 접근하면 아무값도 조회할 수 없다.
   - 프록시의 데이터를 조회할 때는 `getter`를 사용해야 한다.

프록시의 동등성을 비교할 때는 다음 사항을 주의해야 한다.
- 프록시의 타입 비교는 `==` 비교 대신에 `instanceof`를 사용해야 한다.
- 프록세의 멤버변수에 직접 접근하면 안 되고 접근자 메서드를 사용해야 한다.

### 상속 관계와 프록시

**프록시를 부모 타입으로 조회하면 문제가 발생한다.**

```java
@Test
public void 부모타입으로_프록시조회() {
	//테스트 데이터 준비
	Book saveBook = new Book();
	saveBook.setName("jpaBook");
	saveBook.setAuthor("kim");
	em.persist(saveBook);

	em.flush();
	em.clear();

	//테스트 시작
	Item proxyItem = em.getReference(Item.class, saveBook.getId());
	System.out.println("proxyItem = " + proxyItem.getClass());

	if (proxyItem instanceof Book) {
		System.out.println("proxyItem instanceof Book");
		Book book = (Book) proxyItem;
		System.out.println("책 저자 = " + book.getAuthor());
	}

	//결과 검증
	Assert.assertFalse(proxyItem.getClass() == Book.class);
	Assert.assertFalse(proxyItem instanceof Book);
	Assert.assertTrue(proxyItem instanceof Item);
}
```

`em.getReference()` 메서드를 사용해 `Item` 엔티티를 프록시로 조회했다.

이때 실제 조회된 엔티티는 `Book`이므로 `Book`타입을 기반으로 원본 엔티티 인스턴스가 생성된다.

그런데 `Item`엔티티를 대상으로 조회했으므로 프록시는 `Item`타입을 기반으로 만들어진다.

이 프록시 클래스는 원본 엔티티르 `Book` 엔티티를 참조한다.

때문에 프로시는 `Item$Proxy`타입이고 `Book`타입과는 관계가 없다.

따라서 직접 다운 캐스팅을 해도 문제가 발생한다.

프록시를 부모 타입으로 조회하면 부모의 타입을 기반으로 프록시가 생성되는 문제가 있다.

- `instanceof` 연산을 할 수 없다.
- 하위 타입으로 다운 캐스팅을 할 수 없다.

프록시를 부모 타입으로 조회하는 문제는 주로 다형성을 다루는 도메인에서 나타난다.

상속관계에서 발생하는 프록시 문제를 해결하는 방법은 다음과 같다.

- JPQL로 대상 직접 조회
간단한 방법은 처음부터 자식 타입을 직접 조회해서 필요한 연산을 하는 것이다.

이 방법을 사용하면 다형성을 사용할 수 없다.

```java
Book jpqlBook = em.createQuery
        ("select b from Book b where b.id=:bookId", Book.class)
        .setParameter("bookId", item.getId())
        .getSingleResult();
```

- 프록시 벗기기

하이버네이트가 제공하는 기능을 사용하면 프록시에서 원본 엔티티를 가져올 수 있다.

```java
//하이버네이트가 제공하는 프록시에서 원본 엔티티를 찾는 기능을 사용하는 메소드
public static <T> T unProxy(Object entity){
	if(entity instanceof HibernateProxy) {
		entity = ((HibernateProxy) entity)
				.getHibernateLazyInitializer()
				.getImplementation();
	}
	return (T) entity;
}
```

이 방법은 **프록시에서 원본 엔티티를 직접 꺼내기 때문에 프록시와 원본 엔티티의 동일성 비교가 실패한다는 문제점이 있다.**

이 방법을 사용할 때는 원본 엔티티가 꼭 필요한 곳에서 잠깐 사용하고 다른 곳에서 사용되지 않도록 하는 것이 중요하다.

참고로 원본 엔티티의 값을 직접 변경해도 변경 감지 기능은 동작한다.

- 기능을 위한 별도의 인터페이스 제공

```java
public interface TitleView {
	String getTitle();
}

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE")
public abstract class Item implements TitleView {
	@Id @GeneratedValue
	@Column(name = "ITEM_ID")
	private Long id;
	
	...
}
```
`TitleView` 라는 공통 인터페이스를 만들고 자식 클래스들은 인터페이스의 `getTitle()` 메서드를 각각 구현했다.

사용하는 곳에서 `getTitle()`을 호출하면 된다.

이처럼 인터페이스를 제공하고 각각의 클래스가 자신에 맞는 기능을 구현하는 것은 다형성을 활용하는 좋은 방법이다.

이 방법을 사용할 때는 프록시의 특징 때문에 프록시의 대상이 되는 타입에 인터페이스를 적용해야 한다.

- 비지터 패턴 사용

비지터 패턴을 사용해서 상속관계와 프록시 문제를 해결할 수 있다.

```java
public interface Visitor {
	void visit(Book book);
	void visit(Album album);
	void visit(Movie movie);
}
public class PrintVisitor implements Visitor {
	@Override
	public void visit(Book book) {
		//넘어오는 book은 Proxy가 아닌 원본 엔티티다. 
		System.out.println("book.class = " + book.getClass());
	}
	
	@Override
	void visit(Album album) {...}
	@Override
	void visit(Movie movie) {...}
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE")
public abstract class Item {

	@Id @GeneratedValue
	@Column(name = "ITEM_ID")
	private Long id;

	...

	public abstract void accept(Visitor visitor);
}

@Entity
@DiscriminatorValue("B")
public class Book extends Item {

	...
	@Override
	public void accept(Visitor visitor){
		visitor.visit(this);
	}
}
```

비지터 패턴은 `Visitor`와 `Visitor`를 받아들이는 대상 클래스로 구성된다.

비지터 패턴의 장점은 다음과 같다.

- 프록시에 대한 걱정 없이 안전하게 원본 엔티티에 접근할 수 있다.
- `instanceof`와 타입캐스팅 없이 코드를 구현할 수 있다.
- 알고리즘과 객체 구조를 분리해서 구조를 수정하지 않고 새로운 동작을 추가할 수 있다.

단점은 다음과 같다.

- 너무 복잡하고 더블 디스패치를 사용하기 때문에 이해하기 어렵다.
- 객체 구조가 변경되면 모든 `Visitor`를 수정해야 한다.


### 성능 최적화

JPA로 애플리케이션을 개발할 때 발생하는 다양한 성능 문제와 해결 방안을 알아보자.

### N + 1 문제

JPA로 애플리케이션을 개발할 때 성능상 가장 주의해야 하는 것이 `N+1`문제다.

### 즉시 로딩과 N + 1

즉시 로딩의 문제점은 JPQL을 사용할 때 발생한다.

JPQL을 실행하면 JPA는 이것을 분석해서 SQL을 생성한다.

이때는 즉시 로딩과 지연 로딩에 대해서 전혀 신경 쓰지 않고 JPQL만 사용해 SQL을 생성한다.

이때 엔티티와 연관된 컬렉션이 즉시 로딩으로 설정되어 있으면 JPA는 컬렉션을 즉시 로딩하려고 SQL을 추가로 실행한다.

이처럼 **처음 실행한 SQL의 결과 수만큼 추가로 SQL을 실행하는 것을 N + 1문제라 한다.**

즉시 로딩은 JPQL을 실행할 때 N + 1 문제가 발생할 수 있다.

### 지연 로딩과 N + 1

즉시 로딩을 지연 로딩으로 변경해도 N + 1에서 자유로울 수는 없다.

지연 로딩으로 설정하면 JPQL에서는 N + 1 문제가 발생하지 않는다.

이후 비즈니스 로직에서 컬렉션을 사용할 때 SQL이 추가로 실행되는데 이것도 결국 `N + 1`문제다.

`N + 1`문제를 해결하는 방법에 대해 알아보자.

### 페치 조인 사용

`N+1` 문제를 해결하는 가장 일반적인 방법은 페치 조인을 사용하는 것이다.

페치 조인은 SQL 조인을 사용해서 연관된 엔티티를 함께 조회하므로 `N+1`문제가 발생하지 않는다.

이때 중복된 결과가 나타날 수 있으므로 JPQL의 `DISTINCT`를 사용해 중복을 제거하는 것이 좋다.

### 하이버네이트 @BatchSize

하이버네이트가 제공하는 `@BatchSize`를 사용하면 연관된 엔티티를 조회할 때 지정한 `size`만큼 SQL의 `IN` 절을 사용해 조회한다.

만약 조회한 회원이 10명인데 `size=5`로 지정하면 2번의 SQL만 추가로 실행한다.

`default_batch_size`속성을 사용하면 애플리케이션 전체에 기본으로 `@BatchSize`를 적용할 수 있다.

### 하이버네이트 @Fetch(FetchMode.SUBSELECT)

하이버네이트가 제공하는 `@Fetch`에 `FetchMode.SUBSELECT`로 사용하면 연관된 데이터를 조회할 때 서브 쿼리를 사용해서 `N+1`문제를 해결한다.

```java
@Entity
public class Member {

	@Id @GeneratedValue
	private Long id;

	@Fetch(FetchMode.SUBSELECT)
	@OneToMany(mappedBy = "member", fetch = FetchType.EAGER)
	private List<Order> orders = new ArrayList<Order>();

	...
}
```

### N + 1 정리

즉시 로딩과 지연 로딩 중 추천하는 방법은 즉시 로딩은 사용하지 말고 지연 로딩만 사용하는 것이다.

즉시 로딩의 가장 큰 문제점은 성능 최적화가 어렵다는 점이다.

에티티를 조회하다보면 즉시 로딩이 연속으로 발생해 예상하지 못한 SQL이 실행될 수 있다.

따라서 모두 지연 로딩으로 설정하고 성능 최적화가 꼭 필요한 곳에는 JPQL 페치 조인을 사용하자.

JPA의 글로벌 페치 전략 기본값은 다음과 같다.

- `@OneToOne`, `@ManyToOne` : 기본 페치 전략은 즉시 로딩
- `@OneToMany`, `@ManyToMany` : 기본 페치 전략은 지연 로딩

기본 값이 즉시 로딩이라면 `fetchType.Lazy`로 설정해서 지연 로딩 전략을 사용하도록 변경하자.

### 읽기 전용 쿼리의 성능 최적화

엔티티가 영속성 컨텍스트에 관리되면 1차 캐시부터 변경 감지까지 얻을 수 있는 혜택이 많지만

영속성 컨텍스트는 변경 감지를 위해 스냅샷 인스턴스를 보관하므로 더 많은 메모리를 사용하는 단점이 있다.

