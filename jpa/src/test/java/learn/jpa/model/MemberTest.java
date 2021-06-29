package learn.jpa.model;

import learn.jpa.factories.EntityManagerFactoryProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

@SpringBootTest
class MemberTest {
    EntityManager entityManager;
    EntityTransaction transaction;

    @BeforeEach
    void setUp() {
        entityManager = EntityManagerFactoryProvider.getEntityManager();
        transaction = entityManager.getTransaction();
    }

    @Test
    void jpaTest() {
        try {
            transaction.begin();
            Team team = new Team("hibernate");

            Member member = Member.createMember("siro", 11);
            member.toJoinTeam(team);

            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            Member findMember = entityManager.find(Member.class, 1L);
            System.out.println("findMember = " + findMember);

            transaction.commit();
        }
        catch(Exception e) {
            System.out.println(e.getMessage() + "! transaction rollback.");
            transaction.rollback();
        }
        finally {
            entityManager.close();
        }
    }
}
