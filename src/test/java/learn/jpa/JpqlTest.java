package learn.jpa;

import learn.jpa.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.EntityManager;

@DataJpaTest
public class JpqlTest {
    private final TestEntityManager testEntityManager;

    public JpqlTest(TestEntityManager testEntityManager) {
        this.testEntityManager = testEntityManager;
    }

    private EntityManager em;

    @BeforeEach
    void setUp() {
        em = testEntityManager.getEntityManager();
    }

    @Test
    @DisplayName("테스트를 실행하여 JPQL 쿼리를 확인한다")
    void showJpqlQueryV1() {
        em.createQuery("select m from Member m where m.name = 'siro'", Member.class)
          .getResultList();
    }
}
