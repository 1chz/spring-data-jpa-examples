package learn.jpa.model;

import lombok.*;

import javax.persistence.Entity;
import java.util.Objects;

@Entity
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BaseEntity {
    @NonNull
    private String name;
}
