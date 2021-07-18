package learn.jpa.model;

import learn.jpa.model.Member;
import learn.jpa.model.Team;
import learn.jpa.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberTest {
    @Autowired
    MemberRepository memberRepository;

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
        return Stream.of(
                Arguments.of("hibernate", "siro", 29),
                Arguments.of("spring", "siro", 30),
                Arguments.of("JPA", "siro", 31)
                        );
    }
}
