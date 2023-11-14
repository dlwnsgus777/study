## 다양한 연관관계 매핑

---

엔티티의 연관관계를 매핑할 때는 다음 3가지를 고려해야 한다.

- 다중성
- 단방향, 양방향
- 연관관계의 주인

### 다대일

다대일 관계의 반대 방향은 항상 `일대다` 관계이다.

일대다 관계의 반대 방향은 항상 `다대일` 관계이다.

데이터베이스에서는 항상 `다`쪽이 외래 키를 가지ㅏ고 있다.

객체의 양방향 관계에서 연관관계의 주인은 항상 `다`쪽이다.

회원과 팀이 `다대일` 관계라면 회원 쪽이 연관관계의 주인이다.

### 다대일 단방향 [N:1]

회원과 팀의 `다대일` 단방향 연관관계 예제이다.

```java
@Entity
public class Member {
  
  @Id @GeneratedValue
  @Column(name = "MEMBER_ID")
  private Long id;
  
  private String  username;
  
  @ManyToOne
  @JoinColumn(name = "TEAM_ID")
  private Team team;
}

```

```java
@Entity
public class Team {
  
  @Id @GeneratedValue
  @Column(name = "TEAM_ID")
  private Long id;
  
  private String name;
}
```

회원은 `Member.team` 으로 팀 엔티티를 참조할 수 있다.

반대로 팀에서는 회원을 참조하는 필드가 없다.

회원과 팀은 `다대일 단방향` 연관관계이다.

`@JoinColumn(name = "TEAM_ID")` 을 사용해 `Memeber.team` 필드를 `TEAM_ID` 외래 키와 매핑했다.

`Member.team` 필드로 회원 테이블의 `TEAM_ID` 외래 키를 관리한다.

### 다대일 양방향 [N:1, 1:N]

회원과 팀의 다대일 양방향 관계이다.

```java
@Entity
public class Team {

  @Id
  @GeneratedValue
  @Column(name = "TEAM_ID")
  private Long id;

  private String name;

  @OneToMany(mappedBy = "team")
  private List<Member> members = new ArrayList<>();

  public void addMember(Member member) {
    this.members.add(member);
    if (member.getTeam() != this) {
      member.setTeam(this);
    }
  }

  public List<Member> getMembers() {
    return members;
  }
}
```

```java
@Entity
public class Member {

  @Id
  @GeneratedValue
  @Column(name = "MEMBER_ID")
  private Long id;

  private String  username;

  @ManyToOne
  @JoinColumn(name = "TEAM_ID")
  private Team team;

  public void setTeam(Team team) {
    this.team = team;

    //무한 루프에 빠지지 않도록 체크
    if (!team.getMembers().contains(this)) {
      team.getMembers().add(this);
    }
  }

  public Team getTeam() {
    return team;
  }
}
```

- 양방향은 외래 키가 있는 쪽이 연관관계의 주인이다
  - 일대다와 다대일 연관관계는 항상 `다`쪽에 외래 키가 있다.
  - `Member.team`이 연관관계의 주인이다.
  - `JPA`는 외래 키를 관리할 때 연관관계의 주인만 사용한다.
  - 주인이 아닌 쪽은 조회를 위한 `JPQL`이나 객체 그래프 탐색 때 사용한다.
- 양방향 연관관계는 항상 서로를 참조해야 한다.
  - 양방향 연관관계는 항상 서로 참조해야 한다.
  - 양방향 연관관계에서는 연관관계 편의 메서드를 작성하는 것이 좋다.
    - 양방향 연관관계를 양쪽 모두 작성하게 되면 무한루프에 빠지게 되므로 무한 루프에 빠지지 않도록 검사하는 로직이 필요하다.


### 일대다

일대다 관계는 다대일 관계의 **반대 방향이다.**

엔티티를 하나 이상 참조할 수 있으므로 자바 컬렉션인 

`Collection`, `List`, `Set`, `List` 중 하나를 사용한다.

### 일대다 단방향[1:N]

일대다 단방향 관계는 반대쪽 테이블에 있는 외래 키를 관리하게 된다.

일대다 단방향으로 매핑한 팀과 회원을 살펴본다.

```java
@Entity
public class Member {
  
  @Id @GeneratedValue
  @Column(name = "MEMBER_ID")
  private Long id;
  
  private String username;
}
```

```java
@Entity
public class Team {

  @Id @GeneratedValue
  @Column(name = "TEAM_ID")
  private Long id;

  private String name;

  @OneToMany
  @JoinColumn(name = "TEAM_ID")
  private List<Member> members = new ArrayList<>();

}
```

일대다 단방향 관계를 매핑할 때는 `@JoinColumn`을 명시해야 한다.

그렇지 않으면 JPA는 조인 테이블 전략을 기본으로 사용해 매핑하게 된다. (조인 테이블 전략은 7.4절에서)

**일대다 단뱡향 매핑의 단점**

일대다 단방향 매핑은 매핑한 객체가 관리하는 외래 키가 다른 테이블에 있다는 점이다.

본인 테이블에 외래 키가 있으면 저장과 연관관계 처리를 `INSERT SQL` 한 번으로 끝낼 수 있지만

다른 테이블에 외래 키가 있으면 연관관계 처리를 위한 `UPDATE SQL`을 추가로 실행해야 한다.

```java
public void testSave() {
  Member member1 = new Member("1")
  Member member2 = new Member("1")

  Team team1 = new Team("team1");
  team1.getMembers().add(member1);
  team1.getMembers().add(member2);

  em.persist(member1);
  em.persist(member2);
  em.persist(team1);

  transaction.commit();
}
```

`Member`엔티티를 저장할 때는 `Member`테이블의 외래 키에 아무 값도 저장되지 않는다.

대신 `Team` 엔티티를 저장할 대 `Team.members`의 참조 값을 확인해 회원 테이블의 외래 키를 업데이트 한다.

이러한 점은 성능 문제도 있지만 관리도 부담스럽다.

때문에 **일대다 단방향 보다는 다대일 양방향 매핑**을 권장한다.

### 일대다 양방향[1:N, N:1]

일대다 양방향 매핑은 **존재하지 않는다.**

대신 다대일 양방향 매핑을 사용해야 한다.

정확히는 양방향 매핑에서 `@OneToMany`는 연관관계의 주인이 될 수 없다.

때문에 `@ManyToOne`에는 `mappedBy` **속성이 없다.**

일대다 양방향 매핑은 일대다 단방향 매핑 반대편에 같은 외래 키를 사용하는 `다대일` 단방향 매핑을 **읽기 전용**으로 하나 추가하면 된다.

일대다 양방향 관계를 살펴본다.

```java
@Entity
public class Team {

  @Id @GeneratedValue
  @Column(name = "TEAM_ID")
  private Long id;

  private String name;

  @OneToMany
  @JoinColumn(name = "TEAM_ID")
  private List<Member> members = new ArrayList<>();

}
```

```java
@Entity
public class Member {

  @Id @GeneratedValue
  @Column(name = "MEMBER_ID")
  private Long id;

  private String username;
  
  @ManyToOne
  @JoinColumn(name = "TEAM_ID", insertable = false, updatable = false)
  private Team team;
}
```

일대다 단방향 매핑 반대편에 다대일 단방향 매핑을 추가했다.

서로 같은 외래 키를 관리하므로 문제가 생길 수 있기 때문에

반대편인 다대일 쪽은 `insertable = false`, `updatable = false` 으로 설정해 읽기만 가능하게 한다.

이 방법은 일대다 양방향 매핑이라기 보단 단방향 매핑 반대편에 

다대일 단방향 매핑을 **읽기 전용**으로 추가해 일대다 양방향처럼 보이도록 하는 방법이다.

일대다 단방향 매핑이 가지는 단점을 그대로 가지기 때문에 **될 수 있으면 다대일 양방향 매핑을 사용하자.**

### 일대일[1:1]

일대일 관계는 양쪽이 서로 하나의 관계만 가진다.

일대일 관계는 다음과 같은 특징이 있다.

- 일대일 관계는 그 반대도 일대일 관계다.
- 테이블 관계에서는 항상 `다`쪽이 외래 키를 가진다.
  - 일대일 관게는 주 테이블이나 대상 테이블 둘 중 어느 곳이나 외래 키를 가질 수 있다.
  
### 주 테이블에 외래 키

주 객체가 대상 객체를 참조하는 것처럼 주 테이블에 외래 키를 두고 대상 테이블을 참조한다. 객체지향 개발자들이 선호한다.

주 테이블만 확인해도 대상 테이블과 연관관계가 있는지 알 수 있다는 장점이 있다.

#### 단방향

회원과 사물함의 일대일 단방향 관계를 살펴본다.

```java
@Entity
public class Locker {
  @Id
  @GeneratedValue
  @Column(name = "LOCKER_ID")
  private Long id;
  
  private String name;
}

```

```java
@Entity
public class Member {
  @Id @GeneratedValue
  @Column(name = "MEMBER_ID")
  private Long id;

  private String username;

  @OneToOne
  @JoinColumn(name = "LOCKER_ID")
  private Locker locker;
}
```

일대일 관계이기 때문에 `@OneToOne`을 사용한다.

이 관계는 다대일 단방향 `@ManyToOne`과 거의 비슷하다.

#### 양방향

```java
@Entity
public class Locker {
  @Id
  @GeneratedValue
  @Column(name = "LOCKER_ID")
  private Long id;
  
  @OneToOne(mappedBy = "locker")
  private Member member;
  
  private String name;
}

```

```java
@Entity
public class Member {
  @Id @GeneratedValue
  @Column(name = "MEMBER_ID")
  private Long id;

  private String username;

  @OneToOne
  @JoinColumn(name = "LOCKER_ID")
  private Locker locker;
}
```

양방향이기 때문에 `mappedBy`를 사용해 주인을 정했다.




### 대상 테이블에 외래 키

데이터베이스 개발자들이 선호한다. 테입르 관계를 일대일에서 일대다로 변경할 때 테이블 구조를 그대로 유지할 수 있다는 장점이 있다.

일대일 관계 중 대상 테이블에 외래 키가 있는 `단방향`관계는 JPA에서 지원하지 **않는다.**

비슷하게 할 수 있는 방법이 존재하지만 이런 방법을 사용할 정도면 설계를 다시 고민해봐야 한다.

#### 양방향

대상 테이블에 외래 키가 있는 양방향 관계를 알아본다.

```java
@Entity
public class Locker {
  @Id
  @GeneratedValue
  @Column(name = "LOCKER_ID")
  private Long id;

  @OneToOne
  @JoinColumn(name = "MEMBER_ID")
  private Member member;

  private String name;
}

```

```java
@Entity
public class Member {
  @Id @GeneratedValue
  @Column(name = "MEMBER_ID")
  private Long id;

  private String username;

  @OneToOne(mappedBy = "member")
  private Locker locker;
}
```

프록시를 사용할 때 외래 키를 직접 관리하지 않는 일대일 관계는 **지연 로딩으로 설정해도 즉시 로딩된다.**

위의 코드에서 `Member.locker`는 즉시 로딩된다.

이는 프록시의 한계 떄문에 발생하는 문제인데 프록시 대신 `bytecode instrumentation`을 사용하면 해결할 수 있다.

### 다대다[N:N]

관계형 데이터베이스는 정구화된 테이블 2개로 다대다 관계를 표현할 수 **없다.**

다대다 관계를 일대다, 다대일 관계로 풀어내는 연결 테이블을 사용한다.

객체는 객체 2개로 다대다 관계를 만들 수 있다.

`@ManyToMany`를 사용하면 다대다 관계를 편리하게 매핑할 수 있다.

### 다대다: 단방향

다대다 단방향 관계인 회원과 상품 엔티티를 살펴본다.

```java
@Entity
public class Member {
  @Id @Column(name = "MEMBER_ID")
  private String id;
  
  private String username;
  
  @ManyToMany
  @JoinTable(name = "MEMBER_PRODUCT", 
      joinColumns = @JoinColumn(name = "MEMBER_ID"), 
      inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID")
  )
  private List<Product> products = new ArrayList<Product>();
  
}
```

```java
@Entity
public class Product {
  @Id @Column(name = "PRODUCT_ID")
  private String id;

  private String name;
}
```

회원 엔티티와 상품 엔티티를 `@ManyToMany`로 매핑했다.

`@JoinTable` 을 사용해 연결 테이블을 바로 매핑했기 때문에 `회원_상품` 엔티티 없이 매핑을 완료할 수 있다.

`@JoinTable`의 속성은 다음과 같다.

- `@JoinTable.name`: 연결 테이블을 지정한다.
- `@JoinTable.joinColumns`: 현재 방향인 회원과 매핑할 조인 컬럼 정보를 지정한다.
- `@JoinTable.inverseJoinColumns`: 반대 방향인 상품과 매핑할 조인 컬럼 정보를 지정한다.

`MEMBER_PRODUCT`테이블은 대다대 관계를 `일대다`, `다대일` 관계로 풀어내기 위해 필요한 연결 테이블일 뿐이다.

실행된 SQL을 살펴보면 `MEMBER_PRODUCT`와 상품 테이블을 조인해서 연관된 상품을 조회한다.

### 다대다: 양방향

다대다 매핑이므로 역방향도 `@ManyToMany`를 사용한다.

그리고 양쪽 중 원하는 곳에 `mappedBy`로 연관관계의 주인을 지정한다.

```java
@Entity
public class Product {
  @Id @Column(name = "PRODUCT_ID")
  private String id;

  
  @ManyToMany(mappedBy = "products")
  private List<Member> members;
  
  private String name;
}
```

양방향 연관관계는 연관관계 편의 메서드를 추가해 관리하는 것이 편하다.

### 다대다: 매핑의 한계와 극복, 연결 엔티티 사용

`@ManyToMany`를 사용하면 연결 테이블을 자동으로 처리해주므로 도메인 모델이 단순해지고 여러가지로 편리하다.

하지만 실무에 사용하기에는 한계가 있는데 연결 테이블에 다른 컬럼을 추가하게 되면 `@ManyToMany`를 사용할 수 없기 때문이다.

결국 연결 테이블을 매핑하는 엔티티를 만들고 엔티티의 관계도 `다대다`에서 `일대다`, `다대일` 관계로 풀어야 한다.

```java
public class MemberProductId implements Serializable {
  private String member;
  private String product;

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final MemberProductId that = (MemberProductId) o;
    return Objects.equals(member, that.member) && Objects.equals(product, that.product);
  }

  @Override
  public int hashCode() {
    return Objects.hash(member, product);
  }
}
```

```java
@Entity
@IdClass(MemberProductId.class)
public class MemberProduct {

  @Id
  @ManyToOne
  @JoinColumn(name = "MEMBER_ID")
  private Member member;

  @Id
  @ManyToOne
  @JoinColumn(name = "PRODUCT_ID")
  private Product product;
}
```

```java
@Entity
public class Member {
  @Id @Column(name = "MEMBER_ID")
  private String id;

  private String username;

  @OneToMany(mappedBy = "member")
  private List<MemberProduct> memberProducts;

}
```

```java
@Entity
public class Product {
  @Id @Column(name = "PRODUCT_ID")
  private String id;
}
```

회원과 회원상품을 양방향 관계로 만들었다.

상품 엔티티에서 회원상품 엔티티로 객체 그래프 탐색 기능이 필요하지 않다고 판단해 연관관계를 만들지 않았다.

회원상품 엔티티는 기본 키를 매핑하는 `@Id`와 외래 키를 매핑하는 `@JoinColumn`을 동시해 사용해 `기본 키 + 외래 키`를 한번에 매핑했다.

`@IdClass`를 사용해 복합 기본 키를 매핑했다.

### 복합 기본 키

JPA에서는 복합 키를 사용하기 위해 별도의 식별자 클래스를 만들어야 한다.

그리고 엔티티에 `@IdClass`를 사용해 식별자 클래스를 지정한다.

복합 키를 위한 식별자 클래스는 다음과 같은 특징이 있다.

- 복합 키는 별도의 식별자 클래스로 만들어야 한다.
- `Serializable`을 구현해야 한다.
- `equals와 hashCode`메서드를 구현해야 한다.
- 기본 생성자가 있어야 한다.
- 식별자 클래스는 `public`이어야 한다.
- `@IdClass`외에 `@EmbeddedId`를 사용하는 방법도 있다.

### 식별 관계

부모 테이블의 기본 키를 받아 자신의 기본 키 + 외래 키로 사용하는 것을 **식별 관계**라 한다.

회원 상품 엔티티는 데이터베이스에 저장될 때 연관된 회원과 상품의 식별자를 가져와 자신의 기본 키 값으로 사용한다.

복합 키는 항상 식별자 클래스를 만들어야 한다.

### 다대다: 새로운 기본 키 사용

기본 키 생성 전략은 데이터베이스에서 자동으로 생성해주는 대리 키를 `Long`값으로 사용하는 것을 추천한다.

이 방법이 ORM 매핑 시에 복합 키를 만들지 않아도 되므로 간단히 매핑을 완성할 수 있다.

```java
@Entity
public class Order {
  @Id @GeneratedValue
  @Column(name = "ORDER_ID")
  private Long id;
  
  @ManyToOne
  @JoinColumn(name = "MEMBER_ID")
  private Member member;

  @ManyToOne
  @JoinColumn(name = "PRODUCT_ID")
  private Product product;
}
```

```java
@Entity
public class Member {
  @Id @Column(name = "MEMBER_ID")
  private String id;

  private String username;

  @OneToMany(mappedBy = "member")
  private List<Order> orders = new ArrayList<>(); 
}
```

```java
@Entity
public class Member {
  @Id @Column(name = "MEMBER_ID")
  private String id;

  private String username;

  @OneToMany(mappedBy = "member")
  private List<Order> orders = new ArrayList<>(); 
}
```

```java
@Entity
public class Product {
  @Id @Column(name = "PRODUCT_ID")
  private String id;
  private String name;
}

```

대리 키를 사용함으로써 식별 관계에 복합 키를 사용하는 것보다 매핑이 단순하고 이해하기 쉬워졌다.

이렇게 새로운 기본 키를 사용해 다대다 관계를 풀어내는 것도 좋은 방법이다.

### 다대다 연관관계 정리

다대다 관계를 일대다 다대일 관계로 풀어내기 위해 연결 테이블을 만들 때 식별자를 어떻게 구성할지 선택해야 한다.

- 식별 관계: 받아온 식별자를 기본 키 + 외래 키로 사용한다.
- 비식별 관계: 받아온 식별자는 외래 키로만 사용하고 새로운 식별자를 추가한다.

데이터베이스 설계에서는 기본 키 + 외래 키로 사용하는 것을 식별 관계라 하고, 외래 키로만 사용하는 것을 비식별 관계라고 한다.

객체 입장에서는 비식별 관계를 사용하는 것이 단순하고 편리하게 ORM 매핑을 할 수 있다.

이러한 이유로 식별 관계보다는 비식별 관계를 추천한다.
