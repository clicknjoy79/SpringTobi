package springbook.learningtest.jpa;

import javax.persistence.*;

public class MemberDao {
    @PersistenceContext(type= PersistenceContextType.EXTENDED)
    EntityManager em;

    @PersistenceUnit
    EntityManagerFactory emf;

    public void addDuplicatedId() {
        em.persist(new Member(10, "Spring", 7.8));
        em.persist(new Member(10, "Spring", 7.8));
        em.flush();
    }
}
