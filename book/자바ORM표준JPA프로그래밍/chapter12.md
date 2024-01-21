
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
`findByUsername` 처럼 직접 작성한 메서드는 스프링 데이터 JPA에서 메서드 이름을 분석해 JPQL을 실행하게 된다.

### 스프링 데이터 프로젝트

스프링 데이터 JPA는 스프링 데이터 프로젝트의 하위 프로젝트 중 하나이다.

스프링 데이터 JPA 프로젝트는 JPA에 특화된 기능을 제공한다.

### 공통 인터페이스 기능

스프링 데이터 JPA는 간단한 CRUD 기능을 공통으로 처리하는 `JpaRepository` 인터페이스를 제공한다.

스프링 데이터 JPA를 사용하는 가장 간단한 방법으로는 인터페이스를 상속받고 제네릭에 에닡티 클래스와 식별자 타입을 지정하면 된다.

```java
public interface JpaRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {

} 
```

`JpaRepository` 인터페이스를 상속받으면 사용할 수 있는 주요 메서드는 다음 과 같다.

- `save(S)`: 새로운 엔티티는 저장하고 이미 있는 엔티티는 수정한다.
- `delete(T)`: 엔티티 하나를 삭제한다.
  - `em.remove()`를 호출한다.
- `findOne(ID)`: 엔티티 하나를 조회한다.
  - `em.find()`를 호출한다.
- `getOne(ID)`: 엔티티를 프록시로 조회한다.
  - `em.getReference()`를 호출한다.
- `findAll()`: 모든 엔티티를 조회한다. 정렬이나 페이징 조건을 파라미터로 제공할 수 있다.

### 쿼리 메서드 기능

쿼리 메서드는 메서드 이름만으로 쿼리를 생성하는 기능이다. 

메서드만 선언하면 해당 메서드 이름으로 적절한 JPQL 쿼리를 생성해 실행한다.

쿼리 메서드 기능은 크게 3가지가 있다.

- 메서드 이름으로 쿼리 생성
- 메서드 이름으로 JPA NamedQuery 호출
- `@Query` 어노테이션을 사용해 레포지토리 인터페이스에 쿼리 직접 정의

### 메서드 이름으로 쿼리 생성

`findByEmailAndName` 메서드를 정의하면 스프링 데이터 JPA는 메서드 이름을 분석해 JPQL을 생성하고 실행한다.

이때 정해진 규칙에 따라 메서드 이름을 지어야한다.

### JPA NamedQuery

메서드 이름으로 JPA NamedQuery를 호출하는 기능을 제공한다.

스프링 데이터 JPA를 사용하면 메서드 이름만으로 Named 쿼리를 호출할 수 있다.

```java
public interface MemberRepository extends JpaRepository<Member, Long> {
  List<Member> findByUsername(@Param("username") String username);

}
```

`도메인 클래스 + (.) + 메서드 이름`으로 Named 쿼리를 찾아서 실행한다.

만약 실행할 Named 쿼리가 없으면 메서드 이름으로 쿼리를 생성하는 전략을 사용한다.

`@Param`은 이름기반 파라미터를 바인딩할 때 사용하는 어노테이션이다.

### @Query. 레포지토리 메서드에 쿼리 정의

레포지토리 메서드에 직접 쿼리를 정의할 때는 `@Query`에노테이션을 사용하면 된다.