package springbook.learningtest.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate4.HibernateTemplate;


public class MemberTemplateDao  {
    public HibernateTemplate hibernateTemplate;

    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }
}
