# 📕 Persistence Context
작성자: 한창훈

---

## 🚀 About Persistence Context

![image](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fdi6KwS%2FbtqWTX1Neqx%2FqHaJqE96vs8MXXOQKoDWk0%2Fimg.png)

`영속성 컨텍스트(Persistence Context)`는 간단하게 설명하자면 애플리케이션과 데이터베이스 사이에 위치한 가상의 데이터베이스이다.

내부 데이터는 `HashMap`으로 구성돼있으며, **_key=value : id=Entity_** 로 돌아간다.

그리고 이 가상 데이터베이스를 관리하는 객체가 `EntityManager`이다.

`EntityManager`는 `Thraed-Safe`하지 않고, 각자 고유한 `Scope`를 갖기 때문에 보통 한 스레드에서 하나만 생성하며

`Transaction`위에서만 제대로 동작하므로 `Transaction` 설정이 매우 중요하다.

`EntityManager`를 생성하기 위해서는 `EntityManagerFactory`가 필요하며 `EntityManagerFactory`는 `DBCP`와 매핑되어있다.

`Hibernate`에서 `EntityManagerFactory`를 구현한 콘크리트 클래스는 `SessionFactoryImpl`이며 아래와 같은 시그니처를 갖는다.

```
public interface SessionFactory extends EntityManagerFactory{}

public interface SessionFactoryImplementor extends SessionFactory{}

public class SessionFactoryImpl implements SessionFactoryImplementor{}
```

`SessionFactoryImpl`에 대해서 공식문서에서는 하기와 같이 설명하고 있다.

> `SessionFactoryImpl`는 `SessionFactory interface`를 구현한 콘크리트 클래스이며, 다음과 같은 책임을 갖는다
>
> -   캐시 구성을 설정한다 (불변성을 갖는다)
> -   `Entity-테이블 매핑,` `Entity 연관관계 매핑` 같은 컴파일성 매핑을 캐시한다
> -   메모리를 인식하여 쿼리를 캐시한다
> -   `PreparedStatements`를 관리한다
> -   `ConnectionProvider`에게 JDBC 관리를 위임한다
> -   `SessionImpl`를 생성해낸다
>
> 또한, 다음과 같은 특징 및 주의사항이 있다.
>
> 이 클래스는 클라이언트에게 반드시 불변 객체로 보여야 하며, 이는 모든 종류의 캐싱 혹은 풀링을 수행할 경우에도 마찬가지로 적용된다.  
> `SessionFactoryImpl`는 `Thread-Safe 하기` 때문에 동시적으로 사용되는 것이 매우 효율적이다. 또한 동기화를 사용할 경우 매우 드물게 사용되어야 한다.

즉, `EntityManagerFactory`는 불변 객체이기 때문에 `Thread-Safe 하며` 이 말인즉슨, `싱글톤 패턴`을 이용하여 계속해서 재활용할 수 있음도 의미한다고 볼 수 있다.

다음은 `싱글톤`과 `정적 팩토리`를 활용하여 `EntityManagerFactory`와 `EntityManager`를 사용하는 예제이다.

```
public class PersistenceFactory {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("persistenceUnitName");

    private PersistenceFactory() {
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}

@SpringBootTest
class JpaTest {
    @Test
    @DisplayName("순수_JPA_테스트_샘플")
    void jpaTest() throws Exception {
        EntityManager em = PersistenceFactory.getEntityManager(); // get Singleton instance
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        // given
        Member member = Member.builder()
                              .name("홍길동")
                              .age(30)
                              .build();

        // when
        try {
            em.persist(member);
            em.flush();

            Member findMember = em.find(Member.class, member.getId());

            // then
            assertThat(findMember).isSameAs(member);
            tx.commit();
        }
        catch(Exception e) {
            tx.rollback();
        }
        finally {
            em.close();
        }
    }
}
```

`영속성 컨텍스트(Persistence Context)`를 사용하기 위해서는 `EntityManager`가 필요하고, `EntityManager`를 사용하기 위해서는 `EntityManagerFactory`가 필요함을 알았으며, `EntityManagerFactory`에 대해 대충이나마 알아보았다.

그렇다면 이제 `EntityManager`에 대해 알아보자.

우선 `EntityManager`는 특이한 성질을 하나 갖는다. `DB Connection`을 `LAZY` 상태로 가져간다는 것인데,

이게 무슨 말이냐면, `EntityManager`가 생성되며 `DBCP`와 매핑된 `EntityManagerFacotry`에서 자연스럽게 `Connection`을 얻지만 이 `Connection`이 꼭 필요한 상황, 즉 후술 할 `flush` 메서드가 호출되는 시점에서야 `Connection`을 사용한다는 점이다.

이는 `Connection`이 과도하게 남용되어 `Connection`이 부족해질 상황을 대비하기 위한 설계 의도로 보인다.

이러한 속성이 있음을 우선 인지하자.

`EntityManager`는 애플리케이션과 데이터베이스 사이에 위치한 가상의 데이터베이스라고 하였었다.

일단 공식문서를 먼저 참고한다.

> `EntityManager` 인스턴스는 `영속성 컨텍스트(PersistenceContext)`와 연결되며, 영속성 컨텍스트는 단순히 `Entity`의 집합이다.
>
> 영속성 컨텍스트 내에서 모든 `Entity` 인스턴스의 생명주기가 관리되며, `EntityManager`의 모든 `Public API`들은 이 생명주기를 관리하는 데 사용된다.

우선 이를 이미지로 보면 다음과 같다.

![image](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FEAuNE%2FbtqWXbjZeim%2FZtefOIORznYkSE48qJZXJK%2Fimg.png)

위 이미지에서 영속성 컨텍스트라고 적혀있는 표는 `영속성 컨텍스트(PersistenceContext)`가 `HashMap`으로 가지고 있는 `Entity`들을 의미한다.

또한 `findById`, `flush` 등의 API들은 이 `영속성 컨텍스트(PersistenceContext)`를 관리하는 `EntityManager`의 API라고 볼 수 있겠다.

일반적으로 `HashMap` Key에 데이터베이스의 PK값이 들어가게 되며,

`Entity` 객체와 `데이터베이스 테이블`을 매핑하고 **_(연관관계 매핑)_** 발생하는 데이터들을 데이터베이스와 직접 통신하는 것이 아닌,

내부적으로 존재하는 `HashMap`을 사용해 `EntityManager`가 가상의 데이터베이스 **_(=영속성 컨텍스트)_** 를 제어하며 처리하는 것이다.

이렇게 함으로써 `DB Connection`을 최소화하여 데이터베이스 부하를 줄일 수 있게 된다.

이후 작업이 끝나거나 작업 중에 `flush`가 호출되면 `영속성 컨텍스트(PersistenceContext)`의 데이터를 모두 한꺼번에 데이터베이스에 반영한다.

대략 이러한 구조로 돌아가며, 이 정도의 설명으로는 아직 감이 잡히지 않을 수 있다. 괜찮다. JPA에서 가장 이해하기 힘든 영역이기 때문이다.

대략적인 감이라도 잡고 많은 자료를 보며 사용하다 보면 이해하는 순간이 반드시 올 것이다.

우선 `영속성 컨텍스트(PersistenceContext)`에 속하고 `EntityManager`가 처리하는 데이터들에는 4가지 상태가 존재한다.

이를 `Entity 생명주기(Life Cycle)`라고 부른다.

설명하기 전에 우선 위의 코드를 다시 첨부한다.

```
@SpringBootTest
class JpaTest {
    @Test
    @DisplayName("순수_JPA_테스트_샘플")
    void jpaTest() throws Exception {
        EntityManager em = PersistenceFactory.getEntityManager(); // get Singleton instance
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        // given
        Member member = Member.builder()
                              .name("홍길동")
                              .age(30)
                              .build();

        // when
        try {
            em.persist(member);
            em.flush();

            Member findMember = em.find(Member.class, member.getId());

            // then
            assertThat(findMember).isSameAs(member);
            tx.commit();
        }
        catch(Exception e) {
            tx.rollback();
        }
        finally {
            em.close();
        }
    }
}
```

`영속성 컨텍스트(PersistenceContext)`에 속한 `Entity`객체는 다음과 같은 4가지 `생명주기(Life Cycle)`를 갖는다.

![image](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbB7G1m%2FbtqWX5X6QAM%2FH51gsvUP1ojcyLUS39zwE1%2Fimg.png)

> `비영속(transient)`
>
> -   Entity가 방금 인스턴스화 되었으며 아직 영속성 컨텍스트와 연관되지 않은 상태
> -   데이터베이스와 동기화되지 않았으며 일반적으로 할당된 식별자 값이 사용되지 않았거나 식별자 값이 할당되지 않은 상태를 의미

```
Member member = Member.builder()
                      .name("홍길동")
                      .age(30)
                      .build();
```

보다시피 `Entity` 객체가 막 생성됐다.

이 객체에는 `id`가 없기 때문에 `EntityManager`는 이 객체와 `영속성 컨텍스트(PersistenceContext)`를 비교하여 비영속 상태로 간주하게 된다.

> `영속(managed or persistent)`
>
> -   Entity에 연관된 식별자가 있으며 영속성 컨텍스트와 연관된 상태
> -   데이터베이스와 논리적으로 동기화되어 있으나 물리적으로는 존재하지 않을 수도 있는 상태

```
em.persist(member);
```

비영속 상태인 `Entity`의 데이터가 `영속성 컨텍스트(PersistenceContext)`에 등록되었으며, 내부적으로 `HashMap`으로 `Entity`를 관리하기 때문에 Key값이 반드시 필요하다.

하지만 방금 등록된 `Entity`는 비영속 상태이므로 Key값으로 써야 할 id가 null이기 때문에 `Entity` 객체에 선언된 `@GeneratedValue`를 참조하여 id를 생성해낸다. 아무것도 설정하지 않을 경우 `AUTO`로 동작하며, `AUTO`는 `DBCP`와 연결된 데이터베이스의 기본 동작을 따라간다.

일반적으로 국내 업계에서 가장 많이 사용하는 상용 데이터베이스는 `MySQL` 혹은 `MariaDB`인데 **_(무료여서)_**, 이 데이터베이스들의 경우 `auto_increment`를 지원하므로 이 경우 `IDENTITY`전략을 통해 사용하게 된다.

```
@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

> `준영속(detached)`
>
> -   Entity에 연관된 식별자가 있지만 더 이상 영속성 컨텍스트와 연관되지 않은 상태(분리된 상태)
> -   일반적으로 영속성 컨텍스트가 close 되었거나 인스턴스가 영속성 컨텍스트에서 제거된 상태를 의미

```
em.detach(member);
```

이 준영속 상태가 처음에 많이 헷갈린다.

하지만 다행스럽게도 준영속 상태와 비영속 상태는 결정적인 차이가 딱 하나 존재한다.

비영속 상태는 식별자가 있을 수도 있고, 없을 수도 있다. 이게 무슨 말이냐면,

상기 비영속 상태 예제는 개발자가 `Entity` 객체 생성 시 id값을 초기화하지 않았으므로 식별자가 존재하지 않는다. **_(Wrapper인 Long이므로 Nullable 하다)_**

하지만 준영속 상태의 경우 식별자가 **_반드시_** 존재한다.

준영속 상태는 객체가 `영속성 컨텍스트(PersistenceContext)`에 한번 속했다가 떨어져 나온 객체. 즉 `영속성 컨텍스트(PersistenceContext)` 내부 `HashMap`에서 `Entity`가 삭제된 상태를 의미하는데,

비영속 상태의 객체를 `EntityManager`를 통해 `영속성 컨텍스트(PersistenceContext)`에 등록하게 되면 id를 어떻게든 생성해내고, 반대로 데이터베이스에서 데이터를 읽어왔다면 데이터베이스 테이블에 기본 키가 없을수가 없기때문에 **_(무결성 제약조건)_** `영속성 컨텍스트(PersistenceContext)`에도 id가 반드시 존재하기 때문이다.

여기서 재미있는 현상이 하나 생기는데, 그렇다면 개발자가 처음 `Entity` 객체를 생성해낼 때 id를 초기화해서 생성한다면 어떻게 될까?

정답은 `EntityManager`가 id가 존재하는 비영속 상태의 `Entity`객체를 비영속 상태가 아닌 준영속 상태로 간주한다.

`EntityManager`입장에서 준영속 상태라는 것은 `영속성 컨텍스트(PersistenceContext)` 내부 `HashMap`에 같은 식별자를 갖는 `Entity`가 존재하지 않는데, `Entity`에는 식별자가 있는 모든 경우를 의미하기 때문이다.

그래서 이런 경우 `persist` 뿐만 아니라 `merge` 메서드도 먹힌다. 비영속 상태임에도 불구하고 말이다.

> `삭제(removed)`
>
> -   Entity에는 연관된 식별자가 있고 영속성 컨텍스트와 연관되어 있지만 데이터베이스에서 삭제되도록 예약된 상태
> -   최종적으로 영속성 컨텍스트와 데이터베이스에서 모두 삭제된다

```
em.remove(member);
```

이 메서드가 호출되면 해당 `Entity`객체는 `EntityManager`를 통해 `영속성 컨텍스트(PersistenceContext)`에서 삭제가 **_예약_** 되며,

이후 `flush`가 호출되면 `영속성 컨텍스트(PersistenceContext)`에서 삭제됨과 동시에 데이터베이스를 향해 `delete쿼리`가 발생한다.

---

## 🚀 1차 캐시

![image](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fb4VXkb%2FbtqV0DQnvHx%2FWdrqKf43s8SZzqOp5gtXqK%2Fimg.png)

`영속(persistent)` 상태의 `Entity`는 모두 이곳에 `HashMap(key(@Id) = value(Entity))`으로 저장된다.

따라서 `식별자(@Id)`가 없다면 제대로 동작하지 못하며 예외를 발생시킨다.

```
// Entity - 비영속(transient)
Member member = new Member();
member.setId("member1");
member.setUsername("member");


// Entity - 영속(persistent)
em.persist(member);


// 1차 캐시 조회
em.find(Member.class, "member1");
```

JPA는 어떠한 명령에 대해 데이터베이스에 접근하기 전에 항상 이 1차 캐시에서 먼저 작업을 진행한다.

이렇게 동작하는 이유는 데이터베이스 접속을 최소화하여 리소스를 효율적으로 사용할 수 있기 때문이다.

따라서 위 코드에서 `em.find()`가 실행될 시점에 실제 데이터베이스에 접근해서 값을 가져오는 게 아니고

1차 캐시에 저장되어 있는 **_(하지만 데이터베이스에는 아직 반영되지 않은)_** member 객체를 가져온다.

```
@Test
public void cashTest() {
    try {

        System.out.println("=================== new Member ===================");
        Member member = Member.builder()
                              .name("강동원")
                              .age(30)
                              .build();
        System.out.println("=================== em.persist ===================");
        em.persist(member);
        System.out.println("==================================================");

        System.out.println("=================== em.find ===================");
        Member findMember = em.find(Member.class, member.getId());
        System.out.println("member = " + findMember);

        tx.commit();
    }
    catch(Exception e) {
        tx.rollback();
    }
}
```

```
=================== new Member ===================
=================== em.persist ===================
Hibernate: 
    call next value for hibernate_sequence
==================================================
=================== em.find ===================
member = Member(id=1, name=강동원, age=30)
Hibernate: 
    /* insert board.Member
        */ insert 
        into
            MEMBER
            (age, name, id) 
        values
            (?, ?, ?)
```

쿼리 순서를 보자.

`em.find()`가 실행된 후에야 `insert쿼리`가 나가고 `select쿼리`는 그 어디에도 보이지 않는다.

왜 이런 현상이 발생하냐면, `em.persist()`가 실행될 때 데이터베이스에 저장된 게 아니고

영속성 컨텍스트의 1차 캐시에 먼저 저장되어있는 상태에서 `em.find()`가 실행됐기 때문이다.

`EntityManager`는 정해진 규칙대로 데이터베이스에 접근하기 전 1차 캐시를 먼저 뒤졌고, 1차 캐시에서 식별자를 통해 알맞은 객체를 찾아 가져온 것이다.

따라서 `select쿼리`를 데이터베이스에 날릴 필요가 없게 된 것이다.

그 후 `tx.commit()`이 실행되는 시점에서야 `flush`가 호출되며 데이터베이스에 `insert쿼리`를 날린 것이다.

`EntityTransaction` 객체의 `commit()` 메서드를 살펴보면 실제로 내부에 `flush` 메서드가 포함되어 있음을 알 수 있다.

```
@Override
public void commit() {
    errorIfInvalid();
    getTransactionCoordinatorOwner().flushBeforeTransactionCompletion(); // flush

    // we don't have to perform any before/after completion processing here.  We leave that for
    // the Synchronization callbacks
    jtaTransactionAdapter.commit();
}
```

다른 예제를 보겠다.

```
@Test
public void cashTest() {
    try {

        System.out.println("=================== new Member ===================");
        Member member = Member.builder()
                              .name("강동원")
                              .age(30)
                              .build();
        System.out.println("=================== em.persist ===================");
        em.persist(member);
        System.out.println("==================================================");

        em.flush();
        em.clear();

        System.out.println("=================== em.find ===================");
        Member findMember = em.find(Member.class, member.getId());
        System.out.println("member = " + findMember);

        tx.commit();
    }
    catch(Exception e) {
        tx.rollback();
    }
}
```

```
=================== new Member ===================
=================== em.persist ===================
Hibernate: 
    call next value for hibernate_sequence
==================================================
Hibernate: 
    /* insert board.Member
        */ insert 
        into
            MEMBER
            (age, name, id) 
        values
            (?, ?, ?)
=================== em.find ===================
Hibernate: 
    select
        member0_.id as id1_1_0_,
        member0_.age as age2_1_0_,
        member0_.name as name3_1_0_ 
    from
        MEMBER member0_ 
    where
        member0_.id=?
member = Member(id=1, name=강동원, age=30)
```

이번엔 중간에 `EntityManger`를 통해 `영속성 컨텍스트(Persistence Context)`를 강제로 `flush` → `clear` 했다.

이는 영속성 컨텍스트에 저장돼있는 `Entity를` 모두 데이터베이스에 `동기화(flush)`하고,

영속성 컨텍스트의 모든 `Entity`를 `제거(clear)`했음을 의미한다.

실제 쿼리가 나가는 순서를 보면 `flush` → `clear` 할 때 `insert쿼리`가 실행됐고,

그 후 `em.find()` 메서드가 실행되며 1차 캐시를 뒤졌지만

알맞은 `Entity`정보를 찾지 못해 그제야 데이터베이스에 접근하며 `select쿼리`가 나감을 확인할 수 있다.

---

## 🚀 동일성 보장

1차 캐시와 연관된 내용이다.

그냥 간단하게 1차 캐시를 통해 모든 작업을 처리하기 때문에 객체의 동일성 **_(메모리 주소가 같음을 의미한다)_** 이 보장된다는 이야기다.

간단하게 코드로 보자.

```
@Test
public void cashTest() {
    try {

        System.out.println("=================== new Member ===================");
        Member member = Member.builder()
                              .name("강동원")
                              .age(30)
                              .build();
        System.out.println("=================== em.persist ===================");
        em.persist(member);

        System.out.println("=================== em.find ===================");
        Member findMember1 = em.find(Member.class, member.getId());
        Member findMember2 = em.find(Member.class, member.getId());
        System.out.println("compare = " + (findMember1 == findMember2));

        tx.commit();
    }
    catch(Exception e) {
        tx.rollback();
    }
}
```

```
=================== new Member ===================
=================== em.persist ===================
Hibernate: 
    call next value for hibernate_sequence
=================== em.find ===================
compare = true
Hibernate: 
    /* insert board.Member
        */ insert 
        into
            MEMBER
            (age, name, id) 
        values
            (?, ?, ?)
```

`em.persist(member)`가 실행되며 1차 캐시에 member `Entity`의 정보가 저장되었고 **_(영속 상태)_**,

```
Member findMember1 = em.find(Member.class, member.getId());
Member findMember2 = em.find(Member.class, member.getId());
```

를 통해 1차 캐시에서 같은 `Entity`객체를 두 번 가져와 각각 다른 레퍼런스 변수에 주소를 할당했다.

따라서 두 객체는 같은 주소를 가지므로 완벽하게 동일하기 때문에

> compare = true

라는 결과가 나온다.

마지막으로 `tx.commit()` 메서드가 실행되며 `insert쿼리`가 나간다.

1차 캐시를 제대로 이해했다면 너무나 당연한 이야기다.

---

## 🚀 쓰기 지연, 지연 로딩, 변경 감지

역시 모두 1차 캐시와 관련된 내용이다.

쓰기 지연은 위 코드와 같이 `insert쿼리`가 `flush`가 호출되는 시점에 발생하는 것을 의미하며,

지연 로딩 또한 마찬가지로 위 코드와 같이 실제 작업이 실행되는 순간에 `select쿼리`가 나가는 현상을 말한다.

각 작업들을 1차 캐시에 저장해 두고 이를 한 번에 작업해서 효율을 올리겠다는 설계 의도로 보인다.

변경 감지의 경우 JPA에는 `update` 관련 API가 따로 존재하지 않는데 이에 대한 내용이다.

`EntityManager`가 `영속성 컨텍스트(Persistence Context)`를 통해 관리하는 `Entity`는 객체의 정보가 변경될 경우 `EntityManager`가 이를 감지하고

`update쿼리`를 자동으로 날려준다.

이는 `영속성 컨텍스트(Persistence Context)` 내부에 각 `Entity` 객체에 대한 `스냅샷(Snap-Shot)`이 존재하기 때문에 가능한 일이다.

역시 코드를 보자.

```
@Test
public void cashTest() {
    try {
        System.out.println("=================== new Member ===================");
        Member member = Member.builder()
                              .name("강동원")
                              .age(30)
                              .build();
        System.out.println("=================== em.persist ===================");
        em.persist(member);

        System.out.println("=================== em.find ===================");
        Member findMember = em.find(Member.class, member.getId());

        System.out.println("=================== em.update ===================");
        findMember.setName("강동원아님");

        tx.commit();
    }
    catch(Exception e) {
        tx.rollback();
    }
}
```

```
=================== new Member ===================
=================== em.persist ===================
Hibernate: 
    call next value for hibernate_sequence
=================== em.find ===================
=================== em.update ===================
Hibernate: 
    /* insert board.Member
        */ insert 
        into
            MEMBER
            (age, name, id) 
        values
            (?, ?, ?)
Hibernate: 
    /* update
        board.Member */ update
            MEMBER 
        set
            age=?,
            name=? 
        where
            id=?
```

두 눈을 씻고 다시 봐도 위 코드 어디에도 `em.update(member) 같은` 코드는 보이지 않는다

그럼에도 불구하고 실제로 `update쿼리`가 날아갔다.

하지만 가만 보면 살짝 이상한 부분이 보인다.

`insert쿼리`가 먼저 한번 나간 후 `update쿼리`가 나간다.

이는 JPA가 동작하는 내부적인 방식으로 인한 현상인데.

`em.persist(member)`가 실행될 때 `영속성 컨텍스트(Persistence Context)`에 member객체가 저장되며

이때의 상태(최초 상태)를 따로 저장하는데 이를 바로 `스냅샷(Snap-Shot)`이라 한다.

그리고 `EntityManager`는 트랜잭션이 `commit` 되는 시점에 현재 `영속성 컨텍스트(Persistence Context)`의 정보와 `스냅샷(Snap-Shot)`의 정보를 비교하여 update 쿼리를 작성하기 때문에 최초에 insert 쿼리가 한 번 날아간 후 비교를 통해 작성된 `update쿼리`가 한번 더 날아가는 것이다.

---

## 🚀 검증

아래의 예제는 `@GeneratedValue(strategy = GenerationType.IDENTITY)`가 아닌, `@GeneratedValue(strategy = GenerationType.SEQUENCE)`로 진행하였다.

```
@Test
public void persistenceContextTest() throws Exception {
    try {
        Member member = Member.builder()
                              .name("홍길동")
                              .age(30)
                              .build();

        System.out.println("=============== persist() ===============");
        em.persist(member);

        System.out.println("=============== detach() ===============");
        em.detach(member);

        System.out.println("=============== commit() ===============");
        tx.commit();

    }
    catch(Exception e) {
        System.out.println("EXCEPTION -> " + e.getMessage());
        tx.rollback();
    }
}
```

![image](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FcETBt8%2FbtqWXaFkCf3%2F83HweDUe1gWMR6AlYZewyK%2Fimg.png)

`em.persist()`가 실행되는 시점에 쓰기 지연 저장소에 `insert쿼리`를 작성하기 위해

`hibernate_sequence`에 채번을 요청했음을 알 수 있다.

> Hibernate: call next value for hibernate\_sequence

해당 시점에 1차 캐시에는 `hibernate_sequence`에서 얻어온 식별자 값으로

member의 정보와 member의 `insert쿼리`가 들어있는 상태이다.

직후 member는 `detach` 메서드로 인해 `준영속 상태`가 되었으므로

1차적으로 1차 캐시에서 member의 정보가 삭제되고,

2차적으로 쓰기 지연 SQL 저장소에 저장되어있던 `insert쿼리`도 삭제되었다.

따라서 커밋 시점엔 아무런 쿼리도 나가지 않았다.

---

```
@Test
public void persistenceContextTest() throws Exception {
    try {
        Member member = Member.builder()
                              .name("홍길동")
                              .age(30)
                              .build();

        System.out.println("=============== persist() ===============");
        em.persist(member);

        System.out.println("=============== flush() ===============");
        em.flush();

        System.out.println("=============== detach() ===============");
        em.detach(member);

        System.out.println("=============== merge() -> update ===============");
        Member mergeMember = em.merge(member);
        System.out.println("병합 시점의 이름은 = " + mergeMember.getName());
        mergeMember.setName("홍길동 아니다");
        System.out.println("수정 후 이름은 = " + mergeMember.getName());

        System.out.println("=============== commit() ===============");
        tx.commit();

    }
    catch(Exception e) {
        System.out.println("EXCEPTION -> " + e.getMessage());
        tx.rollback();
    }
}
```

![image](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FWzylL%2FbtqWUpwWMO2%2FPOphoiEdZOQsWMas8tvRz1%2Fimg.png)

준영속 상태와 비영속 상태가 다른 점은 식별자의 유무라고 하였다.

비영속 상태는 식별자가 있을 수도 있고 없을 수도 있다.

준영속 상태는 비영속 상태와 거의 동일하지만 한번 영속 상태였다가 영속 상태가 아니게 된 경우이므로

준영속 상태는 반드시 식별자 값을 가지고 있다.

위 코드의 실행 흐름을 보면 처음 member 객체는

홍길동이라는 이름으로 비영속 상태가 되었고 `persist` 되어 `영속 상태`가 되었다.

그리고 `flush`가 호출되며 해당 정보는 데이터베이스에 반영되며 `insert쿼리`가 나갔다.

직후 `detach` 실행 후 `준영속 상태`로 변경되며 영속성 컨텍스트에서 해당 객체가 삭제된 상태에서

다시 바로 `merge`가 호출됐고, 데이터베이스와의 동기화를 위해 객체가 들고 있는 식별자로 `select쿼리`를 날리는 걸 확인할 수 있다.

```
병합 시점의 이름은 = 홍길동
수정 후 이름은 = 홍길동 아니다
```

그래서 위와 같은 메시지가 콘솔에 출력되었고

이후 `영속성 컨텍스트(Persistence Context)`가 변경 감지를 통해 `update쿼리`를 발생시킨다.

---

```
@Test
public void persistenceContextTest() throws Exception {
    try {
        System.out.println("=============== member 인스턴스 생성 ===============");
        Member member = Member.builder()
                              .name("홍길동")
                              .age(30)
                              .build();
        System.out.println("em.contains(member) = " + em.contains(member));

        System.out.println("=============== persist ===============");
        em.persist(member);
        System.out.println("em.contains(member) = " + em.contains(member));

        System.out.println("=============== detach ===============");
        em.detach(member);
        System.out.println("em.contains(member) = " + em.contains(member));

        System.out.println("=============== commit ===============");
        tx.commit();
    }
    catch(Exception e) {
        System.out.println("EXCEPTION -> " + e.getMessage());
        tx.rollback();
    }
}
```

![image](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fvtuqz%2FbtqWXbxta4n%2FHpNlNNiGaIUw8xQejBq5G0%2Fimg.png)

`em.contains` 메서드는 해당 `Entity`가 `영속성 컨텍스트(Persistence Context)`에 존재하는 객체인지 아닌지를 boolean 값으로 출력해주는 메서드다.

member 객체가 처음 생성될 때(비영속 상태) contains는 `false`를 반환한다.

이후 `persist 되어` 영속 상태가 된 시점에는 `true`를 반환한다.

다시 `detach 되어` 준영속 상태가 된 시점에는 `false`를 반환하는 것을 확인할 수 있다.

---

```
@Test
public void persistenceContextTest() throws Exception {
    try {
        System.out.println("=============== member 인스턴스 생성 ===============");
        Member member = Member.builder()
                              .id(1L)
                              .name("홍길동")
                              .age(30)
                              .build();
        System.out.println("em.contains(member) = " + em.contains(member));

        System.out.println("=============== merge ===============");
        Member mergeMember = em.merge(member);
        System.out.println("em.contains(member) = " + em.contains(member));
        System.out.println("em.contains(mergeMember) = " + em.contains(mergeMember));

        System.out.println("=============== commit ===============");
        tx.commit();
    }
    catch(Exception e) {
        System.out.println("EXCEPTION -> " + e.getMessage());
        tx.rollback();
    }
}
```

member 객체는 `비영속 상태`이지만 **_식별자 값을 갖고 있기 때문_** 에

`em.merge(member)가` 먹히며 `영속성 컨텍스트(Persistence Context)`는 member 객체가 `merge` 되는 시점에

데이터베이스와의 동기화를 위해 member 객체가 가지고 있는 식별자 값으로 `select쿼리`를 날렸고,

데이터베이스에는 id=1에 해당하는 정보가 없기 때문에

영속성 컨텍스트에 등록하기 위해 채번을 요청하였음을 확인할 수 있다.

```
Hibernate: 
    call next value for hibernate_sequence
```

![image](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FdytvDH%2FbtqWVQ8RU3H%2Ftus6257JrNREeekbtMcAMk%2Fimg.png)

그리고 한 가지 더 흥미로운 점이 있다.

![image](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FofayM%2FbtqWUM0bhKE%2FjrRsEGcFXNwyUglzDaKJh1%2Fimg.png)

`merge`는 준영속 객체 자체를 바로 `영속성 컨텍스트(Persistence Context)`에 올리는 것이 아니고

새로운 객체를 만들어서 영속성 컨텍스트에 올린다는 것을 위의 로그로 확인할 수 있다.

그리고 영속성 컨텍스트에는 정보가 있지만 데이터베이스에는 정보가 없으므로 이 또한 동기화를 위해

커밋되는 시점에 merge 된 member 객체의 정보를

데이터베이스에 `insert쿼리`를 통해 등록하는 모습을 확인할 수 있다.

그러므로 `merge`는 실제로 CRUD 상에서

`Create` 또는 `Update` 기능을 동시에 수행할 수 있다고 볼 수 있겠다.

---

이런 간단한 검증실험을 통해 알 수 있는 것은

`영속성 컨텍스트(Persistence Context)`는 애플리케이션과 데이터베이스 사이에서 가상의 데이터베이스 역할을 수행한다는 것이다.

그래서 트랜잭션이 `commit 되거나` `flush`가 직접 호출되는 되는 시점에

모든 식별자 값을 통해 영속성 컨텍스트의 내용을 실제 데이터베이스의 내용과 동기화시킨다는 점이다.

```
@Test
public void persistenceContextTest() throws Exception {
    try {
        System.out.println("=============== findById ===============");
        Member member1 = em.find(Member.class, 1L);
        Member member2 = em.find(Member.class, 2L);
        Member member3 = em.find(Member.class, 3L);

        System.out.println("=============== member1 update ===============");
        member1.setName("나 홍길동 아니다");
        member1.setAge(1);

        System.out.println("=============== member2 update ===============");
        member2.setName("동방삭");
        member2.setAge(9999);

        System.out.println("=============== commit ===============");
        tx.commit();
    }
    catch(Exception e) {
        System.out.println("EXCEPTION -> " + e.getMessage());
        tx.rollback();
    }
}
```

![image](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FsSlx6%2FbtqWVRzxUyi%2FGAhJ4mMyaHILNvDqpEhZgk%2Fimg.png)

`em.find()를` 통해 3가지 객체에 대한 정보를 가져오므로 3번의 `select쿼리`가 발생하고

그중 2가지 객체의 정보를 변경하였으므로 2번의 `update쿼리`가 발생한다.

이를 이미지를 통해 보면 아래와 같다.

![image](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FEAuNE%2FbtqWXbjZeim%2FZtefOIORznYkSE48qJZXJK%2Fimg.png)

---

JPA를 사용할 때 가장 중요하게 생각해야 할 점은 `영속성 컨텍스트(Persistence Context)`에 대한 이해이다.

`영속성 컨텍스트(Persistence Context)`는 애플리케이션과 데이터베이스의 사이에 존재하는 **_가상의 데이터베이스_** 이다.

이 점을 명심해야 할 것이다.
