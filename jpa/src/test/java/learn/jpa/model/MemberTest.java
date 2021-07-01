package learn.jpa.model;

import learn.jpa.factories.EntityManagerFactoryProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MemberTest {
    Logger log = LoggerFactory.getLogger(MemberTest.class);

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
            member.changeTeam(team);

            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            Member findMember = entityManager.find(Member.class, member.getId());

            assertThat(findMember).extracting("name", "age")
                                  .containsExactly(member.getName(), member.getAge());

            transaction.commit();
        }
        catch(Exception e) {
            log.warn("transaction rollback. {}!", e.getMessage());
            transaction.rollback();
        }
        finally {
            entityManager.close();
        }
    }
}
