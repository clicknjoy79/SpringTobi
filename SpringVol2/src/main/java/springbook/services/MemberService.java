package springbook.services;

import org.springframework.stereotype.Component;

@Component
public class MemberService {
    public String helloService() {
        return "Hello Service!!!! 적용되었습니다....";
    }
}
