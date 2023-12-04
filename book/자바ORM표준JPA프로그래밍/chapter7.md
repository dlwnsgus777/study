## 고급 매핑

---

### 상속 관계 매핑

관계형 데이터베이스에는 객체지향 언어에서 다루는 상속이라는 개념이 없다.

대신 `슈퍼타입 서브타입`관계라는 모델링 기법이 가장 유사하다.

ORM에서 말하는 상속 관계 매핑은 객체의 상속 구조와 데이터베이스의 슈퍼타입 서브타입 관계를 매핑하는 것이다.

`슈퍼타입 서브타입`논리 모델을 구현할 때는 3가지 방법을 선택할 수 있다.

- 각각의 테이블로 변환: 각각을 모두 테이블로 만들고 조회할 때 조인을 사용한다.(JPA 에서는 조인 전략)
- 통합 테이블로 변환: 테이블을 하나만 사용해서 통합한다. (JPA에서는 단일 테이블 전략)
- 서브타입 테이블로 변환: 서브 타입마다 하나의 테이블을 만든다. (JPA에서는 구현 클래스마다 테이블 전략)

### 조인 전략

조인 전략은 엔티티 각각을 모두 **테이블**로 만들고 자식 테이블이 부모 테이블의 기본 키를 받아서 `기본 키 + 외래 키`로 사용하는 전략이다.

조회할 떄 조인을 자주 사용한다.

객체는 타입으로 구분할 수 있지만 테이블은 타입의 개념이 없기 때문에 타입을 구분하는 컬럼을 추가해야 한다.

```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE")
public abstract class Item {
  @Id @GeneratedValue
  @Column(name = "ITEM_ID")
  private Long id;

  private String name;
  private int price;
}

@Entity
@DiscriminatorValue("A")
public class Album extends Item{
  private String artist;
}

@Entity
@DiscriminatorValue("M")
public class Movie extends Item{
  private String director;
  private String actor;
}
```
- `@Inheritance(strategy = InheritanceType.JOINED)`: 상속 매핑은 부모 클래스에 `@Inheritance`를 사용해야 한다. 속성 값으로 매핑 전략을 지정한다.
- `@DiscriminatorColumn(name = "DTYPE")`: 부모 클래스에 구분 컬럼을 지정한다. 이 컬럼으로 지정된 자식 테이블을 구분한다.
- `@DiscriminatorValue("A")`: 엔티티를 저장할 때 구분 컬럼에 입력할 값을 지정한다.

기본 값으로 자식 테이블은 부모 테이블의 ID 컬럼명을 그대로 사용하는데 자식 테이블의 기본 키 컬럼명을 변경하고 싶으면 `@PrimaryKeyJoinColumn`을 사용하여 변경한다.

- 장점: 
  - 테이블이 정규화된다.
  - 외래 키 참조 무결성 제약조건을 활용할 수 있다.
  - 저장공간을 효율적으로 사용한다.
- 단점:
  - 조회할 때 조인이 많이 사용되므로 성능이 저하될 수 있다.
  - 조회 쿼리가 복잡하다.
  - 데이터를 등록할 때 INSERT SQL을 두 번 실행한다.
- 특징:
  - 하이버네이트를 포함한 몇몇 구현체는 구분 컬럼 없이도 동작한다.


### 단일 테이블 전략

단일 테이블 전략은 테이블을 하나만 사용한다.

구분 컬럼으로 어떤 자식 데이터가 저장되었는지 구분한다.

조회할 때 조인을 사용하지 않으므로 일반적으로 가장 빠르다.

주의점은 자식 엔티티가 매핑한 컬럼 모두 `null`을 허용해야 한다.

```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE")
public abstract class Item {
  @Id @GeneratedValue
  @Column(name = "ITEM_ID")
  private Long id;

  private String name;
  private int price;
}

@Entity
@DiscriminatorValue("M")
public class Movie extends Item {
  private String director;
  private String actor;
}

@Entity
@DiscriminatorValue("A")
public class Album extends Item{
  private String artist;
}
```

`@Inheritance(strategy = InheritanceType.SINGLE_TABLE)`로 설정하면 단일 테이블 전략을 사용한다.

- 장점:
  - 조인이 필요 없으므로 일반적으로 조회 성능이 빠르다.
  - 조회 쿼리가 단순하다.
- 단점:
  - 자식 엔티티가 매핑한 컬럼은 모두 `null`을 허용해야 한다.
  - 단일 테이블에 모든 것을 저장하기 때문에 테이블이 커질 수 있다. 상황에 따라서는 조회 성능이 오히려 느릴 수 있다.
- 특징:
  - 구분 컬럼을 꼭 사용해야 한다.
  - `@DiscriminatorValue`를 지정하지 않으면 기본으로 엔티티 이름을 사용한다.

### 구현 클래스마다 테이블 전략

자식 엔티티마다 테이블을 만드는 전략이다.

```java
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Item {
  @Id @GeneratedValue
  @Column(name = "ITEM_ID")
  private Long id;

  private String name;
  private int price;
}

@Entity
public class Movie extends Item {
  private String director;
  private String actor;
}

@Entity
public class Album extends Item{
  private String artist;
}
```

일반적으로 추천하지 않는 전략이다.

- 장점:
  - 서브 타입을 구분해서 처리할 때 효과적이다.
  - `not null` 제약조건을 사용할 수 있다.
- 단점:
  - 여러 자식 테이블을 함께 조회할 때 성능이 느리다.
  - 자식 테이블을 통합해서 쿼리하기 어렵다.
- 특징:
  - 구분 컬럼을 사용하지 않는다.

이 전략은 데이터베이스 설계자와 ORM 전문가 둘 다 추천하지 않으므로 조인이나 단일 테이블 전략을 고려하자.

### @MappedSuperclass

부모 클래스는 테이블과 매핑하지 **않고** 부모 클래스를 상속 받는 자식 클래스에게 매핑 정보만 제공하고 싶으면 `@MappedSuperclass`를 사용하면 된다.

`@MappedSuperclass`는 추상 클래스와 비슷한데 `@Entity`는 실제 테이블과 매핑되지만 `@MappedSuperclass`는 실제 테이블과 매핑되지 않는다.

단순히 매핑 정보를 상속할 목적으로만 사용된다.

```java
@MappedSuperclass
public abstract class BaseEntity {
  @Id @GeneratedValue
  private Long id;
  private String name;
}

@Entity
public class Member extends BaseEntity{
  
  private String email;
}
```

`BaseEntity`에는 객체들이 주로 사용하는 공통 매핑 정보를 정의한다.

부모로부터 물려받은 매핑 정보를 재정의하려면 `@AttributeOverrides`나 `@AttributeOverride`를 사용하면 된다.

연관관계를 재정의하려면 `@AssociationOverrides`나 `@AssociationOverride`를 사용하면 된다.

`@MappedSuperclass` 특징은 다음과 같다.

- 테이블과 매핑되지 않고 자식 클래스에 엔티티 매핑 정보를 상속하기 위해 사용한다.
- `@MappedSuperclass`로 지정한 클래스는 엔티티가 아니다.
  - `em.find()`나 `JPQL`에서 사용할 수 없다.
- 이 클래스를 직접 생성해 사용할 일은 거의 없으므로 **추상 클래스**로 만드는 걸 권장한다.

엔티티는 엔티티이거나 `@MappedSuperclass`로 지정한 클래스만 상속받을 수 있다.

### 복합 키와 식별 관계 매핑

데이터베이스 테이블 사이에 관계는 외래 키가 기본 키에 포함되는지 여부에 따라 **식별 관계**와 **비식별 관계**로 구분한다.

### 식별 관계

식별 관계는 부모 테이블의 기본 키를 내려받아서 자식 테이블의 기본 키 + 외래 키로 사용하는 관계이다.

### 비식별 관계

비식별 관계는 부모 테이블의 기본 키를 받아서 자식 테이블의 외래 키로만 사용하는 관계이다.

비식별 관계는 외래 키에 `NULL`을 허용하지는지에 따라 필수적 비식별 관계와 선택적 비식별 관계로 나눈다.

- 필수적 비식별 관계 : 외래 키에 `NULL`을 허용하지 않는다. 연관관계를 필수적으로 맺어야 한다.
- 선택적 비식별 관계 : 외래 키에 `NULL`을 허용한다. 연관관계를 맺을지 말지 선택할 수 있다.

최근에는 비식별 관계를 주로 사용하고 꼭 필요한 곳에만 식별 관계를 사용하는 추세다.

### 복합 키: 비식별 관계 매핑

둘 이상의 컬럼으로 구성된 복합 기본 키는 별도의 식별자 클래스를 만들어야 한다.

```java
@Entity
public class Hello {
  @Id 
  private String id1;

  @Id
  private String id2;
}
```

JPA는 식별자를 구분하기 위해 `equals`와 `hashCode`를 사용해서 동등성 비교를 한다.

JPA는 복합키를 지원하기 위해 `@IdClass`와 `EmbeddedId` 2가지 방법을 제공한다.

`@IdClass` 

```java
@Entity
@IdClass(ParentId.class)
public class Parent {
  @Id
  @Column(name = "PARENT_ID1")
  private String id1;

  @Id
  @Column(name = "PARENT_ID2")
  private String id2;
}
```

```java
public class ParentId implements Serializable {
  private String id;
  private String id2;

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final ParentId parentId = (ParentId) o;

    if (!Objects.equals(id, parentId.id)) return false;
    return Objects.equals(id2, parentId.id2);
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (id2 != null ? id2.hashCode() : 0);
    return result;
  }
}
```

`@IdClass`를 사용해 `ParentId`클래스를 식별자 클래스로 지정하였다.


`@IdClass`를 사용할 때 식별자 클래스는 다음 조건을 만족해야 한다.

- 식별자 클래스의 속성명과 엔티티에서 사용하는 식별자의 속성명이 같아야 한다.
  - Parent.id1과 ParentId.id1이 같다.
- `Serializable`인터페이스를 구현해야 한다.
- `equals`,  `hashCode`를 구현해야 한다.
- 기본 생성자가 있어야 한다.
- public 이어야 한다.

복합키로 조회하는 예제 코드를 살펴본다.

```java
@Entity
public class Child {
  
  @Id
  private String id;
  
  @ManyToOne
  @JoinColumns({
          @JoinColumn(name = "PARENT_ID1", referencedColumnName = "PARENT_ID1"),
          @JoinColumn(name = "PARENT_ID2", referencedColumnName = "PARENT_ID2"),
  })
  private Parent parent;
}
```

부모 테이블의 기본 키 컬럼이 복합 키이므로 자식 테이블의 외래 키도 복합 키다.

`@EmbeddedId`

`@EmbeddedId`는 좀 더 객체지향적인 방법이다.

```java
@Entity
public class Parent {
  
  @EmbeddedId
  private ParentId id;

  private String name;
}
```

```java
@Embeddable
public class ParentId implements Serializable {
  
  @Column(name = "PARENT_ID1")
  private String id;

  @Column(name = "PARENT_ID2")
  private String id2;

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final ParentId parentId = (ParentId) o;

    if (!Objects.equals(id, parentId.id)) return false;
    return Objects.equals(id2, parentId.id2);
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (id2 != null ? id2.hashCode() : 0);
    return result;
  }
}
```

식별자 클래스에는 `@Embeddable`을 붙인다.

`@EmbeddedId`를 적용한 식별자 클래스는 식별자 클래스에 기본 키를 직접 매핑한다.

`@EmbeddedId`를 적용한 식별자 클래스는 다음 조건을 만족해야 한다.

- `@Embeddable` 어노테이션을 붙여주어야 한다.
- `Serializable`인터페이스를 구현해야 한다.
- `equals`,  `hashCode`를 구현해야 한다.
- 기본 생성자가 있어야 한다.
- public 이어야 한다.


### 복합 키와 equals(), hashCode()

복합 키는 `equals`,  `hashCode`를 필수로 구현해야 한다.

영속성 컨텍스트는 엔티티의 식별자를 키로 사용해 엔티티를 관리한다.

식별자를 비교할 때 equals(), hashCode()를 사용하기 때문에 식별자 객체의 동등성이 지켜지지 않으면 예상과 다른 엔티티가 조회되거나 엔티티를 찾을 수 없는 등 영속성 컨텍스트가 엔티티를 관리하는데 심각한 문제가 발생한다.

식별자 클래스는 보통 equals(), hashCode() 구현할 떄 **모든 필드를 사용한다.**

### @IdClass VS @EmbeddedId

`@IdClass` 와 `@EmbeddedId`는 각각의 장단점이 있으므로 본인의 취향에 맞는 것을 일관성 있게 사용하면 된다.

`@EmbeddedId` 가 더 객체지향적이고 중복도 없어서 좋아보이지만 특정 상황에 `JPQL`이 조금 더 길어질 수 있다.

### 복합 키 : 식별 관계 매핑

부모, 자식, 손자까지 계속 기본 키를 전달하는 식별 관계다.

자식 테이블은 부모 테이블의 기본 키를 포함해서 복합 키를 구성해야 하므로 `@IdClass` 와 `@EmbeddedId`를 사용해 식별자를 매핑해야 한다.

### `@IdClass`와 식별 관계

```java
@Entity
public class Parent {
  
  @Id @Column(name = "PARENT_ID")
  private String id;
  private String name;
}
```

```java
@Entity
@IdClass(ChildId.class)
public class Child {
  @Id
  @ManyToOne
  @JoinColumn(name = "PARENT_ID")
  public Parent parent;

  @Id
  @Column(name = "CHILD_ID")
  private String childId;
}

```

```java
public class ChildId implements Serializable {
  private String parent;
  private String childId;


  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final ChildId childId1 = (ChildId) o;

    if (!Objects.equals(parent, childId1.parent)) return false;
    return Objects.equals(childId, childId1.childId);
  }

  @Override
  public int hashCode() {
    int result = parent != null ? parent.hashCode() : 0;
    result = 31 * result + (childId != null ? childId.hashCode() : 0);
    return result;
  }
}
```


```java
@Entity
@IdClass(GrandChildId.class)
public class GrandChild {

  @Id
  @ManyToOne
  @JoinColumns({
          @JoinColumn(name = "PARENT_ID"),
          @JoinColumn(name = "CHILD_ID")
  })
  private Child child;

  @Id @Column(name = "GRANDCHILD_ID")
  private String id;
}
```


```java
public class GrandChildId implements Serializable {
  private ChildId child;
  private String id;

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final GrandChildId that = (GrandChildId) o;

    if (!Objects.equals(child, that.child)) return false;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    int result = child != null ? child.hashCode() : 0;
    result = 31 * result + (id != null ? id.hashCode() : 0);
    return result;
  }
}
```

### @EmbeddedId와 식별 관계












