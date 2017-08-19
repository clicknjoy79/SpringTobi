package springbook.learningtest.jpa;

import org.springframework.stereotype.Repository;
import springbook.learningtest.hibernate.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepositoryDao {
    @PersistenceContext
    EntityManager em;

    public void addDuplicatedId() {
        em.persist(new Member(10, "Spring", 7.8));
        em.persist(new Member(10, "Spring", 7.8));
        em.flush();
    }

}
