package learn.jpa.factories;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerStaticFactory {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("pure-jpa");

    private EntityManagerStaticFactory() {
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
