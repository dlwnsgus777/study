
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

이 방법은 이름없는 Named 쿼리를 작성하는 방법이라 할 수 있다.

애플리케이션 실행 시점에 문법 오류를 발견할 수 있다는 장점이 있다.

```java
public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("select m from Member m where m.username = ?1")
    Member findByUsername(String username);
}
```

JPQL은 위치 기반 파라미터를 **1부터 시작**하지만 **네이티브 SQL은 0부터 시작**한다.

### 파라미터 바인딩

스프링 데이터 JPA는 위치 기반 파라미터 바인딩과 이름 기반 파라미터 바인딩을 모두 지원한다.

기본값은 위치 기반이다.

### 벌크성 수정 쿼리

```java
@Modifying
@Query("update Product p set p.price = p.price * 1.1 where p.stockAmount < :stockAmount")
int bulkPriceUp(@Param("stockAmount") String stockAmount);
```

스프링 데이터 JPA에서는 벌크성 수정, 삭제 쿼리는 `@Modifying` 을 사용하면 된다.

벌크성 쿼리를 실행하고 영속성 컨텍스트를 초기화하고 싶으면 `@Modifying(clearAutomatically = true)` 옵션을 설정하면 된다.

### 반환 타입

스프링 데이터 JPA에서는 결과가 한 건 이상이면 컬렉션 인터페이슬르 사용하고, 단건이면 반환 타입을 지정한다.

조회 결과가 없으면 컬렉션은 빈 컬렉션, 단건은 `null`을 반환한다.

단건을 기대했지만 결과가 2건 이상 조회되면 예외가 발생한다.

### 페이징과 정렬

스프링 데이터 JPA는 쿼리 메서드에 페이징과 정렬 기능을 사용할 수 있도록 2가지 파라미터를 제공한다.

- `org.springframework.data.domain.Sort`: 정렬 기능
- `org.springframework.data.domain.Pageable`: 페이징 기능(내부에 sort포함)

파라미터에 `Pageable`을 사용하면 반환 타입으로 `List`나 `Page`를 사용할 수 있다.

`Page`를 사용하면 스프링 데이터 JPA는 검색된 전체 데이터 건수를 조회하는 `count` 쿼리를 추가로 호출한다.

### 힌트

`@QueryHints`를 사용하면 JPA 쿼리 힌트를 사용할 수 있다.

이것은 SQL 힌트가 아니라 JPA 구현체에게 제공하는 힌트다.

### Lock

쿼리 시 락을 걸려면 `@Lock` 어노테이션을 사용하면 된다.

### 명세

도메인 주도 설계에서는 명세라는 개념을 소개하는데 스프링 데이터 JPA는 `JPA Criteria`로 이 개념을 사용할 수 있도록 지원한다.

명세를 이해하기 위한 핵심 단어는 `술어`인데 데이터를 검색하기 위한 조건 하나하나를 술어라 할 수 있다.

스프링 데이터 JPA는 `Spectification`클래스로 정의했다.

### 사용자 정의 레포지토리 구현

스프링 데이터 JPA로 레포지토리를 개발하면 인터페이스만 정의하고 구현체는 만들지 않는다.

스프링 데이터 JPA에서는 필요한메서드만 구현할 수 있는 방법을 제공한다.

```java
public interface MemberRepositoryCustom {
    public List<Member> findMemberCustom();
}
```
직접 구현할 메서드를 위한 사용자 정의 인터페이스를 작성한다.

이때 이름은 자유롭게 지으면 된다.

```java
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    @Override
    public List<Member> findMemberCustom() {
        // 구현부
    }
}
```
다음으로 사용자 정의 인터페이스를 구현한 클래스를 작성한다.

이때 클래스 이름은 `레포지토리 인터페이스 이름 + Impl`로 지어야한다.

```java
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom
```

마지막으로 레포지토리 인터페이스에서 사용자 정의 인터페이스를 상속받으면 된다.

`Impl`대신 다른 이름을 붙이고 싶다면 설정을 통해 변경하면 된다.

```java
@EnableJpaRepositories(basePackages = "jpabook.jpashop.repository", repositoryImplementationPostfix = "Impl")
```

### Web 확장

스프링 MVC에서 사용할 수 있는 편리한 기능을 제공한다.

식별자로 도메인 클래스를 바로 바인딩해주는 도메인 클래스 컨버터 기능과 페이징과 정렬 기능이다.

### 설정

```java
@EnableSpringDataWebSupport
```
를 사용해 설정할 수 있다.

설정을 완료하면 `HandlerMethodArgumentResolver`가 스프링 빈으로 등록된다.

### 도메인 클래스 컨버터 기능

도메인 클래스 컨버터는 HTTP 파라미터로 넘어온 엔티티의 아이디로 엔티티 객체를 찾아 바인딩해준다.

`/member/memberUpdateFrom?id=1`

이라는 URL을 호출했을 때

```java
@Controller
public class MemberController {

  @RequestMapping("member/memberUpdateFrom")
  public String memberUpdateFrom(@RequestParam("id") Member member, Model model) {
    model.addAttribute("member", member);
    return "member/memberSaveFrom";
  }
}
```
도메인 클래스 컨버터를 통해 넘어온 회원 엔티티를 컨트롤러에서 직접 수정해도 **실제 데이터베이스에는 반영되지 않는다.**

### 페이징과 정렬 기능

스프링 데이터는 페이징과 정렬 기능을 MVC에서 편리하게 사용할 수 있도록 `HandlerMethodArgumentResolver`를 제공한다.

- 페이징 기능: `PageableHandlerMethodArgumentResolver`
- 정렬 기능: `SortHandlerMethodArgumentResolver`

```java
@RequestMapping(value = "/members", method = RequestMethod.GET)
public String list(Pageable pageable, Model model) {
    	
    Page<Member> page = memberService.findMembers(pageable);
    model.addAttribute("members", page.getContent());
    return "members/memberList";
}
```
파라미터로 `Pageable`을 받을 수 있다. 

`Pageable`은 인터페이스이고 실제로는 `PageRequest` 객체가 생성된다.

요청 파라미터 정보는 다음과 같다.

- page: 현재 페이지, 0부터 시작
- size: 한 페이지에 노출할 데이터 건수
- sort: 정렬 조건 정의

페이지를 1부터 시작하고 싶으면 `setOneIndexedParameters` 옵션을 변경하면 된다.

### 접두사

사용해야할 페이징 정보가 둘 이상이면 **접두사**를 사용해서 구분할 수 있다.

```java
public String list (
	@Qualifier("member") Pageable memberPageable,
    @Qualifier("order") Pageable orderPageable,

예) /members?member_page=0&order_page=1
```

### 기본 값

`Pageable`의 기본값은 `page=0, size=20`이다. 

기본 값을 변경하고 싶으면 `@PageableDefault`를 사용하면 된다.

### 스프링 데이터 JPA가 사용하는 구현체

스프링 데이터 JPA가 제공하는 공통 인터페이스는 `SimpleJpaRepository`클래스가 구현한다.

```java
import java.io.Serializable;

@Repository
@Transactional(readOnly = true)
public class SimpleJpaRepository<T, ID extends Serializable> implements JpaRepository<T, ID>, JpaSpecificationExcutor<T> {
    @Transactional
  public <S extends T> S save(S entity);
}
```

- `@Repository` 적용: JPA 예외를 스프링이 추상화한 예외로 변환한다.
- `@Transactional`: JPA의 모든 변경은 트랜잭션 안에서 이루어져야 한다.
  - 서비스 계층에서 트랜잭션을 시작하지 않으면레포지토리에서 트랜잭션을 시작한다.
  - 서비스 계층에서 트랜잭션을 시작했으면 레포지토리도 해당 트랜잭션을 전파받아 그대로 사용한다.


### 스프링 데이터 JPA와 QueryDSL 통합

스프링 데이터 JPA는 2가지 방법으로 `QueryDSL` 을 지원한다.

- `QueryDsqlPredicateExcutor`
- `QueryDslRepositorySupport`

### QueryDsqlPredicateExcutor 사용

레포지토리에서 `QueryDsqlPredicateExcutor`를 상속받아 사용한다.

```java
public interface ItemRepository
        extends JpaRepository<Item, Long>, QueryDslPredicateExecutor<Item> {
}
```

```java
QItem item = QItem.item;
Iterable<Item> result = itemRepository.findAll(
        item.name.comtains("장난감").and(item.price.between(10000, 20000))
);
```

`QueryDsqlPredicateExcutor` 인터페이스를 보면 `QueryDSL`을 사용하면서 스프링 데이터 JPA가 제공하는 페이징과 정렬 기능도 함께 사용할 수 있다.

`QueryDsqlPredicateExcutor`는 `join`, `fetch`를 사용할 수 없다. (묵시적 조인은 가능하다.)

따라서 `QueryDSL`이 제공하는 다양한 기능을 사용하려면 `JPAQuery`를 직접 사용하거나 `QueryDslRepositorySupport`를 사용해야 한다.


### QueryDslRepositorySupport

`QueryDSL`이 제공하는 다양한 기능을 사용하려면 `JPAQuery`객체를 직접 생성해서 사용하면 된다.

이때 `QueryDslRepositorySupport`를 상속받아 사용하면 조금 더 편리하게 `QueryDSL`을 사용할 수 있다.

```java
public interface CustomOrderRepository {
    
    public List<Order> search(OrderSearch orderSearch);
}
```

스프링 데이터 JPA가 제공하는 공통 인터페이스는 직접 구현할 수 없기 때문에 

`CustomOrderRepository`라는 사용자 정의 레포지토리를 작성한다.

```java
public class OrderRepositoryImpl extends QuerydslRepositorySupport
        implements CustomOrderRepository{

    public OrderRepositoryImpl() {
        super(Order.class);
    }

    @Override
    public List<Order> search(OrderSearch orderSearch) {
        QOrder order = QOrder.order;
        QMember member = QMember.member;

        JPQLQuery query = from(order);
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query.leftJoin(order.member, member)
                    .where(member.name.contains(orderSearch.getMemberName()));
        }

        if(orderSearch.getOrderStatus() != null) {
            query.where(order.status.eq(orderSearch.getOrderStatus()));
        }

        return query.fetch();
    }
}
```
`QuerydslRepositorySupport`를 사용해 `QueryDSL`로 구현한다.

생성자에서 `QuerydslRepositorySupport`에 엔티티 클래스 정보를 넘겨주어야 한다.


