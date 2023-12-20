
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


