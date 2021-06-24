# 📕 ORM(Object Relational Mapping)
작성자: 한창훈

---

`ORM`이란 것은 애플리케이션과 `데이터베이스` 사이에 가상의 데이터베이스(`JPA`에서 `영속성 컨텍스트`라고 불리는)라는 `인디렉션`을 만드는 것이다.

즉, `OOP(Object-Oriented Programming)`를 하기위한 프로그래밍 기법이다.

원래는 `데이터베이스`에 종속적이었던 코드를 역으로 `데이터베이스`를 추상화하여 코드레벨에 맞춰주며

그렇게 함으로써 데이터에 종속적인 프로그래밍이 아닌 `OOP`를 할 수 있게 된다.

내가 실무를 보며 느끼기에 `ORM`이란 것은 결국 사전적인 정의대로 `OOP`를 위한 기술이다.

`SQL`에 종속적이다, 똑같은 코드가 반복된다 등의 잡다한 이유가 있지만

이러한 문제들의 본질은 `OOP`를 하지 못하기 때문에 발생하는 것이다.

애플리케이션이 `데이터베이스`와 연동되면 모든 코드가 자연스럽게 데이터 기반으로 돌아가기 때문에 

자연스럽게 `OOP` 5대 원칙, `SOLID`도 대부분 위반하게 된다.

| 약어 | 개념 |
|---|---|
| SRP | 단일 책임 원칙 (Single responsibility principle) <br />한 클래스는 하나의 책임만 가져야 한다. |
| OCP | 개방-폐쇄 원칙 (Open/closed principle) <br />“소프트웨어 요소는 확장에는 열려 있으나 변경에는 닫혀 있어야 한다.” |
| LSP | 리스코프 치환 원칙 (Liskov substitution principle) <br /> “프로그램의 객체는 프로그램의 정확성을 깨뜨리지 않으면서 하위 타입의 인스턴스로 바꿀 수 있어야 한다.” 계약에 의한 설계를 참고하라. |
| ISP | 인터페이스 분리 원칙 (Interface segregation principle) <br /> “특정 클라이언트를 위한 인터페이스 여러 개가 범용 인터페이스 하나보다 낫다.” |
| DIP | 의존관계 역전 원칙 (Dependency inversion principle) <br /> 프로그래머는 “추상화에 의존해야지, 구체화에 의존하면 안된다.” 의존성 주입은 이 원칙을 따르는 방법 중 하나다. |

---

간단한 예제를 보자면

`Member` 객체를 `DB`에 저장할것이라고 했을 때,

`Member` 클래스를 `javabeans` 규약에 맞추어 작성하고, `SQL`을 작성한다.

```java
class Member{
    private String name;
    private int age;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }  
}
```

```mysql
INSERT INTO MEMBER
    (NAME, AGE)
VALUES
    (#name, #age);
```

이제 접근자를 이용해 `name`과 `age`를 추출하여 `SQL`에 넣어줘야한다. 

```java
class InsertService {
    public void insert(String name, int age);
}

...
service.insert(member.getName, member.getAge);
...
```

이 데이터를 뽑아내고 입력하는 과정 자체가 데이터에 종속적인 과정이며, 결국 이 과정으로 인해 모든 문제가 발생한다.

만약 이 상황에서 `address`라는 필드가 하나 더 필요해진다면?

`Member`에 `address` 필드를 추가하고, `SQL`과 `Service` 클래스도 변경되어야만 한다.

실무에서는 절대 써먹을수조차 없는 매우 간단한 예제임에도 불구하고 즉시 `OCP`가 위배된다.

이처럼 코드와 `SQL` 사이에 매우 강한 결합도(=`내용 결합도`)가 생기기 때문에 `SOLID`를 지키기가 정말 어려워진다.

나는 개인적으로 불가능하다고 생각하기도 한다.

그렇다면 `ORM`을 도입하면 어떻게 변할까?

```java

@Entity
class Member{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int age;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }  
}
```

SQL은 따로 작성하지 않는다.

추상화된 가상의 데이터베이스가 알아서 처리해주기 때문이다.

이 상태에서 `address` 필드를 추가해야 한다면 그냥 추가하면 끝이다. 더 손댈 게 없다. 즉, `OCP`를 위반하지 않게된다.

이렇게 간단한 예제만으로도 `SOLID`를 지키기가 압도적으로 수월해지며, 결과적으로 `OOP`의 장점을 모두 누릴 수 있게된다.

SQL에 종속적인 문제, 재사용성 증가, 유지보수성 증가, 리팩토링 용이성 등등. 결국 모두 `OOP`를 하게 됨으로써 얻는 이득이다.






