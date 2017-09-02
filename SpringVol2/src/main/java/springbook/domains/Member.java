package springbook.domains;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import springbook.services.MemberService;

@Configurable
public class Member {
    @Autowired
    MemberService service;

    public MemberService getService() {
        return service;
    }

    public void setService(MemberService service) {
        this.service = service;
    }
}
