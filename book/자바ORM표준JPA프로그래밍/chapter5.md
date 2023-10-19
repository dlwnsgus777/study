## 연관관계 매핑 기초

---

객체는 참조를 사용해서 관계를 맺고 테이블은 외래 키를 사용해서 관계를 맺는다.

연관관계 매핑을 이해하기 위해서는 다음 핵심 키워드를 알아야한다.

- 방향: `단방향`, `양방향`이 있다.
  - 회원과 팀이 있을 때 `회원 -> 팀`, `팀 -> 회원` 한 쪽만 참조하는 것을 **단방향 관계**
  -  `회원 -> 팀`, `팀 -> 회원` 양쪽 모두 서로 참조하는 것을 **양방향 관계**
- 다중성: `다대일`, `일대다`, `일대일`, `다대다` 가 있다.
  - 여러 회원은 한 팀에 속하므로 회원과 팀은 `다대일` 관계다.
  - 한 팀에 여러 회원이 속할 수 있으므로 팀과 회원은 `일대다` 관계이다.
- 연관관계의 주인: 객체를 양방향 연관관계로 만들면 연관관계의 주인을 정해야 한다.

### 단방향 연관관계

회원과 팀의 다대일 단방향 관계를 알아본다.

- 회원과 팀이 있다.
- 회원은 하나의 팀에만 소속될 수 있다.
- 회원과 팀은 다대일 관계다.

#### 객체 연관관계

- 회원 객체는 `Member.team` 필드(멤버변수)로 팀 객체와 연관관계를 맺는다.
- 회원 객체와 팀 객체는 **단방향 관계**이다.
- 회원은 `Member.team` 필드를 통해서 팀을 알 수 있지만 **팀은 회원을 알 수 없다.**

#### 테이블 연관관계

- 회원 테이블은 `TEAM_ID` 외래 키로 팀 테이블과 연관관계를 맺는다.
- 회원 테이블과 팀 테이블은 **양방향 관계**이다.
- 회원 테이블의 `TEAM_ID` 외래 키를 통해 회원과 팀을 조인하고 반대로 팀과 회원도 조인할 수 있다.

#### 객체 연관관계와 테이블 연관관계의 가장 큰 차이

- 참조를 통한 연관관계는 언제나 단방향이다.
  - 양방향으로 만ㄷ늘고 싶으면 반대쪽에도 필드를 추가해 참조를 보관해야 한다.
- 양쪽에서 서로 참조하는 것을 양방향 연관관계라 한다.
  - 정확히는 **서로 다른 단방향 관계 2개이다.**
- 테이블은 외래 키 하나로 양방향으로 조인할 수 있다.

```java
// 단방향 연관관계
class A {
  B b;
}

class B {

}
```

```java
// 양방향 연관관계
class A {
  B b;
}

class B {
  A a;
}
```

#### 객체 연관관계 VS 테이블 연관관계 정리

- 객체는 참조로 연관관계를 맺는다.
- 테이블은 외래 키로 연관관계를 맺는다.
- 참조를 사용하는 객체의 연관관계는 단방향이다.
  - `A -> B(a.b)`
- 외래 키를 사용하는 테이블의 연관관계는 양방향이다.
  - `A JOIN B` 가 가능하면 `B JOIN A` 도 가능하다.
- 객체를 양방향으로 참조하려면 단방향 연관관계를 2개 만들어야 한다.
  - `A -> B(a.b)`
  - `B -> A(b.a)`

### 순수한 객체 연관관계

순수한 객체를 사용한 연관관계를 알아본다.

JPA를 사용하지 않은 순수한 회원과 팀 클래스 코드이다.

```java
public class Team {
  private String id;
  private String name;
}

public class Member {

  public Member(final String id, final String username) {
    this.id = id;
    this.username = username;
  }

  private String id;
  private String username;

  private Team team;

  public void setTeam(final Team team) {
    this.team = team;
  }

  public Team getTeam() {
    return team;
  }
}

```

```java
// 실행 코드
    Member member1 = new Member("member1", "회원1");
    Member member2 = new Member("member2", "회원2");
    Team team = new Team("team1", "팀1");

    member1.setTeam(team);
    member2.setTeam(team);

    Team findTeam = member1.getTeam();
```

코드에서 회원1과 회원2는 팀1에 소속했다. 

`Team findTeam = member1.getTeam();` 코드를 통해 회원1이 소속한 팀1을 조회할 수 있다.

이렇게 객체는 참조를 이용해 연관관계를 탐색할 수 있는데 이를 **객체 그래프 탐색**이라고 한다.

### 테이블 연관관계

```sql
CREATE TABLE MEMBER (
  MEMBER_ID VARCHAR(255) NOT NULL,
  TEAM_ID VARCHAR(255),
  USERNAME VARCHAR(255),
  PRIMARY KEY (MEMBER_ID)
)

CREATE TABLE TEAM (
  TEAM_ID VARCHAR(255) NOT NULL,
  NAME VARCHAR(255),
  PRIMARY KEY (TEAM_ID)
)

ALTER TABLE MEMBER ADD CONSTRAINT FK_MEMBER_TEAM
FOREGN KEY (TEAM_ID)
REFERENCES TEAM
```

회원 테이블과 팀 테이블을 생성한다.

추가로 회원 테이블의 `TEAM_ID`에 외래키 제약 조건을 설정했다.

```sql
INSERT INTO TEAM(TEAM_ID, NAME) VALUES('team1', '팀1');
INSERT INTO MEMBER(MEMBER_ID, TEAM_ID, USERNAME) VALUES('member1', 'team1', '회원1');
INSERT INTO MEMBER(MEMBER_ID, TEAM_ID, USERNAME) VALUES('member2', 'team1', '회원2');
```

```sql
SELECT T.*
FROM MEMBER M
JOIN TEAM T ON M.TEAM_ID = T.TEAM_ID
WHERE M.MEMBER_ID = 'member1'
```

위처럼 데이터베이스는 외래 키를 사용해 연관관계를 탐색할 수 잇는데 이를 **조인**이라 한다.

### 객체 관계 매핑

이제 JPA를 사용해 둘을 매핑해본다.

```java
@Entity
public class Team {
  
  @Id
  @Column(name = "TEAM_ID")
  private String id;
  
  private String name;
}

@Entity
public class Member {

  @Id
  @Column(name = "MEMBER_ID")
  private String id;

  private String username;

  // 연관관계 매핑
  @ManyToOne
  @JoinColumn(name = "TEAM_ID")
  private Team team;

  // 연관관계 설정
  public void setTeam(Team team) {
    this.team = team;
  }
}

```

- 객체 연관관계: 회원 객체의 `Member.team` 필드 사용
- 테이블 연관관계: 회원 테이블의 `MEMBER.TEAM_ID` 외래 키 컬럼을 사용

```java

  // 연관관계 매핑
  @ManyToOne
  @JoinColumn(name = "TEAM_ID")
  private Team team;
```

- `@ManyToOne`: 다대일 관계 매핑 어노테이션이다.
  - 연관관계를 매핑할 때 다중성을 나타내는 어노테이션을 필수로 사용한다.
- `@JoinColumn(name = "TEAM_ID")`: 조인 컬럼은 외래 키를 매핑할 때 사용한다.
  - name 속성에는 매핑할 외래 키 이름을 지정한다.
  - 이 어노테이션은 생략할 수 있다.

### @JoinColumn

`@JoinColumn`은 외래 키를 매핑할 때 사용한다. 속성은 다음과 같다.

- name: 매핑할 외래 키 이름
  - 기본값: `필드명 + _ + 참조하는 테이블의 기본 키 컬럼 명`
- referncedColumnName: 외래 키가 참조하는 대상 테이블의 컬럼 명
  - 참조하는 테이블의 기본 키 컬럼명
- foreignkey(DDL): 외래 키 제약 조건을 직접 지정할 수 있다. 
  - 테이블을 생성할 때만 사용한다.

`@JoinColumn`을 생략하면 외래 키를 찾을 때 기본 전략을 사용한다.

### @ManyToOne

`@ManyToOne`은 다대일 관계에서 사용한다. 속성은 다음과 같다.

- optional: `false`로 설정하면 연관된 엔티티가 항상 있어야한다.
- fetch: 글로벌 패치 전략을 설정한다.
- cascade: 영속성 전이 기능을 사용한다.
- targetEntity: 연관된 엔티티의 타입 정보를 설정한다.
  - 이 기능은 거의 사용하지 않는다.
  - 컬렉션을 사용해도 제네릭으로 타입 정보를 알 수 있다.

`@ManyToOne`과 비슷한 `@OneToOne` 관계도 있다.

반대편이 일대다 관계면 다대일을 일대일 관계이면 일대일을 사용하면 된다.

### 연관관계 사용

#### 저장

```java
    Team team = new Team("team1", "팀1");
    em.persist(team);

    Member member1 = new Member("member1", "회원1");
    member1.setTeam(team);
    em.persist(member1);

    Member member2 = new Member("member2", "회원2");
    member2.setTeam(team);
    em.persist(member2);
```

JPA에서 엔티티를 저장할 때 연관된 모든 엔티티는 영속 상태여야한다는 걸 주의해야한다.

위의 코드에서는 회원 엔티티는 팀 엔티티를 참조하고 저장했다.

JPA는 참조한 팀의 식별자(team.id)를 외래 키로 사용해 적절한 등록 쿼리를 생성한다.

#### 조회

연관관계가 있는 엔티티를 조회하는 방법은 크게 2가지이다.

- 객체 그래프 탐색(객체 연관관계를 사용한 조회)
- 객체지향 쿼리 사용(JPQL)

`member.getTeam()` 을 사용해 `member`와 연관된 `team` 엔티티를 조회할 수 있다.

객체지향 쿼리인 JPQL을 사용해 연관관를 조회할 수 있다.

```jpql
select m from Member m join m.team t
where t.name = :teamName
```

#### 수정

```java
    Team team2 = new Team("team2", "팀2");
    em.persist(team2);
    
    Member member = em.find(Member.class, "member1");
    member.setTeam(team2);
```

불러온 엔티티의 값만 변경해두면 트랜잭션을 커밋할 때 플러시가 일어나면서 변경 감지 기능이 작동한다.

이는 연관관계 수정시에도 마찬가지인데 참조하는 대상만 변경하면 나머지는 JPA가 자동으로 처리한다.

#### 연관관계 제거

```java
    Member member1 = em.find(Member.class, "member1");
    member1.setTeam(null); // 연관관계 제거
```

연관관계를 `null`로 설정하여 연관관계를 제거했다.

#### 연관된 엔티티 삭제

연관된 엔티티를 삭제하려면 기존에 있던 연관관계를 먼저 제거하고 삭제해야 한다.

외래 키 제약 조건으로 인해 데이터베이스에서 오류가 발생하는 걸 방지하기 위함이다.

```java
member1.setTeam(null);  // 회원 1 연관관계 제거
member2.setTeam(null);  // 회원 2 연관관계 제거
em.remove(team) // 팀 삭제
```

### 양방향 연관관계

팀에서 회원으로 접근할 수 있게 양방향 연관관계로 매핑해본다.

회원과 팀은 `다대일`관계이다. 

반대로 팀과 회원은 `일대다` 관계이다.

`일대다`관계에서는 여러 건과 연관관계를 맺을 수 있기 때문에 컬렉션을 사용한다.

JPA에서는 `List`를 포함한 `Collection`, `Set`, `Map` 같은 다양한 컬렉션을 지원한다.

데이터베이스 테이블은 외래 키 하나로 양방향으로 조회할 수 있다.

외래 키 하나만으로 양방향 조회가 가능하므로 처음부터 **양방향 관계이다.**

### 양방향 연관관계 매핑

```java
@Entity
public class Member {
  @Id
  @Column(name = "MEMBER_ID")
  private String id;

  private String username;

  // 연관관계 매핑
  @ManyToOne
  @JoinColumn(name = "TEAM_ID")
  private Team team;

  // 연관관계 설정
  public void setTeam(Team team) {
    this.team = team;
  }

  public Team getTeam() {
    return team;
  }

  public Member(final String id, final String username) {
    this.id = id;
    this.username = username;
  }
}
```
회원 엔티티는 변경한 부분이 없다.

```java
@Entity
public class Team {

  @Id
  @Column(name = "TEAM_ID")
  private String id;

  private String name;
  
  // 추가
  @OneToMany(mappedBy = "team")
  private List<Member> members = new ArrayList<>();

  public Team(final String id, final String name) {
    this.id = id;
    this.name = name;
  }

  public Team() {
    
  }
}
```

회원과 팀은 일대다 관계이기 때문에 `@OneToMany`를 사용해 양방향 관계를 매핑했다.

`mappedBy` 속성은 양방향 매핑일 때 **반대쪽 매핑의 필드 이름**을 넣어주면 된다.

이제부터 팀에서 회원 컬렉션으로 객체 그래프를 탐색할 수 있다.

```java
Team team = em.find(Team.class, "team1");
List<Member> members = team.getMembers();

for (Member member : members) {
  System.out.println(member.getUsername());
}
```

### 연관관계의 주인

엄밀히 말해 객체에는 양방향 연관관계라는 것이 없다.

서로 다른 단방향 연관관계 2개를 양방향인 것처럼 보이게 할 뿐이다.

객체 연관관계는 다음과 같다.

- 회원 -> 팀 (단방향)
- 팀 -> 회원 (단방향)

엔티티를 양방향 연관관계로 설정하면 객체의 참조는 둘인데 **외래 키는 하나다.**

두 객체의 연관관계 중 하나를 정해 테이블의 외래 키를 관리해야 하는데 이걸 **연 관관계의 주인**이라고 한다.

### 양방향 매핑의 규칙: 연관관계의 주인

양방향 연관관계 매팽시에는 두 연관관계 중 하나를 **연관관계의 주인**으로 정해야한 한다.

**연관관계의 주인만이 외래 키를 등록, 수정, 삭제할 수 있다.**

**주인이 아닌 쪽은 읽기만 할 수 있다.**

연관관계의 주인은 `mappedBy` 속성을 사용해 정한다.

- 주인은 `mappedBy` 속성을 사용하지 않는다.
- 주인이 아니면 `mappedBy` 속성을 사용해 연관관계의 주인을 지정한다.

연관관계의 주인을 정한다는 것은 **외래 키 관리자를 선택하는 것**이다.

### 연관관계의 주인은 외래 키가 있는 곳

연관관계의 주인은 테이블에 외래 키가 있는 곳을 정해야 한다.

예제에서는 회원 테이블이 외래 키를 가지고 있기 때문에 `Member.team`이 주인이 된다.

데이터베이스 테이블의 다대일, 일대 다 관계에서는 항상 **다** 쪽이 외래 키를 가진다.

따라서 `@ManyToOne`에는 `mappedBy` 속성이 없다.

### 양방향 연관관계 저장

```java
Team team1 = new Team("team1", "팀1");
em.persist(team1);

Member member1 = new Member("member1", "회원1");
member1.setTeam(member1);
em.persist(member1);

Member member2 = new Member("member2", "회원2");
member2.setTeam(member2)
em.persist(member2);
```

연관관계 주인을 통해 팀과 회원을 저장했다.

양방향 연관관계에서는 연관관계의 주인이 외래 키를 관리하기 때문에 **주인이 아닌 방향은 값을 설정하지 않아도 데이터베이스에 외래 키 값이 정상 입력된다.**

**주인이 아닌 곳에 입력된 값은 외래 키에 영향을 주지 않는다.**

엔티티 매니저는 이곳에 입력된 값을 사용해서 외래 키를 관리한다.

### 양방향 연관관계의 주의점

양방향 연관관계에서 주의할 점은 연관관계의 주인에는 값을 입력하지 않고 주인이 아닌 곳에만 값을 입력하면 정상적으로 저장이되지 않는다는 것이다.

```java
Member member1 = new Member("member1", "회원1");
em.persist(member1);

Member member2 = new Member("member2", "회원2");
em.persist(member2);

Team team1 = new Team("team1", "팀1");
// 주인이 아닌 곳만 연관관계 설정
team1.getMembers().add(member1);
team1.getMembers().add(member2);
em.persist(team1);
```

연관관계의 주인만이 외래 키의 값을 변경할 수 있다.

### 순수한 객체까지 고려한 양방향 연관관계

**객체 관점에서 양쪽 방향에 모두 값을 입력해주는 것이 가장 안전하다.**

그렇지 않으면 JPA를 사용하지 않는 순수한 객체 상태에서 심각한 문제가 발생할 수 있다.

ORM은 객체와 관계형 데이터베이스 둘 다 중요하기 때문에 데이터베이스와 객체를 함께 고려해야 한다.

객체까지 고려하면 양쪽 다 관계를 맺어야 한다.

```java
    Team team1 = new Team("team1", "팀1");
    em.persist(team1);
    
    Member member1 = new Member("member1", "회원1");
    
    member1.setTeam(team1);
    team1.getMembers().add(member1);
    em.persist(member1);

    Member member2 = new Member("member2", "회원2");

    member2.setTeam(team1);
    team1.getMembers().add(member2);
    em.persist(member2);
```

양쪽에 연관관계를 설정했기 때문에 순수한 객체 상태에서도 동작한다.

테이블의 외래 키도 정상 입력된다.

이때는 연관관계의 주인인 `Member.team` 값을 사용한다.

### 연관관계 편의 메서드

양방향 연관관계는 양쪽 다 신경써야 한다.

양쪽에 연관관계를 맺다보면 실수로 둘 중 하나만 호출해 양방향이 깨질 수 있다.

```java
  public void setTeam(Team team) {
    this.team = team;
    team.getMembers().add(this);
  }
```

위의 코드처럼 메소드 하나로 양방향 관계를 모두 설정할 수 있도록한다.

한 번에 양방향 관계를 설정하는 메서드를 **연관관계 편의 메서드**라 한다.

### 연관관계 편의 메서드 작성 시 주의사하아

`setTeam()` 메서드에는 버그가 있다.

`setTeam` 호출 후 다른 팀으로 변경할 때 기존 팀과 회원의 연관관계를 삭제하는 코드를 추가해야 한다.

```java
  public void setTeam(Team team) {
    // 기존 팀과 관계를 제거
    if (this.team != null) {
      this.team.getMembers().remove(this);
    }
    this.team = team;
    team.getMembers().add(this);
  }
```

객체에서 양방향 연관관계를 사용하려면 로직을 견고하게 작성해야 한다.

### 정리

단방향 매핑과 비교해 양방향 매핑은 복잡하다.

**양방향의 장점은 반대방향으로 객체 그래프 탐색 기능이 추가된 것 뿐이다.**

주인의 반대편읜 `mappedBy`를 사용해 주인을 지정해야 한다.

주인의 반대편은 단순히 객체 그래프 탐색만 할 수 있다.

- 단방향 매핑만으로 테이블과 객체의 연관관계 매핑은 이미 완료되었다.
- 단방향을 양방향으로 만들면 반대방향으로 객체 그래프 탐색 기능이 추가된다.
- 양방향 연관관계를 매핑하려면 객체에서 양쪽 방향을 모두 관리해야 한다.

#### 연관관계의 주인을 정하는 기준

연관관계의 주인은 외래 키의 위치와 관련해서 정해야지 비즈니스 중요도로 접근하면 안 된다.

양방향 매핑시에는 무한루프에 빠지지 않게 조심해야 한다.

`일대다`를 연관관계의 주인으로 선택하는 것이 불가능한 것은 아니지만 성능과 관리 측면에서 권장하지 않는다.



