# 📕 Entity Mapping Basic
작성자: 한창훈

---

ERD 에는 총 4가지 관계가 있다.

이를 JPA의 연관관계 매핑으로 매치시키면 하기와 같다.

- 일대일(1:1) - `@OneToOne`
- 일대다(1:N) - `@OneToMany`
- 다대일(N:1) - `@ManyToOne`
- 다대다(N:N) - `@ManyToMany`

---

# 📜 @OneToOne

![image](https://user-images.githubusercontent.com/71188307/123915813-dfb19100-d9bb-11eb-9ee5-24d96712c960.png)

|속성|기능|기본값|
|---|---|---|
|targetEntity|연관관계 매핑의 대상이 되는 타겟을 설정한다.|field의 class명|
|cascade|database의 cascade와 동일하다.|-|
|fetch|로딩 전략. EAGER가 기본값이며, EAGER일 경우 즉시로딩을 의미한다. <br /> LAZY는 지연로딩을 의미한다.|EAGER|
|optional|null인지 아닌지를 설정한다. 기본값은 true이며 nullable함을 의미한다.|true|
|mappedBy|연관관계의 주인을 설정한다. 주인이 아닌 곳에서 선언한다.|-|
|orphanRemoval|고아 객체 처리를 어떻게 할지에 대한 설정이다.|false|


일반적으로 선수는 캐비넷을 하나씩 갖는다.

이를 일대일관계라고 부르며 이 경우 부모는 선수, 자식은 캐비넷이라고 볼 수 있을 것이다.

이때, 선수가 캐비넷이 필요없다고 하면 없을수도 있다(nullable).

이를 JPA의 `@OneToOne`으로 풀어낸다면

```java
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private int age;

    @OneToOne
    private Cabinet cabinet;

    // omitted for brevity
}
```

```java
@Entity
public class Cabinet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private Integer number;
    
    // omitted for brevity
}
```

이런 식으로 사용할 수 있을 것 같다.

`@OneToOne`의 경우 실무에서 여러가지 문제를 발생시키는 JPA의 만악의 근원중 하나다.

우선 `@OneToOne`의 경우 지연로딩전략이 잘 먹히지 않으며, 지연로딩을 하기 위해서는 다음과 같은 조건을 만족해야 한다.

- `not null`이어야 한다. 즉, `optional=false``여야만 한다.
- 반드시 단방향 일대일 관계여야만 한다.
- 반드시 `PK(Primary Key)`로 조인하여야만 한다.

`LAZY`의 경우 `프록시`를 활용하여 동작하게 되는데, 값이 `null`인 경우에는 프록시로 대체할수가 없다.

매핑되는 객체가 Collection 타입일 경우 실제 내부 엘리먼트가 null이건 아니건 상관없이 Collection 자체를 프록시로

대체할 수 있기 때문에 지연로딩이 매우 잘 먹히는데 반해 

`@OneToOne`의 경우 `Collection`으로 매핑하지 않고 단일 객체로 매핑하기 때문에

JPA에서 값이 null인 경우를 배제할 수 없게 된다.

그래서 JPA는 이 경우 프록시를 채울 수 없게되고, 반드시 쿼리를 한번 발생시켜 값이 있는지 없는지를 검사해야만

null을 채울지 프록시를 채워줄지를 알 수 있다. 이러한 원리로 인해 반드시 `EAGER`로 동작하게 되며 N+1문제가 빈번히 발생한다.

따라서 개발자가 해당 객체는 not null임을 명시해준다면(optional=false) JPA는 값이 null일 경우를 배제할 수 있게되어

프록시를 채워주게 되고, 이는 곧 지연로딩으로 동작할 수 있음을 의미하게 된다.

<br />

보통 일대일 관계를 반드시 사용해야만 하는 경우가 드문데,

만약 JPA를 사용하는 중 반드시 일대일 관계를 사용해야만 하는 상황일 경우에는 꼭 부모테이블에 FK를 두도록 하고

일대일 관계가 아니게 될 수 있다면 애초에 처음부터 일대다나 다대일관계로 만드는 것이 추후 사고를 예방하는 길이 되겠다.

---

# 📜 @OneToMany

![image](https://user-images.githubusercontent.com/71188307/123917965-58195180-d9be-11eb-8411-6297c85abe94.png)

|속성|기능|기본값|
|---|---|---|
|targetEntity|연관관계 매핑의 대상이 되는 타겟을 설정한다.|field의 class명|
|cascade|database의 cascade와 동일하다.|-|
|fetch|로딩 전략. LAZY가 기본값이며, LAZY일 경우 지연로딩을 의미한다. <br /> EAGER는 지연로딩을 의미한다.|LAZY|
|mappedBy|연관관계의 주인을 설정한다. 주인이 아닌 곳에서 선언한다.|-|
|orphanRemoval|고아 객체 처리를 어떻게 할지에 대한 설정이다.|false|

필자는 `@OneToMany`도 역시 잘 사용하지 않는다.

우선 전통적인 RDB방식에 어긋나는 방식이다.

일반적으로 RDB에서는 자식 테이블이 부모 테이블의 기본키를 외래키(FK)로 갖고있기 때문이다.

실무에서는 일반적으로 양방향 관계를 설정할 경우에 종종 사용하는데, 

필자는 가급적 양방향 관계를 걸지 않으려고 하기 때문에 많이 써본 적은 없는 것 같다.

굳이 사용 예를 들자면 보통 한 팀에는 여러명의 선수가 소속되므로 팀 입장에서 선수와 일대다 관계이다.

```java
@Entity
public class Team {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String name;

    @OneToMany
    private List<Member> members;
}
```

양방향 관계에서 연관관계의 주인이 아님을 명시하여 사용하는 예이다.

이런 코드는 생각보다 많이 볼 수 있다.

```java
@Entity
public class Team {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members;
}

```

---

# 📜 @ManyToOne

![image](https://user-images.githubusercontent.com/71188307/123918400-ce1db880-d9be-11eb-883e-239b9d2173ff.png)

|속성|기능|기본값|
|---|---|---|
|targetEntity|연관관계 매핑의 대상이 되는 타겟을 설정한다.|field의 class명|
|cascade|database의 cascade와 동일하다.|-|
|fetch|로딩 전략. EAGER가 기본값이며, EAGER일 경우 즉시로딩을 의미한다. <br /> LAZY는 지연로딩을 의미한다.|EAGER|
|optional|null인지 아닌지를 설정한다. 기본값은 true이며 nullable함을 의미한다.|true|

JPA를 사용하면서 가장 많이 사용하게 될 어노테이션이고, 가장 많이보게 될 어노테이션이다.

절대다수의 RDB 설계상 자식 테이블이 부모 테이블의 기본키를 외래키(FK)로 갖고 있으며, 보통 DB 설계를 먼저 해놓고

JPA가 따라가며 매핑하는 방식으로 일을하게 되기 때문에 이 어노테이션을 많이 사용하게 된다.

`@ManyToOne`에도 `optional`이 있는데 이 경우에는 부모 테이블이 확실하게 존재하기 때문에 

부모객체가 null일 확률을 배제할 수 있다. 따라서 별다른 문제 없이 지연로딩 전략이 잘 적용된다.

N+1 문제를 회피하기 위해 `fetch`옵션을 항상 `LAZY`로 설정하여 지연로딩 전략으로 사용한다.

사용 예를 들자면 보통 한 팀에는 여러명의 선수가 소속되므로 선수 입장에서 팀과 다대일 관계이다.

이를 코드로 표현하면 하기와 같다.

```java
@Entity
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;
}
```

---

# 📜 @ManyToMany

![image](https://user-images.githubusercontent.com/71188307/123919776-4042cd00-d9c0-11eb-8867-5c9cb478e324.png)

|속성|기능|기본값|
|---|---|---|
|targetEntity|연관관계 매핑의 대상이 되는 타겟을 설정한다.|field의 class명|
|cascade|database의 cascade와 동일하다.|-|
|fetch|로딩 전략. LAZY가 기본값이며, LAZY일 경우 지연로딩을 의미한다. <br /> EAGER는 지연로딩을 의미한다.|LAZY|
|mappedBy|연관관계의 주인을 설정한다. 주인이 아닌 곳에서 선언한다.|-|

다대다 관계인데, RDB에서는 이를 보통 매핑 테이블로 뽑아내서 사용한다.

JPA에서도 역시 이 어노테이션을 쓰기보다 매핑 테이블과 일대다, 다대일관계로 많이 사용하게 된다.

이유로는 `@ManyToMany`를 사용하면 관련 코드가 매우 복잡해져서 한눈에 잘 안들어오며 확장성이 매우 떨어진다. 

또한 안 그래도 복잡한 JPA인데 이정도로 추상화를 해버리면 팀 동료가 힘들어지는 경우가 생길 수 있다.

필자는 아예 사용자체를 안해서 잘 모르기도 하거니와 앞으로도 쓸 이유가 없을 것 같아 따로 더 서술하진 않겠다.

다만 굳이 이 어노테이션을 사용해야겠다면 가급적 `List`보다는 `Set`을 사용하는게 좋을 것 같다.