package learn.jpa.model;

import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Simple extends BaseEntity {
    @NotNull
    private String name;

    @NotNull
    private int age;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Team team;

    private Simple(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Builder(toBuilder = true)
    public static Simple createSimple(String name, int age) {
        return new Simple(name, age);
    }

    public void changeTeam(Team team) {
        if(team == null) {
            throw new IllegalArgumentException("Team is null!");
        }
        this.team = team;
    }
}
