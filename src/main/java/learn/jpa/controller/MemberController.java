package learn.jpa.controller;

import learn.jpa.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberController {
    @GetMapping("members/{id}")
    public Member getMember(@PathVariable("id") Optional<Member> member) {
        return member.orElseGet(null);
    }
}
