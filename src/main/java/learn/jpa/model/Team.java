package learn.jpa.model;

import learn.jpa.model.BaseEntity;
import lombok.*;

import javax.persistence.Entity;

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
