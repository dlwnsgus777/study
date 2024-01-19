
## 스프링 데이터 JPA

---

### 스프링 데이터 JPA 소개

스프링 데이터 jpa는 스프링 프레임워크에서 JPA를 편리하게 사용할 수 있도록 지원하는 프로젝트다.

데이터 접근 계층을 개발할 때 지루하게 반복되는 `CRUD` 문제를 해결한다.

`CRUD`를 처리하기 위한 공통 인터페이스를 제공하고 **데이터 접근 계층을 개발할 때 구현 클래스 없이 인터페이스 만 작성해도 개발을 완료할 수 있다.**

`CRUD`를 처리하기 위한 공통 인터페이스는 스프링 데이터 JPA가 제공하는 `JpaRepository` 인터페이스에 있다.

```java
public interface MemberRepository extends JpaRepository<Member, Long> {
  Member findByUsername(String username);
}
```

