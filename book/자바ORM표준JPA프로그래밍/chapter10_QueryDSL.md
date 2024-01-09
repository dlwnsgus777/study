
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