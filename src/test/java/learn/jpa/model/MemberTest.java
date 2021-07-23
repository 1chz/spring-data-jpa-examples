package learn.jpa.model;

import com.querydsl.jpa.impl.JPAQueryFactory;
import learn.jpa.config.JpaConfig;
import learn.jpa.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
class MemberTest {
    private final JPAQueryFactory queryFactory;
    private final MemberRepository memberRepository;

    public MemberTest(JPAQueryFactory queryFactory, MemberRepository memberRepository) {
        this.queryFactory = queryFactory;
        this.memberRepository = memberRepository;
    }

    @ParameterizedTest
    @MethodSource("provideArgs")
    @DisplayName("연관관계_매핑_테스트")
    void relationshipMapping(String teamName, String memberName, int memberAge) {
        // given
        Team team = new Team(teamName);
        Member member = Member.createMember(memberName, memberAge);
        member.changeTeam(team);

        // when
        Member saveMember = memberRepository.save(member);

        // then
        assertThat(saveMember).extracting("name", "age", "team")
                              .containsExactly(memberName, memberAge, team);
    }

    /**
     * Test Case
     */
    private static Stream<Arguments> provideArgs() {
        return Stream.of(Arguments.of("hibernate", "siro", 29),
                         Arguments.of("spring", "siro", 30),
                         Arguments.of("JPA", "siro", 31)
                        );
    }

    @Test
    void querydsl() {
        // given
        String teamName = "hibernate";
        String memberName = "siro";
        int memberAge = 29;
        Team team = new Team(teamName);
        Member member = Member.createMember(memberName, memberAge);
        member.changeTeam(team);
        memberRepository.save(member);

        // when
        List<Member> members = queryFactory.selectFrom(QMember.member).fetch();

        // then
        System.out.println("members = " + members);
        //        assertThat(saveMember).extracting("name", "age", "team")
        //                              .containsExactly(memberName, memberAge, team);
    }
}
