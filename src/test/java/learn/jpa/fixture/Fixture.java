package learn.jpa.fixture;

import learn.jpa.model.Member;
import learn.jpa.model.Team;

import java.util.List;

public class Fixture {
    private static final Fixture INSTANCE = new Fixture();

    private Fixture() {
    }

    public static Fixture getInstance() {
        return INSTANCE;
    }

    public List<Member> createMembers() {
        return List.of(
                Member.createMember("siro", 29),
                Member.createMember("sophia", 32),
                Member.createMember("dennis", 25),
                Member.createMember("james", 41),
                Member.createMember("michael", 33)
                      );
    }

    public Team createTeam() {
        return new Team("querydsl");
    }
}
