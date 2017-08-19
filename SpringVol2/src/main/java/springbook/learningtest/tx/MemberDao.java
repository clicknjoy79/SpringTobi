package springbook.learningtest.tx;


import springbook.learningtest.hibernate.Member;

import java.util.List;

public interface MemberDao {
    void add(Member m);
    void add(List<Member> members);
    void deleteAll();
    long count();
}
