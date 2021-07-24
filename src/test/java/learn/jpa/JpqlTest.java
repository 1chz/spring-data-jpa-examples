package learn.jpa;

import com.querydsl.jpa.impl.JPAQueryFactory;
import learn.jpa.config.JpaConfig;
import learn.jpa.dto.MemberDto;
import learn.jpa.dto.QMemberDto;
import learn.jpa.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

import static learn.jpa.model.QMember.member;

@DataJpaTest
@Import(JpaConfig.class)
public class JpqlTest {
    private final TestEntityManager testEntityManager;
    private final JPAQueryFactory queryFactory;

    public JpqlTest(TestEntityManager testEntityManager, JPAQueryFactory queryFactory) {
        this.testEntityManager = testEntityManager;
        this.queryFactory = queryFactory;
    }

    private EntityManager em;

    @BeforeEach
    void setUp() {
        em = testEntityManager.getEntityManager();
    }

    @Test
    @DisplayName("테스트를 실행하여 SELECT 쿼리를 확인한다")
    void select() throws Exception {
        em.createQuery("select m from Member m where m.name = 'siro'", Member.class)
          .getResultList();

        em.createQuery("select m from Member m where m.name = :name")
          .setParameter("name", "siro")
          .getResultList();
    }

    @Test
    @DisplayName("테스트를 실행하여 UPDATE 쿼리를 확인한다")
    void update() throws Exception {
        em.createQuery("update Member m set m.name = 'siro' where m.id = 1")
          .executeUpdate();

        em.createQuery("update Member m set m.name = :name where m.id = :id")
          .setParameter("name", "siro")
          .setParameter("id", 1L)
          .executeUpdate();
    }

    @Test
    @DisplayName("테스트를 실행하여 DELETE 쿼리를 확인한다")
    void delete() throws Exception {
        em.createQuery("delete from Member m where m.id = 1")
          .executeUpdate();

        em.createQuery("delete from Member m where m.id = :id")
          .setParameter("id", 1L)
          .executeUpdate();
    }

    @Test
    @DisplayName("TypedQuery, Query의 차이를 확인한다")
    void typeTest() throws Exception {
        // TypedQuery: 반환 타입이 아주 명확한 경우
        TypedQuery<Member> typedQuery = em.createQuery("select m from Member m", Member.class);

        // 명확한 타입의 객체가 반환된다.
        List<Member> typedResultList = typedQuery.getResultList();

        for (Member member : typedResultList) {
            // Member 타입
        }

        // Query: 반환 타입이 명확하지 않은 경우
        Query query = em.createQuery("select m.id, m.name from Member m");

        // Object 타입의 객체가 반환된다. (타입 캐스팅을 한번 더 해야하므로 약간 더 불편하다)
        List<Object[]> queryResultList = query.getResultList();

        for (Object[] o : queryResultList) {
            System.out.println("id = " + (Long) o[0]); // 식별자
            System.out.println("name = " + (String) o[1]); // 이름
        }
    }

    @Test
    @DisplayName("프로젝션")
    void projection() throws Exception {
        List<Object[]> queryResultList = em.createQuery("select m.id, m.name from Member m")
                                           .getResultList();

        List<MemberDto> memberDtos = new ArrayList<>();
        for (Object[] o : queryResultList) {
            Long id = (Long) o[0];
            String name = (String) o[1];

            MemberDto memberDto = new MemberDto(id, name);

            memberDtos.add(memberDto);
        }
    }

    @Test
    @DisplayName("생성자 프로젝션")
    void constructorProjection() throws Exception {
        em.createQuery("select new learn.jpa.dto.MemberDto(m.id, m.name) from Member m", MemberDto.class)
          .getResultList();
    }

    @Test
    @DisplayName("Querydsl 생성자 프로젝션")
    void querydslConstructorProjection() throws Exception {
        queryFactory.select(new QMemberDto(member.id, member.name))
                    .from(member)
                    .fetch();
    }

    @Test
    @DisplayName("페이징")
    void paging() throws Exception {
        em.createQuery("select m from Member m order by m.name desc", Member.class)
          .setFirstResult(10)
          .setMaxResults(20)
          .getResultList();
    }

    @Test
    @DisplayName("집계함수")
    void aggregate() throws Exception {
        em.createQuery("select count(m) from Member m").getSingleResult();
        em.createQuery("select max(m.id) from Member m").getSingleResult();
        em.createQuery("select min(m.id) from Member m").getSingleResult();
        em.createQuery("select avg(m.age) from Member m").getSingleResult();
        em.createQuery("select sum(m.age) from Member m").getSingleResult();

        em.createQuery("select m.name as aa from Member m group by m.team having m.age > 20").getResultList();
    }

    @Test
    @DisplayName("정렬")
    void sort() throws Exception {
        em.createQuery("select m from Member m order by m.age desc").getResultList();
    }

    @Test
    @DisplayName("내부조인")
    void innerJoin() throws Exception {
        em.createQuery("select m from Member m join m.team t").getResultList();
    }

    @Test
    @DisplayName("외부조인")
    void leftOuterJoin() throws Exception {
        em.createQuery("select m from Member m left join m.team t").getResultList();
    }

    @Test
    @DisplayName("세타조인")
    void thetaJoin() throws Exception {
        em.createQuery("select m, t from Member m, Team t where m.name = t.name").getResultList();
    }

    @Test
    @DisplayName("페치조인")
    void fetchJoin() throws Exception {
        em.createQuery("select m from Member m join fetch m.team").getResultList();
    }

    @Test
    @DisplayName("경로표현식")
    void pathExpression() throws Exception {
        em.createQuery("select m.team from Member m").getResultList();
    }
}
