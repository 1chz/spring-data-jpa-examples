# 📕 Entity Mapping
작성자: 한창훈

---

![image](https://user-images.githubusercontent.com/71188307/123510343-dbccf880-d6b5-11eb-9c4e-041642747546.png)

JPA를 사용하는 데 가장 중요한 일은 Entity와 Table을 정확하게 매핑하는 것이다.

JPA의 매핑 어노테이션은 크게 4가지로 분류할 수 있다.

- 객체(Entity) ↔ 테이블(Table): `@Entity`, `@Table`
- 기본 키(PK, Primary Key): `@Id`
- 필드(Field) ↔ 컬럼(Column): `@Column`
- 연관관계(FK, Foreign Key): `@ManyToOne`, `@ManyToMany`, `@OneToMany`, `@OneToOne`, `@JoinColumn`


### 💨 Naming Strategy..

스프링 부트에서 spring-data-jpa 사용시 hibernate가 기본 구현체로 채택돼있고,

hibernate 매핑시 `ImprovedNamingStrategy`를 기본 클래스로 사용하고 있는걸로 판단된다.

같은 `NamingStrategy` 인터페이스를 구현한 `DefaultNamingStrategy`도 존재하는데,

이름만 보면 이녀석이 디폴트가 맞는 것 같은데....

정확한 동작과 내부 구현에 대해서는 추후 심도깊게 파봐야겠다.

일단 `ImprovedNamingStrategy`는 간단하게 

- 소문자 → 소문자
- 대문자 → 대문자
- 카멜 케이스 → 스네이크 케이스 

로 변환하여 매핑해주는 걸로 보인다.


---

# 🚀 @Entity

이 어노테이션이 붙은 클래스는 JPA에서 관리하게 된다.

따라서 JPA의 모든 기능을 이용하고자 한다면 필수적으로 사용하게 되는 어노테이션이다.

![image](https://user-images.githubusercontent.com/71188307/123519637-8ad5f800-d6e7-11eb-9db4-af1b383b871b.png)

|속성|기능|기본값
|---|---|---|
|name|JPA에서 사용할 엔티티 이름을 지정한다.<br /> 이후 JPQL등에서 이 이름을 사용한다.<br /> 프로젝트 전체에서 고유한 이름을 설정해야만 충돌로 인한 문제가 발생하지 않는다.|클래스 이름을 그대로 사용한다.|

## @Entity 제약사항

- 접근제한자가 `public`또는 `protected`인 기본 생성자가 반드시 필요하다.
- `final`, `enum`, `interface`, `inner` 클래스에서는 사용할 수 없다.
- 저장할 필드에 `final`을 사용할 수 없다.

엔티티 객체는 내부적으로 `Class.getDeclaredConstructor().newInstance()` 라는 자바 리플렉션을 사용하여 동작하는데

이 API는 생성자의 인수를 읽을 수 없기 때문에 인수가 존재하지 않는 기본 생성자가 반드시 필요하다.

JPA 구현체로 Hibernate 벤더를 사용하는 경우 Hibernate 내부적으로 바이트코드를 조작하는 라이브러리를 도입하여

이러한 이슈를 어느정도 보완해준다고 하나 역시 완벽한 해결책이 아니며

기본 생성자 없이 동작할 수도 있지만, 오히려 동작하지 않기도 하는 경우가 발생하여 오히려 더 안 좋은 상황을 야기할 수 있으므로,

기본 생성자 만큼은 반드시 생성하는걸 권고하고 있다.

기본적으로 자바 컴파일러는 클래스에 아무 생성자도 없으면 인수가 존재하지 않는 생성자, 즉 기본 생성자를 알아서 만들어 주지만

임의의 생성자를 한개 이상 작성하였다면 이때 자바 컴파일러는 기본 생성자를 생성해주지 않기 때문에 에러가 발생한다. 

따라서 임의의 생성자를 작성하였을 경우 기본 생성자를 반드시 직접 작성 해 주어야 하며, 실무에서는 특별한 이유가 존재하지 않고서야 기본 생성자를 

열어두지 않는 방향, 즉 제약적으로 개발하는게 일반적이기 때문에 `package-private` 또는 `private`으로 많이 작성하게 되나, 엔티티의 경우 

반드시 `protected` 이상의 접근제한자가 허용되어 있어야 하기 때문에 일반적으론 `protected`로 많이 생성하게 된다.

```java
@Entity
public class Memeber{
    @Id
    private Long id;
    private String name;

    protected Member() {
    }
    // omitted for brevity
}

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // use lombok
public class Memeber{
    @Id
    private Long id;
    private String name;
    // omitted for brevity
} 
```

---

# 🚀 @Table

엔티티와 매핑할 데이터베이스 테이블을 지정한다.

생략하면 엔티티 이름을 기본값으로 사용한다고 하는데, 

여기서 말하는 엔티티 이름이 `@Entity(name = "")`인지, `엔티티 클래스의 이름`인지는 명확하지 않다.

추후 따로 테스트를 통해 알아봐야겠다.

![image](https://user-images.githubusercontent.com/71188307/123519648-99241400-d6e7-11eb-9b61-68288db297ec.png)

<br />

|속성|기능|기본값
|---|---|---|
|name|JPA에서 사용할 엔티티 이름을 지정한다.<br /> 이후 JPQL등에서 이 이름을 사용한다.<br /> 프로젝트 전체에서 고유한 이름을 설정해야만 충돌로 인한 문제가 발생하지 않는다.|클래스 이름을 그대로 사용한다.|
|catalog|catalog 기능이 있는 DB에서 catalog를 매핑한다.| - |
|schema|schema 기능이 있는 DB에서 scheme를 매핑한다|-|
|uniqueConstraints|DDL 생성 시 복합 유니크 인덱스를 생성한다. <br /> 단일 유니크 인덱스도 만들 수 있으나, <br />후술할 `@Column`에 더 좋은 기능이 존재하므로 잘 사용하지 않는다. <br />이 기능은 `설정파일의 DDL 옵션`에 따라 동작한다.|-|

> DDL 옵션
> 
> `create`: 애플리케이션 시작 시 모든 스키마를 드랍 후 새로 생성
> 
> `create-drop`: 애플리케이션 시작 시 모든 스키마 드랍 후 생성, 애플리케이션 종료 시 모든 스키마 드랍
> 
> `update`: 애플리케이션 시작 시 기존 스키마에서 변경된 내역을 적용
> 
> `validate`: 애플리케이션 시작 시 스키마가 제대로 매핑되는지 유효성 검사만 진행. 실패하면 애플리케이션을 종료
> 
> `none`: JPA DDL 옵션을 사용하지 않음
> 
> ```java
> #application.yaml
> spring:
>   jpa:
>     hibernate:
>       ddl-auto: create-drop
> ```
>

<br />

### 💥 DDL 옵션은 항상 신중하게 확인해야 한다 💥

실무에서 이 옵션으로 인해 중요한 테이블이 통째로 드랍되거나, 중요한 데이터가 삭제되는 등의 많은 사고가 발생한다.

실제로 필자도 토이 프로젝트에서 테이블을 날려먹어본 경험이 있으며, 이 때 백업을 해놓지 않아 굉장한 고생을 했었다.

이러한 현상은 주니어 시니어를 가리지 않고 JPA를 처음 접한 대부분의 개발자라면 마치 당연하게 겪는 성장통처럼 느껴질 정도다.

다만 ***실무에서 만큼은*** 이러한 사고를 치지 않도록 항상 스스로 주의해야 할 것이다.

<br />

[재난급 서버 장애내고 개발자 인생 끝날뻔 한 썰 - 납량특집! DB에 테이블이 어디로 갔지?](https://youtu.be/SWZcrdmmLEU)

![image](https://user-images.githubusercontent.com/71188307/123533309-3c5c4400-d74f-11eb-9e6f-0c8a5f029c68.png)

- 출처: [개발바닥 유튜브](https://www.youtube.com/channel/UCSEOUzkGNCT_29EU_vnBYjg)

---

# 🚀 @Id, @GeneratedValue

![image](https://user-images.githubusercontent.com/71188307/123519663-b0fb9800-d6e7-11eb-8874-f133ad697c92.png)

일반적으로 `@Id`를 붙인 필드는 테이블의 기본키(PK)와 매핑된다.

다만 오해하지 않아야 할 것은 반드시 물리적인 기본키와 매핑될 필요는 없다는 것이다.

물리적인 기본키가 무슨 말이냐면, 데이터베이스에 설정되어있는 기본키를 말함이다.

`Hibernate 공식 문서`에서는 `@Id`가 무조건 PK와 매핑될 필요가 없으며, 다만 식별할 수 있는 값이기만 하면 된다고 하고 있다.

이는 `영속성 컨텍스트(Persistence Context)`에 저장되는 엔티티들이 `@Id`를 `key`로 하여 `HashMap`으로 저장되는 특징 때문인 것으로 보인다.

하지만 대부분의 상황이 관례적인 방법으로 모두 해결이 가능하기 때문에 일반적으로 데이터베이스의 기본키와 매핑하여 사용되고 있으며

이러한 방법으로 해결할 수 없는 특별한 상황에 대해서는 다른 방법을 강구해야 할 테니, 이런 내용에 대해서는 알아두면 좋겠다 싶었다.

<br />

`Hibernate 공식 문서`에는 `@Id`에 다음과 같은 타입을 지원한다고 적혀있다.

- 자바 원시타입(primitive type): int, long, float, double 등등
- 자바 래퍼타입(wrapper type): Integer, Long 등
- String
- java.util.Date
- java.sql.Date
- java.math.BigDecimal
- java.math.BigInteger

기본키를 사용하는 많은 방법이 존재하지만 실무에서는 일반적으로 `primitive type`은 사용하지 않는다.

왜냐하면 `primitive type`인 `int`를 `id`로 사용한다고 하면, 값을 할당하지 않을 경우 0으로 초기화가 되는데

이럴 경우 id가 0인 데이터에 대해서 id를 할당하지 않았기 때문에 0인 것인지, 아니면 어떠한 의도를 갖고 id를 0으로 할당한 것인지 알 수 없다.

하지만 `Object type`의 경우 `nullable`하기 때문에 값을 초기화하지 않는다면 null이 들어가게 되며

만약 이러한 상황에서 id 값이 0인 데이터를 발견했다면 이는 어떠한 의도가 있기 때문에 id가 0이라는 것을 보장해줄 수 있게 된다.

따라서 위의 이유로 대부분 id 필드로 `Long`을 가장 많이 사용하며, 간혹 `String(UUID)`, `Integer`, `BigDecimal` 등을 사용하는 모습도 볼 수 있다.

만약 당신이 원시타입을 id 필드로 사용하고 있었다면 앞으로는 혹시 모를 곤혹스런 상황이 발생 할 것에 미리 대비하며 `Object type`을 사용하는 습관을 들이자.

<br />

[Hibernate 공식 문서](https://docs.jboss.org/hibernate/orm/5.3/userguide/html_single/Hibernate_User_Guide.html#entity-pojo-identifier)
> We recommend that you declare consistently-named identifier attributes
> 
> on persistent classes and that you use a nullable (i.e., non-primitive) type.

<br />

```java
@Entity
@Table(name = "request_log")
public class RequestLog{
    @Id @GeneratedValue // 아무것도 작성하지 않으면 AUTO
    @Column(name = "request_log_id")
    Long id;
}
```

![image](https://user-images.githubusercontent.com/71188307/123515965-c0251a80-d6d4-11eb-89e4-85d9466fc2f8.png)

각 DB 벤더는 기본키 자동생성 전략이 있는데, 

업계에서 가장 많이 사용되는 벤더인 Oracle, MySQL 은 다음과 같은 기본키 생성전략을 사용한다.
- Oracle: sequence
- MySQL: auto_increment

이러한 전략들을 JPA에서 사용하기 위해 `@GeneratedValue`를 사용하며, `@GeneratedValue`를 생략 할 경우 기본키 생성전략을 사용하지 않고

개발자가 직접 기본키를 할당해줘야 하기 때문에 `@GeneratedValue`는 필수가 아니지만 필수적으로 사용하게 된다.

`@GeneratedValue`를 사용하지 않을 경우 하기와 같은 방식으로 기본키를 직접 할당해줘야 한다.

```java
Member member = new Member();
member.setId(1L)
em.persist(member); // 혹은 memberRepository.save(member);
```

`@GeneratedValue`를 사용 할 경우는 하기와 같다.

```java
Member member = new Member();

// 저장되며 id 자동 생성
em.persist(member); // 혹은 memberRepository.save(member);
```

왜 사용하느냐면, 만약 기본키를 직접 할당해야 할 경우

새로 할당하려는 값이 이미 DB에 저장되어 있다면 기본키 무결성 제약조건을 위반하여 `SQL Exception`이 떨어지게 된다.

하지만 이를 시스템에서 자동 생성하게 한다면 시스템이 알아서 새로 사용 할 수 있는 안전한 값을 찾아내어 생성하기 때문에

이러한 문제가 발생하지 않게 된다. 

`@GeneratedValue`의 기본키 생성전략은 아래와 같다.

```java
@GeneratedValue(strategy = GenerationType.TABLE)

@GeneratedValue(strategy = GenerationType.SEQUENCE)

@GeneratedValue(strategy = GenerationType.IDENTITY)

@GeneratedValue(strategy = GenerationType.AUTO)
```

![image](https://user-images.githubusercontent.com/71188307/123519673-c670c200-d6e7-11eb-8891-a60684136238.png)


Table 생성 전략은 실무에서 거의 사용하지 않으며, 

`MySQL` 혹은 `MariaDB` 같은것을 사용한다면 `IDENTITY`를 일반적으로 사용하고

`Oracle`을 쓴다면 `SEQUENCE`를 사용한다.

외에 토이 프로젝트나 사이드 프로젝트의 경우는 기술전환이 자유롭게 행해지기 때문에 `AUTO`도 많이 사용되는 걸로 보인다.

이러한 기본키 생성 전략은 사실 JPA의 영역이라기 보다 DB의 영역이기 때문에 

이러한 내용에 대해 잘 모르겠다면 JPA가 아닌 DB에 대한 선행 학습이 필요하다고 사료된다.

`IDENTITY`의 경우 한가지 중요한 내용이 있다.

DB의 `auto_increment` 기능을 사용 하고 JPA에서 `@GeneratedValue(strategy = GenerationType.IDENTITY)`를 사용하는 경우

트랜잭션 작업이 들어가야지만 할당해야 할 `id`값을 알 수 있기 때문에 `insert 쿼리`에 대해 쓰기지연 기능이 제대로 동작하지 않고 즉시 `insert 쿼리`가 발생한다.

같은 원리로 `batch insert`도 제대로 동작하지 않기 때문에 이 경우 `JdbcTemplate`나 `Mybatis` 등의 사용을 고려하게 된다.

---

# 🚀 @Column

![image](https://user-images.githubusercontent.com/71188307/123532547-f9976d80-d748-11eb-8dfc-caeef60b304c.png)

엔티티 필드와 테이블 컬럼을 매핑하는데 사용한다.

기본값은 위와 같으며, 주로 사용되는 옵션은 `name`정도다.

나머지는 DDL 옵션과 관계가 있는데 실무에서는 보통 DB구축이 이미 돼있는 상황에 매핑을 하기만 하는 경우가 일반적이고,

혹시 모를 사고를 대비하기 위해 DDL 옵션을 끄거나(none), 유효성 검사(validate)정도만 사용하는 경우가 대부분이기 때문에

나머지 DDL 관련 옵션들은 적용되지 않는다.

|속성|기능|기본값|
|---|---|---|
|name|필드와 매핑할 테이블의 컬럼명|객체의 필드명|
|insertable|엔티티 저장 시 이 필드도 같이 저장 <br /> false로 설정하면 이 필드는 데이터베이스에 저장하지 않음|true|
|updatable|엔티티 수정 시 이 필드도 같이 수정 <br /> false로 설정하면 이 필드는 데이터베이스에 수정하지 않음|true|
|table|하나의 엔티티를 두 개 이상의 테이블에 매핑할 때 사용. <br />|현재 클래스가 매핑 된 테이블|
|nullable|null 허용 여부로, false로 설정하면 DDL 옵션으로 인한 DDL 생성 시<br /> 컬럼에 not null 제약조건이 붙는다|true|
|unique|`@Table`의 `uniqueConstraints`는 복합 유니크 인덱스를 생성할 때 사용하며, <br /> 이 기능은 단일 유니크 인덱스를 생성할 때 사용|-|
|columnDefinition|데이터 베이스 컬럼 정보를 직접 입력한다(Native)|-|
|length|문자 길이 제약 조건, String 에만 사용|255|
|precision|BigDecimal, BigInteger 타입 사용시 사용한다.<br /> 정밀도를 의미하는데, 전체 자릿수를 의미한다|0|
|scale|BigDecimal, BigInteger 타입 사용시 사용한다.<br /> 스케일은 소수점 이하 자리수를 의미한다|0|

---

# 🚀 @Enumerated

![image](https://user-images.githubusercontent.com/71188307/123532762-ff8e4e00-d74a-11eb-965a-d8d18259a8d0.png)

![image](https://user-images.githubusercontent.com/71188307/123532763-06b55c00-d74b-11eb-823b-137c62feedd9.png)

보통 어떤 상태값을 표현하는데 많이들 사용한다.

예로 들만한 대표적인 enum 클래스는 `spring-web`의 `org.springframework.http.HttpStatus` 라고 볼 수 있겠다.

![image](https://user-images.githubusercontent.com/71188307/123533188-3ca80f80-d74e-11eb-8d1a-b01391470053.png)

엔티티에서는 보통 어떤 상태값에 대해 enum 클래스를 작성하고, 이에 부수적인 기능들을 추가하여 엔티티 필드에 매핑해 사용하는데

이때 `Ordinal`, `String` 을 선택한다. 이것에 대해서는 공식이라고 생각해도 좋겠다. 무조껀 `String`을 사용하자.

`Ordinal`은 enum 클래스의 상수 필드 순서를 `Integer` 타입으로 사용하게 되는데, enum 클래스에 변경사항이 생겨

상수 필드의 순서가 바뀐다면 관련된 모든 코드에 에러가 발생하게 된다. (OCP 위반)

`String`은 enum 클래스의 상수 필드명 자체를 사용하기 때문에 enum 클래스에 변경사항이 생겨도 다른 코드에 영향을 주지 않는다. 

`Ordinal`이 `String`에 비해 좋은 점도 분명히 있긴 한데, 실무에서는 사소한 성능상의 이점보다 휴먼에러를 줄이는 것이 더 중요한 과제이기 때문에 

성능상 손해를 좀 보더라도 안전한 코드를 작성하는게 더 좋다고 생각한다.

---

# 🚀 @Temporal

![image](https://user-images.githubusercontent.com/71188307/123533058-5bf26d00-d74d-11eb-8936-5b75ec60c0e3.png)

`Date`와 `Calendar`관련 어노테이션인데, 이 두 클래스는 현 시점에서 자바 플랫폼 라이브러리에서 실패한 클래스이므로 따로 작성하지 않는다.

이 객체들은 별다른 노하우가 없던 자바 초창기에 불변 객체로 만들어지지 못했었고, 이로 인한 많은 문제가 발생하고 있다.

자바 8이 주류로 자리잡고 `Date`와 `Calendar`의 대부분 Public API가 `Deprecated` 됐으며 

`LocalDate`, `LocalDateTime`이 주류로 사용되고 있는 현 시점에서

`Date`와 `Calendar`를 사용한다는 것은 시대를 역행하는 것이라고 생각하기 때문에

이에 관해 궁금하신 분은 다른 자료가 많으니 직접 찾아보는 것을 추천드린다.

<br />

일단 알아야 할 것은 만약 `Hibernate`를 사용하는데 `Date`나 `Calendar`를 사용한다면 `@Temporal`을 반드시 사용해야 하며,

`LocalDate`나 `LocalDateTime`을 사용한다면 별다른 어노테이션을 작성하지 않아도 된다.

자바 초창기에 `Hibernate`에서 자바의 `Date`를 지원하기 위해 작성된 기능이므로 현 시점에서는 거의 사용되지 않는다.

---

# 🚀 @Lob

![image](https://user-images.githubusercontent.com/71188307/123533064-6876c580-d74d-11eb-9133-82d950533243.png)

데이터베이스의 `BLOB`, `CLOB` 타입과 매핑한다.

중요하지 않다고 생각되어 자세한 내용은 작성하지 않는다.

---

# 🚀 @Transient

![image](https://user-images.githubusercontent.com/71188307/123533074-7c222c00-d74d-11eb-8ffc-48e5856a6683.png)

데이터베이스와 무관한 필드임을 명시하는 어노테이션이다.

주로 엔티티에서 데이터베이스 테이블과 관련되지 않는 데이터를 작업하거나 할 때 사용한다.

생각보다 종종 사용할 일이 있었다.

---

# 🚀 @Access

![image](https://user-images.githubusercontent.com/71188307/123533175-226e3180-d74e-11eb-835a-f198df96b8cf.png)

![image](https://user-images.githubusercontent.com/71188307/123533178-2732e580-d74e-11eb-8fd2-5b5418a9fd03.png)


JPA가 엔티티에 접근하는 방식을 지정한다

필드 접근과 프로퍼티 접근의 투트랙이 존재한다.

이 어노테이션을 생략하면 `@Id`의 위치에 따라 결정된다.

우선순위는 `@Access`를 명시하는 것이 더 높다.

```java
@Entity
@Access(AccessType.FIELD) // 생략해도 무방
public class User{
    @Id // 필드에 위치
    private Long id;

    // omitted for brevity
}

@Entity
@Access(AccessType.PROPERTY) // 생략해도 무방
public class User{
    private Long id;

    @Id // 접근자에 위치
    public Long getId() {
        return id;
    }

    // omitted for brevity
}
```
