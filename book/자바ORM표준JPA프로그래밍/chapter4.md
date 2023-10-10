## 엔티티 매핑

---

JPA를 사용하는 데 가장 중요한 일은 엔티티와 테이블을 정확히 매핑하는 것이다.

JPA는 다양한 매핑 어노테이션을 지원하는데 크게 4가지로 분류할 수 있다.

- 객체와 테이블 매핑 : `@Entity`, `@Table`
- 기본 키 매핑 : `@Id`
- 필드와 컬럼 매핑 : `@Column`
- 연관관계 매핑 : `@ManyToOne`, `@JoinColumn`

### @Entity 

JPA를 사용해서 테이블과 매핑할 클래스는 `@Entity`를 **필수로 붙여야한다.**

`@Entity`가 붙은 클래스는 JPA가 관리하는 것으로 엔티티라 부른다.

`@Entity` 적용시 주의사항은 다음과 같다.

- 기본 생성자는 필수 (파라미터가 없는 `public` 또는 `protected` 생성자)
- `final` 클래스, `enum`, `interface`, `inner` 클래스에는 사용할 수 없다.
- 저장할 필드에 `final`을 사용하면 안 된다.

### @Table

`@Table`은 엔티티와 매핑할 테이블을 지정한다.

기본값은 엔티티 이름을 테이블 이름으로 사용한다.

`@Table`의 속성은 다음과 같다.

- name : 매핑할 테이블 이름
- catelog: catelog 기능이 있는 데이터베이스에서 catelog를 매핑한다.
- schema: schema 기능이 있는 데이터베이스에서 schema를 매핑한다.
- uniqueConstraints: DDL 생성 시에 유니크 제약 조건을 만든다.
  - 2개 이상의 복합 유니크 제약 조건도 만들 수 있다.
  - 스키마 자동 생성 기능을 사용해서 DDL을 만들 때만 사용된다.

### 데이터베이스 스키마 자동 생성

JPA는 데이터베이스 스키마를 자동으로 생성하는 기능을 지원한다.

```java
hibernate.hbm2ddl.auto
```

JPA에 스키마 자동 생성 기능을 설정하면 애플리케이션 실행 시점에 데이터베이스 테이블을 자동으로 생성한다.

자동 생성되는 DDL은 지정한 데이터베이스 방언에 따라 달라진다.

스키마 자동 생성 기능이 만든 DDL은 운영 환경에서 사용할 만큼 완벽하지 않기 때문에 개발 환경에서 참고하는 정도로만 사용하는 것이 좋다.

`hibernate.hbm2ddl.auto` 의 속성은 다음과 같다.

- create: 기존 테이블을 삭제하고 새로 생성한다.
- create-drop: create 속성에 추가로 애플리케이션을 종료할 때 생성한 DDL을 제거한다.
- update: 데이터베이스 테이블과 엔티티 매핑정보를 비교해 변경 사항만 수정한다.
- validate: 데이터베이스 테이블과 엔티티 매핑정보를 비교해 차이가 있으면 경고를 남기고 애플리케이션을 실행하지 않는다. (이 설정은 DDL을 수정하지 않는다.)
- none: 자동 생성 기능을 사용하지 않으려면 속성 자체를 삭제하거나 유효하지 않은 옵션 값을 주면 된다. (none은 유효하지 않은 옵션 값이다.)

### DDL 생성 기능

회원 이름은 필수로 입력되어야 하고, 10자를 초과하면 안된다는 조건을 DDL 자동 생성으로 적용하는 코드는 다음과 같다.

```java
@Entity
@Table(name = "members")
public class Member {
    @Id
    private String id;

    @Column(name = "name", nullable = false, length = 10)
    private String name;
}    
```

`nullable`을 `false`로 지정하면 `not null` 제약 조건을 추가할 수 있다.

```java
@Entity
@Table(name = "members",
uniqueConstraints = {
        @UniqueConstraint(
                name = "NAME_UNIQUE",
                columnNames = {"name"}
        )
})
public class Member {
  public Member() {
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false, length = 10)
  private String name;
}  
```

`@Table`의 `uniqueConstraints` 속성을 사용해 유니크 제약 조건을 추가할 수 있다.

이러한 기능들은 단지 DDL을 자동 생성할 때만 사용되고 **JPA의 실행 로직에는 영향을 주지 않는다.**

JPA 애플리케이션의 실행 동작에는 영향을 주지 않지만 개발자가 엔티티만 보고도 쉽게 지약 조건을 파악할 수 있는 장점이 있다.

### 기본 키 매핑

JPA가 제공하는 데이터베이스 기본 키(primary key) 생성 전략은 다음과 같다.

- 직접 할당: 기본 키를 애플리케이션에서 직접 할당한다.
- 자동 생성: 대리 키 사용 방식
  - `IDENTITY`: 기본 키 생성을 데이터베이스에 위임한다.
  - `SEQUENCE`: 데이터베이스 시퀀스를 사용해서 기본 키를 할당한다.
  - `TABLE`: 키 생성 테이블을 사용한다.

`SEQUENCE`나 `IDENTITY` 전략은 사용하는 데이터베이스에 의존한다.

`TABLE` 전략은 키 생성용 테이블을 하나 만들어두고 시퀀스처럼 사용하는 방법이기 때문에 모든 데이터베이스에서 사용할 수 있다.

기본 키를 직접 할당하려며 `@Id`만 사용하면 되고 자동 생성 전략을 사용하려면

`@Id`에 `@GeneratedValue`를 추가하고 원하는 키 생성 전략을 선택하면 된다.

키 생성 전략을 사용하려면 다음 속성을 반드시 추가해야 한다.

```java
hibernate.id.new_generator_mapping=true
```

### 기본 키 직접 할당 전략

기본 키를 직접 할당하려면 `@Id`로 매핑하면 된다.

```java
@Id
@Column(name = "id")
private String id;
```

@Id 적용 가능 자바 타입은 다음과 같다.

- 자바 기본형
- 자바 래퍼 형
- Stirng
- `java.util.Date`
- `java.sql.Date`
- `java.math.BigDecimal`
- `java.math.BigInteger`

기본 키 직접 할당 전략은 `em.persist()`로 엔티티를 저장하기 전에 애플리케이션에서 기본 키를 직접 할당해야한다.

```java
Board board = new Board();
board.setId("id1");
em.persist(board);
```

기본 키 직접 할당 전략에서는 식별자 값 없이 저장하면 **예외가 발생한다.**

### IDENTITY 전략

`IDENTITY` 전략은 기본 키 생성을 데이터베이스에 위임하는 전략이다.

주로 `MySQL`, `PostgreSQL`, `DB2` 에서 사용한다.

`MySQL`의 `AUTO_INCREMENT` 기능은 데이터베이스가 기본 키를 자동으로 생성해준다.

`IDENTITY` 전략은 **데이터베이스에 값을 저장하고 나서야 기본 키 값을 구할 수 있을 때 사용한다.**

`IDENTITY` 전략을 사용하려면 `@GeneratedValue`의 `strategy` 속성 값을 `GenerationType.IDENTITY`로 지정하면 된다.

이 전략을 사용하면 JPA는 기본 키 값을 얻어오기 위해 데이터베이스를 추가로 조회한다.

`IDENTITY` 전략은 데이터를 데이터베이스에 `INSERT`한 후에 기본 키 값을 조회할 수 있다.

`JDBC3`에 추가된 `Statement.getGeneratedKeys()`를 사용하면 데이터를 저장하면서 동시에 생성된 기본 키 값도 얻어 올 수 있다.

엔티티가 영속 상태가 되려면 식별자가 반드시 필요하기 때문에 `em.persist()`를 호출하는 즉시 `INSERT SQL`이 데이터베이스에 전달된다. 

`IDENTITY` 전략은 **트랜잭션을 지원하는 쓰기 지연이 동작하지 않는다.**

### SEQUENCE 전략

데이터베이스 시퀀스는 유일한 값을 순서대로 생성하는 특별한 데이터베이스 오브젝트다.

`SEQUENCE` 전략은 이 시퀀스를 사용해서 기본 키를 생성한다.

이 전략은 시퀀스를 지원하는 오라클, PostgreSQL, H2 데이터베이스에서 사용할 수 있다.

