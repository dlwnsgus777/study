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

