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

