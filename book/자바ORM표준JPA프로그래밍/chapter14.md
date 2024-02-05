
## 컬렉션과 부가 기능

---

### 컬렉션

JPA는 자바에서 기본으로 제공하는 `Collection`, `List`, `Set`, `Map` 컬렉션을 지원한다.

- `@OneToMany`, `@ManyToMany`를 사용해 일대다나 다대다 엔티티 관계를 매핑할 떄
- `@ElementCollection`을 사용해서 값 타입을 하나 이상 보관할 때

자바 컬렉션 인터페이스의 특징은 다음과 같다.

- `Collection`: 자바가 제공하는 최상위 컬렉션. 
  - 하이버네이트는 중복을 허용하고 순서를 보장하지 않는다고 가정한다.
- `Set`: 중복을 허용하지 않는 컬렉션. 순서를 보장하지 않는다.
- `List`: 순서가 있는 컬렉션. 순설르 보장하고 중복을 허용한다.
- `Map`: key, value 구조로 되어 있는 특수한 컬렉션

JPA 명세에는 자바 컬렉션 인터페이스에 대한 특별한 언급이 없으므로 하이버네이트 구현제를 기준으로 설명한다.

### JPA와 컬렉션

하이버네이트는 엔티티를 영속 상태로 만들 때 컬렉션 필드를 하이버네이트에서 준비한 컬렉션으로 감싸서 사용한다.

하이버네이트는 컬렉션을 효율적으로 관리하기 위해 엔티티를 영속 상태로 만들 때 원본 컬렉션을 감싸고 있는 내장 컬렉션을 생성해 내장 컬렉션을 사용하도록 참조를 변경한다.

이를 **래퍼 컬렉션**으로도 부른다.

이런 특징 때문에 하이버네이트는 컬렉션을 사용할 때 즉시 초기화해 사용하는 것을 권장한다.

```java
Collection<Member> members = new ArrayList<Member>();
```

- `Collection`, `List`
  - 내장 컬렉션 : `PersistenBag`
  - 중복 허용, 순서 보장 안함
- `Set`
  - 내장 컬렉션 : `PersistenSet`
  - 중복 허용 안함, 순서 보장 안함
- `List` + `@OrderColunm`
  - 내장 컬렉션 : `PersistenList`
  - 중복 허용, 순서 보장

### Collection, List

`Collection`, `List` 인터페이스는 중복을 허용하는 컬렉션이다.

`PersistenBag`을 래퍼 컬렉션으로 사용한다.

이 인터페이스는 `ArrayList`로 초기화하면 된다.

중복을 허용한다고 가정하므로 `add()` 메서드는 내부에서 어떤 비교도 하지 않고 항상 `true`를 반환한다.

같은 엔티티가 있는지 찾거나 삭제할 때는 `equals()` 메서드를 사용한다.

**엔티티를 추가할 때 중복된 엔티티가 있는지 비교하지 않고 단순히 저장만 하기 때문에 엔티티를 추가해도 지연 로딩된 컬렉션을 초기화하지 않는다.**

### Set

`Set`은 중복을 허용하지 않는 컬렉션이다. 

`PersistenSet`을 컬렉션 래퍼로 사용한다.

이 인터페이스는 `HashSet`으로 초기화하면 된다.

중복을 허용하지 않으므로 `add()` 메서드는 객체를 추가할 때마다 `equals()` 메서드로 같은 객체가 있는지 비교한다.

같은 객체가 없으면 객체를 추가하고 `true`를 반환하고, 같은 객체가 있으면 추가에 실패하고 `false`를 반환한다.

`HashSet`은 해시 알고리즘을 사용하므로 `hashcode()`도 함께 사용해서 비교한다.

**엔티티를 추가할 때 중복된 엔티티가 있는지 비교하기 때문에 엔티티를 추가할 때 지연 로딩된 컬렉션을 초기화한다.**

### List + @OderColumn

List 인터페이스에 `@OderColumn`을 추가하면 순서가 있는 특수한 컬렉션으로 인식한다.

데이터베이스에 순서 값을 저장해서 조회할 때 사용한다.

`PersistenList`를 컬렉션 래퍼로 사용한다.

```java
@OneToMany(mappedBy = "board")
@OrderColumn(name = "POSITION")
private List<Comment> comments = new ArrayList<Comment>();
```
순서가 있는 컬렉션은 데이터베이스에 순서 값도 함께 관리한다.

`@OrderColumn` 은 다음과 같은 단점들 때문에 실무에서 잘 사용하지 않는다.

- `POSITION` 값을 `update`하는 SQL이 추가로 발생한다.
- `LIST`를 변경하면 연관된 많은 위치 값을 변경해야 한다.
- 중간에 `POSITION`값이 없으면 조회한 `LIST`에는 `null`이 보관된다.

### @OrderBy

`@OrderColumn` 이 데이터베이스에 순서용 컬럼을 매핑해서 관리했다면 `@OrderBy`는 데이터베이스의 `ORDER BY` 절을 사용해 컬렉션을 정렬한다.

따라서 순서용 컬럼을 매핑하지 않아도 된다.

`@OrderBy`는 모든 컬렉션에 사용할 수 있다.

```java
import java.util.HashSet;

@OneToMany(mappedBy = "team")
@OrderBy("username desc, id asc")
private Set<Member> members = new HashSet<Member>();
```

`@OrderBy`의 값으로 `username desc, id asc`를 사용해 정렬했다.

`@OrderBy`의 값은 **엔티티의 필드를 대상**으로 한다.

### @Converter

컨버터를 사용하면 엔티티의 데이터를 변환해서 데이터베이스에 저장할 수 있다.

엔티티의 값은 `Boolean`이지만 데이터베이스에는 숫자 대신 문자 Y 또는 N, 0이나 1로 저장하고 싶을 때 컨버터를 사용하면 된다.

```java
@Converter
public class BooleanToYNConverter implements AttributeConverter<Boolean, String> {

  @Override
  public String convertToDatebaseColumn(Boolean attribute) {
    return (attribute != null && attribute) ? "Y" : "N";
  }

  @Override
  public Boolean convertToEntityAttribute(String dbData) {
    return "Y".equals(dbData);
  }
}
```

```java
@Convert(converter=BooleanToTNConverter.class)
private boolean vip;
```

`@Convert`를 적용하면 데이터베이스에 저장되기 직전에 지정한 컨버터가 동작한다.

컨버터 클래스는 `@Converter`를 사용하고 `AttributeConverter` 인터페이스를 구현해야 한다.

- `convertToDatebaseColumn` : 엔티티의 데이터를 데이터베이스 컬럼에 저장할 데이터로 변환한다.
- `convertToEntityAttribute` : 데이터베이스에서 조회한 컬럼 데이터를 엔티티의 데이터로 변환한다.

컨버터를 클래스 레벨에도 설정할 수 있는데 이때는 `attributeName`속성을 사용해 어떤 필드에 컨버터를 적용할지 명시해야 한다.

```java
@Entity
@Convert(converter=BooleanToTNConverter.class, attributeName="vip")
public class Member {
    
}
```

### 글로벌 설정

모든 `Boolean`타입에 컨버터를 적용하려면 다음과 같이 `@Converter(autoApply = true)` 옵션을 적용하면 된다.

```java
@Converter(autoApply = true)
public class BooleanToYNConverter implements AttributeConverter<Boolean, String> {

  @Override
  public String convertToDatebaseColumn(Boolean attribute) {
    return (attribute != null && attribute) ? "Y" : "N";
  }

  @Override
  public Boolean convertToEntityAttribute(String dbData) {
    return "Y".equals(dbData);
  }
}
```
이렇게하면 따로 엔티티에 `@Convert`를 사용하지 않아도 모든 `Boolean`타입에 자동으로 컨버터가 적용된다.

### 리스너

JPA 리스너 기능을 사용하면 엔티티의 생명주기에 따른 이벤트를 처리할 수 있다.

### 이벤트 종류

- `PostLoad`: 엔티티가 영속성 컨텍스트에 조회된 직후 또는 `refresh`를 호출한 후(2차 캐시에 저장되어 있어도 호출됨)
- `PrePersist`: `persist()`메서드를 호출해 엔티티를 영속성 컨텍스트에 관리하기 직전.
  - 식별자 생성 전략을 사용한 경우 엔티티에 식별자는 아직 존재하지 않는다.
  - 새로운 인스턴스를 `merge`할 때도 수행된다.
- `PreUpdate`: `flush`나 `commit`을 호출해 엔티티를 데이터에 수정하기 직전에 호출
- `PreRemove`: `remove` 메서드를 호출해 엔티티를 영속성 컨텍스트에서 삭제하기 직전에 호출
  - 삭제 명령어로 영속성 전이가 일어날 때도 호출된다.
  - `orphanRemoval`에 대해서는 `flush`나 `commit`시에 호출된다.
- `PostPersist`: `flush`나 `commit`을 호출해서 엔티티를 데이터베이스에 저장한 직후에 호출된다.
  - 식별자가 항상 존재한다.
  - 식별자 생성 전략이 `IDENTITY`면 식별자를 생성하기 위해 `persist()`를 호출하면서 데이터베이스에 저장하므로 이때는 `persist()`를 호출한 직후에 바로 `PostPersist`가 호출된다.
- `PostUpdate`: `flush`나 `commit`을 호출해서 엔티티를 데이터베이스에 수정한 직후에 호출
- `PostRemove`: `flush`나 `commit`을 호출해서 엔티티를 데이터베이스에 삭제한 직후에 호출

### 이벤트 적용 위치

이벤트는 엔티티에서 직접 받거나 별도의 리스너를 등록해서 받을 수 있다.

- 엔티티에 직접 적용

```java
@Entity
public class Duck {
	@Id @GeneratedValue
	private Long id;

	private String name;

	@PrePersist
	public void prePersist() {
		System.out.println("Duck.prePersist id=" + id);
	}

	@PostPersist
	public void postPersist() { 
		System.out.println("Duck.postPersist id=" + id);
	}

	@PostLoad
	public void postLoad() {
		 System.out.println("Duck.postLoad");
	}

	@PreRemove
	public void preRemove() {
		 System.out.println("Duck.preRemove");
	}

	@PostRemove
	public void postRemove() {
		 System.out.println("Duck.postRemove");
	}

}
```
엔티티에 이벤트가 발생할 때마다 어노테이션으로 지정한 메서드가 실행된다.

- 별도의 리스너 등록

```java
@Entity
@EntityListeners(DuckListener.class)
public class Duck {
	...
}

public class DuckListener {

	@PrePersist
	// 특정 타입이 확실하면 특정 타입을 받을 수 있다. 
	private void perPersist(Object obj) {
		System.out.println("DuckListener.prePersist obj = [" + obj + "]");
	}

	@PostPersist
	// 특정 타입이 확실하면 특정 타입을 받을 수 있다. 
	private void postPersist(Object obj) {
		System.out.println("DuckListener.postPersist obj = [" + obj + "]");
	}
}
```
리스너는 대상 엔티티를 파라미터로 받을 수 있다.

반환 타입은 `void`로 설정해야 한다.

- 기본 리스너 사용

모든 엔티티의 이벤트를 처리하려면 `META-INF/orm.xml`에 기본 리스너로 등록하면 된다.

여러 리스너를 등록했을 때 이벤트 호출 순서는 다음과 같다.

1. 기본 리스너
2. 부모 클래스 리스너
3. 리스너
4. 엔티티

### 더 세밀한 설정

더 세밀한 설정을 위한 어노테이션도 있다.

- `javax.persistence.ExcludeDefaultListeners` : 기본 리스너 무시
- `javax.persistence.ExcludeSuperclassListeners` : 상위 클래스 이벤트 리스너 무시

### 엔티티 그래프

엔티티를 조회할 때 연관된 엔티티를 함께 조회하려면 글로벌 `fetch` 옵션을 `fetchType.EAGER`로 설정한다.

글로벌 `fetch` 전략은 애플리케이션 전체에 영향을 주고 변경할 수 없는 단점이 있기 때문에 일반적으로 `fetch` 옵션은 `fetchType.Lazy`를 사용한다.

엔티티를 조회할 때 연관된 엔티티를 함께 조회할 필요가 있으면 JPQL의 페치 조인을 사용한다.

하지만 페치 조인을 사용하면 같은 JPQL을 중복해서 작성하는 경우가 많다.

이는 JPQL이 데이터를 조회하는 기능뿐만 아니라 연관된 엔티티를 함께 조회하는 기능도 제공하기 때문이다.

JPA 2.1에 추가된 엔티티 그래프 기능을 사용하면 엔티티를 조회하는 시점에 함께 조회할 연관된 엔티티를 선택할 수 있다.

따라서 JPQL은 데이터를 조회하는 기능만 수행하면 되고 연관된 엔티티를 함께 조회하는 기능은 엔티티 그래프를 사용하면 된다.

**엔티티 그래프 기능은 엔티티 조회시점에 연관된 엔티티들을 함께 조회하는 기능이다.**

### Named 엔티티 그래프

```java
@NamedEntityGraph(name = "Order.withMember", attributeNodes = {
	@NamedAttributeNode("member")
})
@Entity
@Table(name = "ORDERS")
public class Order {

	@Id @GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchTYpe.LAZY, optional = false)
	@JoinCloumn(name = "MEMBER_ID")
	private Member member;

	...
}
```
Named 엔티티 그래프는 `@NamedEntityGraph`로 정의한다.

- name: 엔티티 그래프의 이름을 정의한다.
- attributeNodes: 함께 조회할 속성을 선택한다.
  - `@NamedAttributeNode`를 사용하고 그 값으로 함께 조회할 속성을 선택하면 된다.

엔티티 그래프에서 함께 조회할 속성으로 `member`를 선택했으므로 이 엔티티 그래프를 사용하면 `Order`를 조회할 때 연관된 `member`도 함께 조회할 수 있다.

둘 이상 정의하려면 `@NamedEntityGraphs`를 사용하면 된다.

### em.find()에서 엔티티 그래프 사용

```java
EntityGraph graph = em.getEntityGraph("Order.withMember");

Map hints = new HashMap();
hints.put("javax.persistence.fetchgraph", graph);

Order order = em.find(Order.class, orderId, hints);
```

Named 엔티티 그래프를 사용하려면 정의한 엔티티 그래프를 `em.getEntityGraph`를 통해서 찾아오면 된다.

엔티티 그래프는 JPA 힌트 기능을 사용해 동작하는데 힌트의 키로 `javax.persistence.fetchgraph`를 사용하고, 값으로 엔티티 그래프를 사용하면 된다.

### subgraph

`Order -> OrderItem -> Item`까지 함께 조회하려고 할 때 

`OrderItem`은 `Order`가 관리하는 필드지만 `OrderItem.Item`은 `Order`가 관리하는 필드가 아니다.

이때는 `subgraph`를 사용하면 된다.

```java
@NamedEntityGraph(name = "Order.withAll", attributeNodes = {
	@NamedAttributeNode("member"),
	@NamedAttributeNode(value = "orderItems", subgraph = "orderItems")
	},
	subgraphs = @NamedSubgraph(name = "orderItems", attributeNodes = {
		@NamedAttributeNode("item")
	})
)
@Entity
@Table(name = "ORDERS")
public class Order {
    
}
```

`subgraph` 속성은 `@NamedSubgraph`를 사용해서 서브 그래프를 정의한다.

### JPQL에서 엔티티 그래프 사용

JPQL에서 엔티티 그래프를 사용하는 방법은 `em.find()`와 동일하게 힌트만 추가하면 된다.

```java
List<Order> resultList =
	em.createQuery("select o from Order o where o.id = :orderId", Order.class)
		.setParameter("orderId", orderId)
		.setHint("javax.persistence.fetchgraph", em.getEntityGraph("Order.withAll"))
		.getResultList();
```

`Order.member`는 필수 관계로 설정되어 있다.

`em.find()`에서 엔티티 그래프를 사용하면 하이버네이트는 필수 관계를 고려해서 SQL 내부 조인을 사용하지만

JPQL에서 엔티티 그래프를 사용할 때는 항상 SQL 외부 조인을 사용한다.

만약 SQL 내부 조인을 사용하려면 다음처럼 내부 조인을 명시하면 된다.

`select o from Order o join fetch o.member where o.id = :orderId`

### 동적 엔티티 그래프

엔티티 그래프를 동적으로 구성하려면 `createEntityGraph()`를 사용하면 된다.

```java
EntityGraph<Order> graph = em.createEntityGraph(Order.class);
graph.addAttributeNodes("member");
Subgraph<OrderItem> orderItems = graph.addSubgraph("orderItems");
orderItems.addAttributeNodes("item");

Map hints = new HashMap();
hints.put("javax.persistence.fetchgraph", graph);

Order order = em.find(Order.class, orderId, hints);
```

`addAttributeNodes`를 사용해 `Order.member` 속성을 엔티티 그래프에 포함했다.

`subgraph` 기능도 동적으로 구성할 수 있다.

### 엔티티 그래프 정리

- Root에서 시작
  - 엔티티 그래프는 항상 조회하는 엔티티의 ROOT에서 시작해야 한다.
- 이미 로딩된 엔티티
  - 영속성 컨텍스트에 해당 엔티티가 이미 로딩되어 있으면 엔티티 그래프가 적용되지 않는다.
- fetchgraph와 laodgraph의 차이
  - `fetchgraph` 힌트는 엔티티 그래프에 선택한 속성만 함께 조회한다.
  - `laodgraph` 힌트는 엔티티 그래프에 선택한 속성뿐만 아니라 글로벌 `fetch` 모드가 `EAGER`로 설정된 연관관계도 포함해서 함께 조회한다. 
