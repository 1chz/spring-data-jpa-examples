package learn.jpa.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@ToString(exclude = "team")
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private int age;

    @OneToOne
    private Cabinet cabinet;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @Builder(toBuilder = true)
    public static Member createMember(String name, int age) {
        return new Member(name, age);
    }

    public void toJoinTeam(Team team) {
        if(team == null) {
            throw new IllegalArgumentException("Team is null!");
        }
        this.team = team;
        this.team.getMembers().add(this);
    }
}
