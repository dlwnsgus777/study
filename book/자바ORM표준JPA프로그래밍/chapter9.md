
## 값 타입

---

엔티티 타입은 식별자를 통해 지속해서 추적할 수 있지만 값 타입은 식별자가 없고 숫자나 문자같은 속성만 있으므로 추적할 수 없다.

값 타입은 다음 3가지로 나눈다.

- 기본값 타입
  - 자바 기본 타입
  - 래퍼 클래스
- 임베디드 타입 
  - JPA에서 사용자가 직접 정의한 값
- 컬렉션 값 타입

### 기본값 타입

```java
@Entity
public class Member {

  @Id
  private Long id;

  private String username;
}
```

`Member`엔티티의 `username`이 값 타입이다.

식별자도 없고 생명주기도 엔티티에 의존한다.

### 임베디드 타입 (복합 값 타입)

새로운 값 타입을 정의해서 사용할 수 있다.

JPA에서는 **임베디드 타입**이라고 한다.

```java
@Entity
public class Member {
  @Id
  private Long id;
  private String username;

  @Embedded
  Period workPeriod;
}
```

```java
public class Period {

    @Temporal(TemporalType.DATE)
    Date startDate;

    @Temporal(TemporalType.DATE)
    Date endDate;

    public boolean isWork(Date date) {
        // ....
    }
}
```

임베디드 타입을 사용하면 엔티티가 더욱 의미있고 응집력 있게 변한다.

임베디드 타입을 사용하려면 다음 2가지 어노테이션이 필요하다.

둘 중 하나는 생략해도 된다.

- `@Embeddable`: 값 타입을 정의하는 곳에 표시
- `@Embedded`: 값 타입을 사용하는 곳에 표시

임베디드 타입은 엔티티의 생명주기에 의존하므로 컴포지션 관계가 된다.

**임베디드 타입은 값이 속한 엔티티의 테이블에 매팽된다.**

### 임베디드 타입과 연관관계

임베디드 타입은 값 타입을 포함하거나 엔티티를 참조할 수 잇다.

```java
@Entity
public class Member {
  @Id
  private Long id;
  private String username;

  @Embedded
  PhoneNumber phoneNumber;
}
```

```java
@Embeddable
public class PhoneNumber {
    String areaCode;
    String localNumber;
    @ManyToOne 
    PhoneServiceProvider provider;
}

```

### @AttributeOverride: 속성 재정의

임베디드 타입에 정의한 매핑정보를 재정의하려면 엔티티에 `@AttributeOverride` 를 사용하면 된다.

예를들어 회원에게 주소가 하나 더 필요할 때 

```java
@Entity
public class Member {
  @Id
  private Long id;
  private String username;

  @Embedded
  Address homeAddress;

  @Embedded
  Address companyAddress;
}
```

이렇게 작성하면 테이블에 매핑할 때 컬럼명이 중복되게 된다.

```java
@Entity
public class Member {
  @Id
  private Long id;
  private String username;

  @Embedded
  Address homeAddress;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "city", column = @Column(name = "COMPANY_CITY"))
  })
  Address companyAddress;
}

```

위의 코드처럼 `@AttributeOverrides`를 사용해 매핑할 컬럼명을 지정해주면 된다.

### 임베디드 타입과 null

임베디드 타입이 null이면 매핑한 컬럼 값은 모두 null이 된다.

```java
member.setAddress(null);
em.persist(member);
```

### 값 타입과 불변 객체

값 타입은 단순하고 안전하게 다룰 수 있어야 한다.

임베디드 타입 같은 값 타입을 여러 엔티티에서 공유하면 위험하다.

```java
member1.setAddress(new Address("주소"));
Address address = member1.getAddress();

address.setCity("주소2");
member2.setAddress(address);
```

위의 코드대로 되면 회원 2의 주소만 변경되는 것이 아닌 **회원1의 주소도 같이 변경하게 된다.**

이렇게 뭔가를 수정했는데 예상치 못한 곳에서 문제가 발생하는 걸 **부작용**이라고 한다.

이런 부작용을 막으려면 **값을 복사해서 사용해야한다.**

```java
Address a = new Address("A");
Address b = a.clone();

b.setCity(b);
```
이렇게 매번 복사를 하면 **공유 참조**를 피할 수 있지만 **원본 참조 값을 직접 넘기는 것을 막을 방법이 없다**는 게 문제다.

객체의 공유 참조는 피할 수 없다. 따라서 **근본적으로 객체의 값을 수정하지 못하게 막으면 된다.**

### 불변 객체

객체를 불변하게 만들면 값을 수정할 수 없으므로 **부작용**을 원천 차단할 수 있다.

값 타입은 될 수 있으면 **불변 객체**로 설계해야 한다.

참조 값을 공유해도 인스턴스의 값을 수정할 수 없기때문에 부작용이 발생하지 않는다.

### 값 타입의 비교

자바가 제공하는 객체 비교는 2가지다.

- 동일성 비교: 인스턴스의 참조 값을 비교 `==`사용
- 동등성 비교: 인스턴스의 값을 비교 `equals()` 사용

값 타입을 비교할 때는 `equals()`를 사용하므로 `equals()`를 재정의해야 한다.

재정의할 때는 보통 모든 필드의 값을 비교하도록 구현한다.

`equals()` 재정의할 때는 `hashCode()` 메서드도 재정의하는 것이 안전하다.


### 값 타입 컬렉션

값 타입을 하나 이상 저장하려면 컬렉션에 보관하고 `@ElementCollection`, `@CollectionTable` 어노테이션을 사용하면 된다.

```java
@Entity
public class Member {
  @Id
  private Long id;
  private String username;

  @Embedded
  Address homeAddress;

  @ElementCollection
  @CollectionTable(name = "FAVORITE_FOODS", 
    joinColumns = @JoinColumn(name = "MEMBER_ID")
  )
  @Column(name = "FOOD_NAME")
  private set<String> favoriteFoods = new HashSet<String>();
}
```

관계형 데이터베이스의 테이블은 컬럼 안에 컬렉션을 포함할 수 없기 때문에 별도의 테이블을 추가하고 `@CollectionTable` 를 사용해서 추가한 테이블을 매핑해야 한다.

### 값 타입 컬렉션 사용

```java
Member member = new Member();

// 임베디드 값 타입
member.setHomeAddress(new Address("address"));

// 기본 값 타입 컬렉션
member.getFavoriteFoods().add("짬뽕");
member.getFavoriteFoods().add("짜장");

// 임베디드 값 타입 컬렉션
member.getAddressHistory().add(new Address("address1"));

em.persist(member);
```

JPA는 영속화할 때 값 타입도 함께 저장한다.

- member: INSERT SQL 1번
- member.homeAddress: 컬렉션이 아닌 임베디드 값 타입이므로 회원 테이블을 저장하는 SQL에 포함된다.
- member.favoriteFoods: INSERT SQL 3번
- member.addressHistory: INSERT SQL 2번

따라서 `em.persist`한번의 호출로 총 6번의 `ISNERT SQL`이 실행된다.

값 타입 컬렉션은 영속성 전이 + 고아 객체 제거 기능을 필수로 가진다고 볼 수 있다.

값 타입 컬렉션도 조회할 때 페치 전략을 선택할 수 있는데 `LAZY`가 기본이다.

값 타입 컬렉션을 수정할 때는 

- 임베디드 값 타입 수정: 임베디드 값 타입은 매핑한 엔티티의 테이블만 UPDATE한다.
- 기본값 타입 컬렉션 수정: 컬렉션 내의 원소를 제거하고 새로운 원소를 추가해야 한다.
- 임베디드 값 타입 컬렉션 수정: 값 타입은 불변해야 하기 때문에 기존 컬렉션에서 원소를 제거하고 새로 추가해야 한다.

### 값 타입 컬렉션의 제약사항

엔티티는 식별자가 있으므로 값을 변경해도 식별자로 데이터베이스에 저장된 원본 데이터를 찾아 변경할 수 있다.

하지만 값 타입은 **식별자가 없기** 때문에 데이터베이스에 저장된 원본 데이터를 찾기는 어렵다.

특정 엔티티 하나에 소속된 값 타입은 소속된 엔티티를 찾아 값을 변경하면 되지만 컬렉션에 보관된 값들은 **별도의 테이블에 보관**된다.

컬렉션의 값 타입이 변경되면 데이터베이스에서 원본 데이터를 찾기 어렵다.

JPA 구현체들은 값 타입 컬렉션에 변경 사항이 발생하면, 값 타입 컬렉션이 매핑된 테이블의 모든 데이터를 **삭제**하고 현재 컬렉션에 있는 값 타입들의 모든 값을 데이터베이스에 **다시 저장한다.**

따라서 값 타입 컬렉션이 매핑된 테이블에 데이터가 많다면 값 타입 컬렉션 대신 **일대다 관계**를 고려해야 한다.

값 타입 컬렉션을 매핑하는 테이블은 모든 컬럼을 묶어서 기본 키를 구성해야하기 때문에 `null`을 입력할 수 없고, 중복해서 저장할 수 없다는 제약이 있다.

값 타입 컬렉션 대신 새로운 엔티티를 만들어 **일대다 관계**로 설정한 뒤 **영속성 전이 + 고아 객체 제거** 기능을 적용하면 값 타입 컬렉션처럼 사용할 수 있다.




