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

```java
@Entity
@SequenceGenerator(
        name = "BOARD_SEQ_GENERATOR",
        sequenceName = "BOARD_SEQ",
        initialValue = 1, allocationSize = 1
)
public class Board {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE,
  generator = "BOARD_SEQ_GENERATOR")
  private Long id;
  
  public Board() {
  }
}
```

`@SequenceGenerator`를 사용해 시퀀스 생성기를 등록한다.

`sequenceName` 속성의 이름으로 JPA는 시퀀스 생성기를 실제 데이터베이스의 시퀀스와 매핑한다.

`SEQUENCE` 전략은 `em.persist()`를 호출할 때 먼저 데이터베이스 시퀀스를 사용해서 식별자를 조회한다.

조회한 식별자를 엔티티에 할당한 후에 엔티티를 영속성 컨텍스트에 저장하고 트랜잭션을 커밋해서 플러시가 일어나면 엔티티를 데이터베이스에 저장한다.


### @SequenceGenerator

@SequenceGenerator의 속성은 다음과 같다.

- name: 식별자 생성기 이름
- sequenceName: 데이터베이스에 등록되어 있는 시퀀스 이름
- initialValue: DDL 생성시에만 사용됨, 시퀀스 DDL을 생성할 때 처음 시작하는 수 
- allocationSize: 시퀀스 한 번 호출에 증가하는 수(기본값 50)
- catelog, schema: 데이터베이스 catelog, schema 이름

`SEQUENCE` 전략은 데이터베이스 시퀀스를 통해 식별자를 조회하는 추가 작업이 필요하다.

때문에 데이터베이스와 2번 통신하게 된다.

1. 식별자를 구하려고 데이터베이스 시퀀스를 조회한다.
2. 조회한 시퀀스를 기본 키 값으로 사용해 데이터베이스에 저장한다.

JPA에서는 시퀀스 접근을 최소화하기 위해 `allocationSize`를 사용한다.

`allocationSize`값이 50이면 시퀀스를 한 번에 50 증가 시키고 1 ~ 50까지는 메모리에서 식별자를 할당한다.

이 방법은 JVM이 여러 대 동작해도 기본 키 값이 충돌하지 않는다는 장점이 있다.

하지만 데이터베이스에 직접 접근해 데이터를 등록할 때 시퀀스 값이 한 번에 많이 증가한다는 점에 주의해야한다.


현재 까지의 최적화 방법은 `hibernate.id.new_generator_mapping=true` 으로 설정되어 있어야 동작한다.

`@SequenceGenerator`는 `@GeneratedValue` 옆에 사용해도 된다.

### TABLE 전략

`TABLE` 전략은 키 생성 전용 테이블을 하나 만들고 이름과 값으로 사용할 컬럼을 만들어 데이터베이스 시퀀스를 **흉내내는 전략**이다.

이 전략은 모든 데이터베이스에 적용할 수 있다.

`TABLE` 전략을 사용하려면 키 생성 용도로 사용할 테이블을 만들어야 한다.

```java
@Entity
@TableGenerator(
        name = "BOARD_SEQ_GENERATOR",
        table = "MY_SEQUENCES",
        pkColumnValue = "BOARD_SEQ", allocationSize = 1
)
public class Board {

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE,
          generator = "BOARD_SEQ_GENERATOR")
  private Long id;

  public Board() {
  }
}
```

위처럼 작성하면 `Id` 식별자 값은 `BOARD_SEQ_GENERATOR` 테이블 키 생성기가 할당된다.

`TABLE` 전략은 시퀀스 대신에 테이블을 사용한다는 것만 제외하면 `SEQUENCE` 전략과 내부 동작방식이 같다.

시퀀스 용 테이블에 값이 없으면 JPA 가 `INSERT`하면서 초기화하므로 값을 미리 넣어둘 필요는 없다.

### @TableGenerator

`@TableGenerator`의 속성은 다음과 같다.

- name: 식별자 생성기 이름
- table: 키 생성 테이블명
- pkColumnName: 시퀀스 컬럼 명
- valueColumnName: 시퀀스 값 컬럼명
- pkColumnValue: 키로 사용할 값 이름
- initialValue: 초기 값, 마지막으로 생성된 값이 기준이다.
- allocationSize: 시퀀스 한 번 호출에 증가하는 수(기본값 50)
- catelog, schema: 데이터베이스 catelog, schema 이름
- uniqueConstraints(DDL): 유니크 제약 조건을 지정할 수 있다.

`TABLE` 전략은 값을 조회하면서 `SELECT` 쿼리르 사용하고 다음으로 값을 증가시키기 위해 `UPDATE` 쿼리를 사용한다.

최적화하는 방법은 `SEQUENCE` 전략과 동일하다.

### AUTO 전략

`AUTO`는 선택한 데이터베이스 방언에 따라 `IDENTITY`, `SEQUENCE`, `TABLE` 전략 중 하나를 자동으로 선택한다.

오라클이라면 `SEQUENCE`를 MySQL이라면 `IDENTITY`를 사용한다.

`AUTO` 전략의 장점은 데이터베이스를 변경해도 코드를 수정할 필요가 없다는 것이다.

`AUTO`를 사용할 때 `SEQUENCE`, `TABLE` 전략이 선택되면 시퀀스나 키 생성용 테이블을 미리 만들어두어야 한다.

스키마 자동 생성 기능을 사용한다면 하이버네이트가 기본값을 사용해 적절한 테이블을 만들어 준다.

### 기본 키 매핑 정리

데이터베이스의 기본 키는 다음 3가지 조건을 모두 만족해야 한다.

- `null`값은 허용하지 않는다.
- 유일해야 한다.
- 변해선 안 된다.

테이블의 기본 키를 선택하는 전략은 크게 2가지가 있다.

- 자연 키
  - 비즈니스에 의미가 있는키
  - 주민등록번호, 이메일, 전화번호
- 대리 키
  - 비즈니스와 관련 없는 임의로 만들어진 키(대체 키)
  - 오라클 시퀀스, auto_increment, 키 생성 테이블

비즈니스 환경은 언젠가 변하기 때문에 **자연 키보다는 대리 키를 권장**한다.

기본 키는 변하면 안 된다는 기본 원칙으로 인해 기본 키 값을 변경하려고 하면 JPA에서 예외를 발생시키거나 정상 동작하지 않는다.

### 필드와 컬럼 매핑: 레퍼런스

### @Column

`@Column`은 객체 필드를 테이블 컬럼에 매핑한다.

속성 중 `name`과 `nullable`이 주로 사용된다.

`@Column`을 생략하게 되면 기본 값이 적용되는데 자바 기본 타입일 때는 `nullable` 속성에 예외가 있다.

```java
int data1 // @Column 생략, 자바 기본 타입
data1 integer not null // 생성된 DDL

Integer data2 // @Column 생략, 객체 타입
data2 integer // 생성된 DDL

@Column
int data3 // @Column 사용, 자바 기본 타입
data3 integer // 생성된 DDL
```

JPA에서는 DDL 생성 기능을 사용할 때 `int data1` 같은 기본 타입에는 `not null` 제약조건을 추가한다.

`Integer data2` 처럼 객체 타입이면 `null`이 입력될 수 있으므로 `not null` 제약조건을 설정하지 않는다.

`int data3` 처럼 `@Column`을 사용하면 `@Column`은 `nullable = true`가 기본 값이므로 `not null` 제약조건을 설정하지 않는다.

자바 기본 타입에 `@Column`을 사용하면 `nullable = false`로 지정하는 것이 안전하다.

### @Enumerated

자바의 `enum` 타입을 매핑할 때 사용한다.

`@Enumerated`의 속성은 다음과 같다.

- value: 
  - `EnumType.ORDINAL`: `enum` 순서를 데이터베이스에 저장
  - `EnumType.STRING`: `enum` 이름을 데이터베이스에 저장


`EnumType.ORDINAL`은 `enum`에 정의된 순서대로 데이터베이스에 저장한다.
- 장점: 데이터베이스에 저장되는 크기가 작다.
- 단점: 이미 저장된 `enum`의 순서를 변경할 수 없다.

`EnumType.STRING`은 `enum`이름 그대로 문자를 데이터베이스에 저장한다.
- 장점: 저장된 `enum`의 순서가 바뀌거나 `enum`이 추가되어도 안전하다.
- 단점: 데이터베이스에 저장되는 데이터 크기가 `ORDINAL`에 비해서 크다.

`EnumType.STRING`을 사용하는 것을 권장한다.

### @Temporal

날짜 타입을 매핑할 때 사용한다.

속성은 다음과 같다.

- value: 
  - `TemporalType.DATE`: 날짜, 데이터베이스 `data`타입과 매핑 (예ㅣ 2013-10-11)
  - `TemporalType.TIME`: 시간, 데이터베이스 `time`타입과 매핑 (예ㅣ 11:11:11)
  - `TemporalType.TIMESTAMP`: 날짜와 시간, 데이터베이스 `timestamp`타입과 매핑 (예ㅣ 2013-10-11 11:11:11)

`@Temporal`을 생략하면 자바의 `Date`와 가장 유사한 `timestamp`로 정의된다.

`timestamp` 대신 `datetime`을 예약어로 사용하는 데이터베이스도 있는데 방언 덕분에 애플리케이션 코드는 변경하지 않아도 된다.

### @Lob

데이터베이스 `BLOB`과 `CLOB` 타입과 매핑한다.

`@Lob` 에는 지정할 수 있는 속성이 없다. 

대신 매핑하는 필드 타입이 문자면 `CLOB` 으로 매핑하고 나머지는 `BLOB` 으로 매핑한다.

- CLOB: `String`, `char[]`, `java.sql.CLOB`
- BLOB: `byte[]`, `java.sql.BLOB`

### @Transient

`@Transient`이 붙은 필드는 매핑하지 않는다.

### @Access

`@Access`은 JPA가 엔티티 데이터에 접근하는 방식을 지정한다.

- 필드 접근: `AccessType.FIELD`로 지정한다.
  - 필드에 직접 접근한다.
  - `privatge`권한이어도 접근할 수 있다.
- 프로퍼티 접근: `AccessType.PROPERTY`로 지정한다.
  - 접근자 `getter`를 사용한다.

`@Access`를 설정하지 않으면 `@Id`의 위치를 기준으로 접근 방식이 설정된다.

```java
@Entity
@Access(AccessType.FIELD)
public class Member {
    @Id
    private Stirng id;
}
```

`@Id`가 필드에 있으므로 `@Access(AccessType.FIELD)`을 설정한 것과 같다.

따라서 `@Access(AccessType.FIELD)`을 생략해도 된다.

```java
@Entity
@Access(AccessType.PROPERTY)
public class Member {
    private Stirng id;

    @Id
    public String getId() {

    }
}
```

`@Id`가 `getter`에 있으므로 `@Access(AccessType.PROPERTY)` 설정한 것과 같다.

따라서 `@Access(AccessType.PROPERTY)`을 생략해도 된다.


```java
@Entity
public class Member {
    @Id
    private Stirng id;

    @Transient
    private String firstName;

    @Transient
    private String secondName;

    @Access(AccessType.PROPERTY)
    public String getFullName() {
        return firstName + secondName;
    }

    public String getId() {

    }
}
```

`@Id`가 필드에 있으므로 기본은 필드 접근 방식을 사용하고 `getFullName()`만 프로퍼티 접근 방식을 사용한다.

위의 코드에서는 회원 엔티티를 저장하면 회원 테이블의 `FULLNAME` 컬럼에 `firstName + secondName`의 결과가 저장된다.
