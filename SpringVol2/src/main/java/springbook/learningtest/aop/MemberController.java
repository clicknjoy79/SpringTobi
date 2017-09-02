package springbook.learningtest.aop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import springbook.domains.Member;

@Controller
public class MemberController {
    @RequestMapping("/member")
    public String member(Model model) {
        model.addAttribute("member", new Member());
        return "loadTimeWeavingTest";
    }
}
