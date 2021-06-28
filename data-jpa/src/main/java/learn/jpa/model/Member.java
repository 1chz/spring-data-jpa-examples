package learn.jpa.model;

import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;

@Entity
@Getter
@ToString(callSuper = true)
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @NonNull
    private String name;

    @NonNull
    private int age;

    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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
    }
}
