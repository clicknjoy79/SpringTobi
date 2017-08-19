package springbook.learningtest.tx;


import springbook.learningtest.hibernate.Member;

import java.util.List;

public interface MemberDaoAspectJ {
    void add(Member m);
    void add(List<Member> members);
    void deleteAll();
    long count();
    void addWithoutTx(List<Member> members);
}
