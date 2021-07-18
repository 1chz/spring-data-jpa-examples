package learn.jpa.model.value;

import lombok.*;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;

@Getter
@ToString
@Embeddable
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Name {
    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

    @Builder
    public static Name of(@NonNull String firstName, @NonNull String lastName) {
        return new Name(firstName, lastName);
    }

    public String getName() {
        return firstName + " " + lastName;
    }
}
