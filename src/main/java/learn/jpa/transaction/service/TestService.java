package learn.jpa.transaction.service;

import learn.jpa.model.Member;
import learn.jpa.model.User;
import learn.jpa.repository.MemberRepository;
import learn.jpa.repository.UserRepository;
import learn.jpa.transaction.proxy.TransactionActionProxy;
import learn.jpa.type.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TestService {
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final TransactionActionProxy transactionActionProxy;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String testService() throws InterruptedException {
        // 원래 트랜잭션이 영속성 컨텍스트에 등록
        memberRepository.save(Member.createMember("sirooooooo", 12));

        transactionActionProxy.action(() -> { // 새로운 영속성 컨텍스트가 만들어짐
            User user = User.createUser("michel", "password", "test@gmail.com", Gender.MALE);
            userRepository.save(user);
        });

        System.out.println("외부 API 호출 중...");
        Thread.sleep(1_000 * 100);
        return "hi";
    }

}
