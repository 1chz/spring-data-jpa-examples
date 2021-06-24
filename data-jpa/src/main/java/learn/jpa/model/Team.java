package learn.jpa.model;

import lombok.*;

import javax.persistence.Entity;
import java.util.Objects;

@Entity
@ToString @Getter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BaseEntity {
    @NonNull
    private String name;

    @Override public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        Team team = (Team) o;
        return getName().equals(team.getName());
    }

    @Override public int hashCode() {
        return Objects.hash(getName());
    }
}
