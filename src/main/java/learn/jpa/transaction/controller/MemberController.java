package learn.jpa.transaction.controller;

import learn.jpa.model.Member;
import learn.jpa.transaction.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final TestService testService;

    @GetMapping("/members/{id}")
    public Member getMember(@PathVariable("id") Optional<Member> member) {
        return member.orElseGet(null);
    }

    @GetMapping("/test")
    public String test() throws InterruptedException {
        return testService.testService();
    }
}
