# 📕 JPA(Java Persistence API)
작성자: 한창훈

---

## 🚀 About JPA

![image](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FtMvTg%2FbtqUL9JlI78%2FTf6mCkkrelMZCxgqfK99qk%2Fimg.png)

`JPA`는 `ORM`을 `Java` 플랫폼에 호환되게 사용하기 위한 `요구사항을 규격화한 표준 API`이다.

이 API는 `javax.persistence` 패키지에 정의돼있으며, 문자 그대로 대부분의 `요구사항`이 `정의`만 되어 있다.

![image](https://user-images.githubusercontent.com/71188307/122570766-0819c100-d087-11eb-87d8-cbdddb76570d.png)

패키지를 까보면 대부분의 클래스가 `interface`임을 알 수 있다.

여러 벤더에서 이 API를 구현하여 제공하고 있는데 구현체를 제공하는 벤더들은

`Eclipse Link`, `Data Nucleus`, `OpenJPA`, `TopLink Essentials`, `Hibernate` 등이 있으며,

2021년 현재 전 세계적으로 가장 많이 사용되는 벤더는 `Hibernate`이다.

위키백과에 따르면 2019년부터 JPA는 `Jakarta Persistence`로 이름이 바뀌며 v3.0이 출시됐으며, 

위 이미지는 `Spring-Boot` 최신 버전에서 캡처한 이미지로, jar이름도 `jakarta.persistence`임을 알 수 있다.

`Jakarta Persistence v3.0`의 요구사항을 만족하는 구현체를 제공하는 벤더는 총 3종류로

`Data Nucleus v6.0`, `EclipseLink v3.0`, `Hibernate v5.5`이다.

```text
Jakarta Persistence 3.0
Renaming of the JPA API to Jakarta Persistence happened in 2019, followed by the release of v3.0 in 2020.

Main features included were:

Rename of all packages from javax.persistence to jakarta.persistence.
Rename of all properties prefix from javax.persistence to jakarta.persistence.
Vendors supporting Jakarta Persistence 3.0

DataNucleus (from version 6.0)
EclipseLink (from version 3.0)
Hibernate (from version 5.5)
```

어떤 벤더를 사용하더라도 JPA라는 API가 강제돼있기 때문에 내부적으로 동작이 다를 순 있으나 실제 사용법에는 큰 차이가 없다.

---

## 🚀 About Spring-Data-JPA

![image](https://user-images.githubusercontent.com/71188307/122573610-dc4c0a80-d089-11eb-9ee1-114b2486b651.png)

JPA를 구현한 순수 구체 클래스를 그대로 사용할 경우 이런 저런 설정을 매번 따로 해줘야 하는 경우가 많은데,

이를 한단계 더 추상화하여 자주 사용하는 기능들을 편리하게 쓸 수 있게 만들어 둔 계층이다.

개인적으로 가장 많이 사용하게 되는것은 `org.springframework.data.jpa.repository` 패키지의 `SimpleJpaRepository`이다.

실제로 가장 많이 사용되는 `save`, `saveAll`, `delete`, `deleteAll`, `findById`, `findBy~` 등의 API들이

`SimpleJpaRepository`에 정의돼있으며, 실제 구현부 코드는 JPA를 래핑한 수준의 아주 단순한 코드들이다.

```java
@Transactional
@Override
public <S extends T> S save(S entity) {

    Assert.notNull(entity, "Entity must not be null.");

    if (entityInformation.isNew(entity)) {
        em.persist(entity);
        return entity;
    } else {
        return em.merge(entity);
    }
}

@Transactional
@Override
public <S extends T> List<S> saveAll(Iterable<S> entities) {
    
    Assert.notNull(entities, "Entities must not be null!");
    
    List<S> result = new ArrayList<S>();
    
    for (S entity : entities) {
        result.add(save(entity));
    }
    return result;
}

@Override
@Transactional
@SuppressWarnings("unchecked")
public void delete(T entity) {
      
    Assert.notNull(entity, "Entity must not be null!");
    
    if (entityInformation.isNew(entity)) {
        return;
    }
    
    Class<?> type = ProxyUtils.getUserClass(entity);
    
    T existing = (T) em.find(type, entityInformation.getId(entity));
    
    // if the entity to be deleted doesn't exist, delete is a NOOP
    if (existing == null) {
        return;
    }
    
    em.remove(em.contains(entity) ? entity : em.merge(entity));
}
```

JPA를 처음 접하는 사람은 아직 잘 모르겠지만, JPA의 모든 동작은 `transaction` 위에서 진행되어야 작업이 반영되기 때문에,

`SimpleJpaRepository`의 모든 public API에는 `@Transactional`이 작성돼있다.

만약 JPA를 학습하고자 하는데 `transaction`이 뭔지 잘 모른다면 이 부분에 대한 선행학습이 필수적으로 요구된다고 볼 수 있겠다.

---

## 🚀 JPA 동작 원리

![image](https://user-images.githubusercontent.com/71188307/122574473-a4919280-d08a-11eb-93b7-8272704a5d65.png)

굉장히 복잡하게 구성돼있는데, 간단하게 핵심만 생각하자면 모든 작업이 `EntityManager` 위주로 돌아간다.

`EntityManager`는 `영속성 컨텍스트`라는 이름의 가상 데이터베이스를 관리하는 객체이기 때문이다.

DB 작업이 필요한 시점에 `EntityManager`를 생성해야 하는데, 이때 `DataSource`와 매핑된 `Persistence`에서 `connection`을 얻어와 

`EntityManagerFactory`가 `EntityManager`를 생성해주고, `EntityManager`는 `connection`을 Lazy 상태로 가진다.

그리고 실제 작업이 시작되는 타이밍에 `EntityManager`는 `EntityTransaction`객체를 생성해 `transaction`을 시작하며 `connection`을 연결한다.

이후 작업이 끝나면 `EntityTransaction`을 폐기하며 `transaction`을 종료하고 `EntityManager`또한 폐기한다.

`EntityManager`까지 폐기하는 이유는 `EntityManager`가 `Thread-Safe`하지 않기 때문이며, `EntityManager` 인스턴스는 매 작업마다 새로 생성된다.

![image](https://user-images.githubusercontent.com/71188307/122577926-567e8e00-d08e-11eb-9740-be932a5bbb13.png)

이 과정이 대략 아래와 같은 코드로 나타난다.

```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook"); //엔티티 매니저 팩토리 생성
EntityManager em = emf.createEntityManager(); //엔티티 매니저 생성
EntityTransaction tx = em.getTransaction(); //트랜잭션 획득

try {
    tx.begin(); //트랜잭션 시작
    logic(em);  //비즈니스 로직
    tx.commit();//트랜잭션 커밋
} catch (Exception e) {
    tx.rollback(); //트랜잭션 롤백
} finally {
    em.close(); //엔티티 매니저 종료
}

emf.close(); //엔티티 매니저 팩토리 종료
```

`Spring-Data-JPA`를 사용할 경우 위의 작업은 대부분 신경쓰지 않아도 되지만, 

만약 순수 JPA를 사용한다면 `EntityManagerFactory`는 싱글톤 등의 기법을 사용하여 애플리케이션 전체에서 재활용하는게 비용상 좋다.

---

## 🚀 JPA 사용

2021년 6월 기준 신규 프로젝트는 대부분 `Spring-Boot`으로 시작하기 때문에 `xml`기반의 설정을 제외하며,

`Maven`보다는 `Gradle`이 아주 활발하게 사용되고 있으므로 `Gradle` 설정으로 대체한다.

데이터베이스는 `H2`로 설정하고, `boilerplate code`를 작성하는 수고를 줄이기 위해 `lombok`을 추가한다.

```groovy
//build.gradle

dependencies {
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'
    
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'com.h2database:h2'
}
```

```yaml
#application.yaml
spring.datasource.url=jdbc:h2:tcp://localhost/~/learn-jpa
spring.datasource.username=sa
spring.datasource.password=
```


```java
@Entity // JPA 기능을 사용하기 위한 객체임을 명시 (테이블과 매핑되는 객체) 
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member implements Serializable {
    private static final long serialVersionUID = 3990803224604257521L;
    
    @Id // 해당 필드가 DB의 PK임을 명시 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 기본키 생성전략을 auto_increment로 설정
    // DB가 auto_increment를 지원하지 않으면 sequence로 설정된다
    private Long id;
    private String name;
    private int age;
    
    @Builder
    public Member(Long id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
}
```


```java
@DataJpaTest
class MemberTest {
    @PersistenceUnit
    EntityManagerFactory emf; // EntityManagerFactory 생성
    
    EntityManager em;
    EntityTransaction tx;
    
    @BeforeEach
    void setUp() { // 테스트케이스가 시작되면 먼저 실행될 코드블럭
        em = emf.createEntityManager(); // EntityManagerFactory에서 EntityManager 생성
        tx = em.getTransaction(); // EntityManager에서 trasaction 획득
    }
    
    @AfterEach
    void tearDown() { // 테스트케이스가 종료되면 실행될 코드블럭
        em.close();
        emf.close();
    }
    
    @Test
    void memberTest() throws Exception {
        tx.begin(); // transaction start
        // given
        Member member = Member.builder()
                              .name("홍길동")
                              .age(30)
                              .build(); // Member 인스턴스 생성
        
        // when
        em.persist(member); // Member 인스턴스를 영속성 컨텍스트에 저장
        em.flush(); // 영속성 컨텍스트의 변경사항을 DB에 반영
        
        Member findMember = em.find(Member.class, member.getId()); // 영속성 컨텍스트에서 ID로 Member를 조회
        
        
        // then
        assertThat(findMember).isSameAs(member); // 영속성 컨텍스트에 저장된 Member와 조회한 Member가 동일한지 
        tx.commit(); // transaction 종료
    }
}
```
