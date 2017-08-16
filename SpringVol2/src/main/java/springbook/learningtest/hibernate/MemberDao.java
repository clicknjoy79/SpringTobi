package springbook.learningtest.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MemberDao {
    @Autowired
    SessionFactory sessionFactory;

}
