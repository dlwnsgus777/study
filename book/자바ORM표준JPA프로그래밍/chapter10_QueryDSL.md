
## JPQL

---

### QueryDSL

JPA criteria의 가장 큰 단점은 너무 복잡하고 어렵다는 것이다. 

쿼리를 문자가 아닌 코드로 작성해도 쉽고 간결하며

모양도 쿼리와 비슷하게 개발할 수 있는 프로젝트가 `QueryDSL` 프로젝트이다.

`QueryDSL`은 JPQL 빌더 역할을 한다.

`QueryDSL`은 오픈소스 프로젝트이다.

### 시작

```java
public void queryDSL() {
    EntityManager em = emf.createEntityManager();

    JPAQuery query = new JPAQuery(em);
    QMember qMember = new QMember("m")// JPQL의 별칭

    List<Member> members = query.from(qMember)
    .where(qMember.name.eq("회원1"))
    .orderBy(qMember.name.desc())
    .list(qMember);
}
```

`QueryDSL`을 사용하려면 `JPAQuery`를 생성해야하는데 이때 엔티티 매니저를 넘겨준다.

쿼리 타입을 생성하는데 생성자에는 별칭을 주면된다.

### 기본 Q 생성

쿼리 타입은 사용하기 편하도록 기본 인스턴스를 보관하고 있다.

같은 엔티티를 조인하거나 같은 엔티티를 서브쿼리에 사용하면 같은 별칭이 사용되므로 이때는 별칭을 직접 지정해야 한다.

```java
QMember qMember = new QMember("m"); // 직접 지정
QMember qMember = QMember.member; // 기본 인스턴스 사용
```

### 결과 조회

쿼리 작성이 끝나고 결과 조회 메서드를 호출하면 실제 데이터베이스를 조회한다.

대표적인 결과 조회 메서드는 다음과 같다.

- `uniqueResult()`: 조회 결과가 한 건일 때 사용
  - 조회 결과가 없으면 `null`, 하나 이상이면 예외가 발생
- `singleResult()`: 결과가 하나 이상이면 처음 데이터를 반환한다.
- `list()`: 결과가 없으면 빈 컬렉션을 반환한다.

### 조인

조인은 `innerJoin`, `leftJoin`, `rightJoin`, `fullJoin`을 사용할 수 있다.

첫 번째 파라미터에 조인 대상을 지정하고, 두 번째 파라미터에 별칭으로 사용할 쿼리 타입을 지정하면 된다.

```java
Qorder order = QOrder.order;
QMember member = QMember.member;
QOrderItem orderItem = QOrderItem.orderItem;

query.from(order)
.join(order.member, member)
.leftJoin(order.orderItems, orderItem)
.list(order);
```

추가로 `on()`, `fetch()`도 사용할 수 있다.

```java
query.from(order, member)
.where(order.member.eq(member))
.list(order);
```

위 코드은 FROM 절에 여러 조인을 사용하는 세타 조인 방법이다.

### 서브 쿼리

서브 쿼리는 `com.mysema.query.JPASubQuery`를 생성해 사용한다.

```java
query.from(item)
.where(item.price.eq(
    new JPASubQuery().from(itemSub).unique(itemSub.price.max())
))
.list(item);
```

서브 쿼리의 결과가 하나면 `unique()`, 여러 건이면 `list()`를 사용하면 된다.

### 프로젝션과 결과 반환

프로젝션 대상이 하나이면 해당 타입으로 반환된다.

프로젝션 대상이 여러 필드면 `Tuple`이라는 `Map`과 비슷한 내부 타입을 사용하게 된다.

```java
QItem item = QItem.item;

List<Tuple> result = query
  .from(item)
  .list(item.name, item.price); // === .list(new QTuple(item.name, item.price));

for (Tuple tuple: result) {
  System.out.println("name = " + tuple.get(item.name));
  System.out.println("price = " + tuple.get(item.price));
}
```

### 빈 생성

쿼리 결과가 엔티티가 아닌 특정 객체로 받고 싶으면 빈 생성 기능을 사용한다.

`QueryDSL`은 객체를 생성하는 다양한 방법을 제공한다.

- 프로퍼티 접근
- 필드 직접 접근
- 생성자 사용

```java
// 프로퍼티 접근(Setter)
// Setter 사용해서 값 채워줌
List<ItemDTO> result = query
  .from(item)
  .list(
    Projections.bean(ItemDTO.class, item.name.as("username"), item.price) // 쿼리 결과와 매핑할 프로퍼티 이름 다르면 as 사용
  );

// field 직접 접근
// Projections.fields() 메소드 사용시 필드에 직접 접근해서 값 채워줌
// 필드를 private으로 설정해도 동작함
List<ItemDTO> result = query
  .from(item)
  .list(
    Projections.fields(ItemDTO.class, item.name.as("username"), item.price)
  );

// constructor 사용
// 생성자 이용해서 값 채움
// 지정한 프로젝션과 파라미터 순서 동일한 생성자가 필요
List<ItemDTO> result = query
  .from(item)
  .list(
    Projections.constructor(ItemDTO.class, item.name, item.price)
  );
```

### 수정, 삭제 배치 쿼리

`QueryDSL`에서도 수정, 삭제 같은 배치 쿼리를 지원한다.

이때 `JPQL` 배치 쿼리와 같이 **영속성 컨텍스트를 무시하고 데이터베이스를 직접 쿼리**하게 된다.

### 동적 쿼리

`BooleanBuilder`를 사용하면 특정 조건에 따른 동적 쿼리를 편리하게 생성할 수 있다.

```java
SearchParam param = new SearchParam();
param.setName("itemA")
param.setPrice(10000);

QItem item = QItem.item;

BooleanBuilder builder = new BooleanBuilder();
if (StringUtils.hasText(param.getName())) {
  builder.and(item.name.contains(param.getName()));
}
ig (param.getPrice() != null) {
  builder.and(item.price.gt(param.getPrice()));
}

List<Item> result = query
  .from(item)
  .where(builder)
  .list(item);
```

### 메서드 위임

메서드 위임 기능을 사용하면 쿼리 타입에 검색 조건을 직접 정의할 수 있다.


### 네이티브 SQL

JPQL은 특정 데이터베이스에 종속적인 기능은 지원하지 않는다. 

때로는 특정 데이터베이스에 종속적인 기능이 필요한데 JPA는 특정 데이터베이스에 종속적인 기능을 사용할 수 있는 방법을 제공한다.

- 특정 데이터베이스만 사용하는 함수
  - JPQL에서 네이티브 SQL 함수를 호출할 수 있다.
  - 하이버네이트는 데이터베이스 방언에 각 데이터베이스에 종속적인 함수들을 정의해두었다.
    - 직접 호출할 함수를 정의할 수도 있다.
- 특정 데이터베이스만 지원하는 SQL힌트
  - 하이버네이트를 포함한 몇몇 JPA 구현체들이 지원한다.
- 인라인 뷰, UNION, INTERSECT
  - 하이버네이트는 지원하지 않지만 일부 JPA 구현체들이 지원한다.
- 스토어 프로시저
  - JPQL에서 스토어 프로시저를 호출할 수 있다.
- 특정 데이터베이스만 지원하는 문법
  - 이때는 네이티브 SQL을 사용해야 한다.

**네이티브 SQL을 사용하면 엔티티를 조회할 수 있고 JPA가 지원하는 영속성컨텍스트의 기능을 그대로 사용할 수 있다.**

### 네이티브 SQL 사용

- 엔티티 조회

`em.createNativeQuery(SQL, 결과 클래스)`를 사용한다.

첫 번째 파라미터는 네이티브 SQL을 입력하고 두 번째 파라미터에는 조회할 엔티티의 클래스 타입을 입력한다.

```java
String sql = 
  "SELECT id, age, name, team_id " +
  "FROM member " +
  "WHERE age > ?";

Query nativeQuery = em.createNativeQuery(sql, Member.class) // native SQL은 type 정보 줘도 TypeQuery 아니고 Query 임
  .setParameter(1, 20);

List<Member> resultList = nativeQuery.getResultList();
```

**네이티브 SQL로 직접 SQL을 사용할 뿐이지 나머지는 JPQL을 사용할 때와 같다.**

### 결과 매핑 사용

매핑이 복잡해지면 `@SqlResultSetMapping`을 정의해 결과 매핑을 사용해야 한다.

```java
@Entity
@SqlResultSetMapping(name = "memberWithOrderCount",
  entities = {@EntityResult(entityClass = Member.class)}, // 여러 엔티티랑 매핑 가능
  columns = {@ColumnResult(name = "order_count")} // 여러 컬럼이랑 매핑 가능
)
public class Member { ... }
```

```java
String sql =
  "SELECT m.id age, name, team_id, i.order_count " +
  "FROM member m " +
  " LEFT JOIN " +
  "   (SELECT im.id, COUNT(*) AS order_count " +
  "   FROM orders o, member im " +
  "   WHERE o.member_id = im.id) i " +
  " ON m.id = i.id";

Query nativeQuery = em.createNativeQuery(sql, "memberWithOrderCount");
```
여러 엔티티와 여러 컬럼을 매핑할 수 있다.

### Named 네이티브 SQL

네이티브 SQL도 Named 네이티브 SQL을 사용해서 직접 SQL을 작성할 수 있다.

```java
@Entity
@NamedNativeQuery(
  name = "Member.memberSQL",
  query = 
    "SELECT id, age, name, team_id " +
    "FROM member " +
    "WHERE age > ?", 
  resultClass = Member.class
)
public class Member { ... }
```

```java
TypedQuery<Member> nativeQuery = em.createNamedQuery("Member.memberSQL", Member.class)
  .setParamter(1, 20);
```

JPQL의 `createNamedQuery` 메서드를 사용하게된다.

따라서 `TypeQuery`를 사용할 수 있다.

Named 쿼리에서도 `@SqlResultSetMapping` 를 사용할 수 있다.

```java
@Entity
@NamedNativeQuery(
  name = "Member.memberSQL",
  query = 
    "SELECT id, age, name, team_id " +
    "FROM member " +
    "WHERE age > ?", 
  resultSetMapping = "memberWithOrderCount"
)
public class Member {...}
```

### 네이티브 SQL XML에 정의

JPQL과 마찬가지로 Named 네이티브 쿼리를 XML에 정의하고 사용할 수 있다.

네이티브 쿼리를 작성하는 경우는 대체로 쿼리가 복잡하고 라인 수가 많이 때문에 어노테이션보다는 `XML`을 사용하는 것이 편리하다.

### 스토어드 프로시저 (JPA 2.1)

JPA는 2.1부터 스토어드 프로시저를 지원한다.

#### 스토어드 프로시저 사용

```java
# 첫 번째 파라미터로 값 입력받고 곱하기 2 해서 두 번째 파라미터로 결과 반환하는 프로시저 생성
DELIMITER //

CREATE PROCEDURE proc_multiply (INOUT inParam INT, INOUT outParam INT)
BEGIN
  SET outParam = inParam * 2;
END //
```

```java
// proc_multiply 프로시저 호출 (순서 기반 파라미터)
StoredProcedureQuery spq = em.createStoredProcedureQuery("proc_multiply");
spq.registerStoredProcedureParamter(1, Integer.class, ParamterMode.IN);
spq.registerStoredProcedureParamter(2, Integer.class, ParamterMode.OUT);

spq.setParamter(1, 100);
spq.execute();

Integer out = (Integer) spq.getOutputParamterValue(2);
System.out.println("out = " + out); // out = 200

...

// proc_multiply 프로시저 호출 (이름 기반 파라미터)
StoredProcedureQuery spq = em.createStoredProcedureQuery("proc_multiply");
spq.registerStoredProcedureParamter("inParam", Integer.class, ParamterMode.IN);
spq.registerStoredProcedureParamter("outParam", Integer.class, ParamterMode.OUT);

spq.setParamter("inParam", 100);
spq.execute();

Integer out = (Integer) spq.getOutputParamterValue(2);
System.out.println("out = " + out); 
```

스토어드 프로시저를 사용하려면 `em.createStoredProcedureQuery()`메서드에 사용할 스토어드 프로시저 이름을 입력하면 된다.

그 후 `registerStoredProcedureParameter()`메서드를 사용해 프로시저에서 사용할 파라미터를 순서, 타입, 파리미터 모드 순으로 정의하면 된다.

### Named 스토어드 프로시저 사용

스토어드 프로시저 쿼리에 이름을 부여해서 사용하는 것을 **Named 스토어드 프로시저**라고 한다.

```java
@Entity
@NamedStoredProcedureQuery(
  name = "multiply",
  procedureName = "proc_multiply",
  paramter = {
    @StoredProcedurePatameter(name = "inParam", mode = ParameterMode.IN, type = Intger.class),
    @StoredProcedureParamter(name = "outParam", mode = ParameterMode.OUT, type = Integer.class)
  }
)
public class Member { ... }

```

```java
StoredProcedureQuery spq = em.createNamedStoredProcedureQuery("multiply");

spq.setParamter("inParam", 100);
spq.execute();

Integer out = (Integer) spq.getOutputParamterValue("outParam");
System.out.println("out = " + out);
```

`@NamedStoredProcedureQuery`로 정의하고 `name` 속성을 부여하면 된다.

`@StoredProcedurePatameter`를 사용해 파라미터 정보를 정의한다.

둘 이상을 정의하려면 `@NamedStoredProcedureQueries`를 사용하면 된다.

스토어드 프로시저 역시 `XML`에 정의해 사용할 수 있다.

### 객체지향 쿼리 심화

### 벌크 연산

엔티티를 수정하려면 영속성 컨텍스트의 변경 감지 기능이나 병합을 사용하고, 삭제하려면 `EntityManager.remove()`를 사용한다.

이 방법은 수 백개 이상의 엔티티를 하나씩 처리하기에는 무리가 있다.

이럴때 **벌크연산** 을 사용하면 된다.

```java
String qlString =
  "UPDATE Product p " +
  "SET s.price = p.price * 1.1 " +
  "WHERE p.stockAmount < :stockAmount";

int resultCounht = em.createQuery(qlString)
  .setParamter("stockAmount", 10)
  .executeUpdate();
```

벌크 연산은 `excuteUpdate()`를 사용한다.

이 메서드는 벌크 연산으로 영향을 받은 엔티티 건수를 반환한다.

삭제에서도 같은 메서드를 사용한다.

JPA 표준은 아니지만 하이버네이트는 `INSERT 벌크` 연산도 지원한다.

```java
String qlString =
  "INSERT into ProductTemp(id, name, price, stockAmount) " +
  "SELECT p.id, p.name, p.price, p.stockAmount FROM Product p " +
  "WHERE p.price < :price";

int resultCount = em.createQuery(qlString)
  .setParameter("price", 100)
  .executeUpdate();
```

### 벌크 연산의 주의점

벌크 연산을 사용할 때는 **영속성 컨텍스트를 무시하고 데이터베이스에 직접 쿼리** 한다는 점을 주의해야 한다.

따라서 영속성 컨텍스트에 있는 엔티티와 실제 데이터베이스에 있는 값이 다를 수가 있다.

이 문제를 해결하는 방법은 다음과 같다.

- `em.refresh()`사용
  - 벌크 연산을 수행한 직후에 정확한 엔티티를 사용해야 한다면 `em.refresh()`를 사용해 데이터베이스에서 상품A를 다시 조회하면 된다.
- 벌크 연산 먼저 실행
  - 가장 실용적인 해결책으로 벌크 연산을 **가장 먼저 실행**하는 것이다.
  - 이 방법은 JPA와 JDBC를 함께 사용할 때도 유용하다.
- 벌크 연산 수행 후 영속성 컨텍스트 초기화
  - 벌크 연산을 수행한 직후 바로 영속성 컨텍스트를 초기화하는 방법도 있다.
  - 영속성 컨텍스트를 초기화하면 이후 엔티티를 조회할 때 벌크 연산이 적용된 데이터베이스에서 엔티티를 조회한다.

벌크 연산은 영속성 컨텍스트와 2차 캐시를 무시하고 데이터베이스에 직접 실행한다.

영속성 컨텍스트와 데이터베이스 간에 데이터차이가 발생할 수 있으므로 주의해서 사용해야 한다.

가능하면 벌크 연산을 먼저 수행하는 것이 좋고 상황에 따라 영속성 컨텍스트를 초기화하는 것도 필요하다.

### 영속성 컨텍스트와 JPQL

### JPQL로 조회한 엔티티와 영속성 컨텍스트

영속성 컨텍스트에 회원1이 이미 있는데 JPQL로 회원1을 다시 조회하면 어떻게 될까?

**JPQL로 데이터베이스에서 조회한 엔티티가 영속성 컨텍스트에 이미 있으면 JPQL로 데이터베이스에서 조회한 결과를 버리고 영속성 컨텍스트에 있던 엔티티를 반환한다.**

이때는 식별자 값을 사용해서 비교한다.

순서대로 보면 다음과 같다.

1. JPQL을 사용해서 조회를 요청
2. JPQL은 SQL로 변환되어 데이터베이스 조회
3. 조회한 결과와 영속성 컨텍스트 비교
4. 식별자 값을 기준으로 이미 영속성 컨텍스트에 있으면 JPQL로 가져온 값을 버리고 기존에 있던 엔티티를 반환
5. 식별자 값을 기준으로 영속성 컨텍스트에 엔티티가 없다면 영속성 컨텍스트에 조회한 엔티티 추가
6. 쿼리 결과 반환

위의 과정에서 다음 2가지를 확인할 수 있다.
