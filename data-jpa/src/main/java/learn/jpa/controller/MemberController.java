package learn.jpa.controller;

import learn.jpa.model.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    @GetMapping("members/{id}")
    public Member getMember(@PathVariable("id") Member member) {
        return member;
    }
}
