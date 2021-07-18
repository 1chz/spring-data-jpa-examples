package learn.jpa.model.value;

import lombok.*;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Getter
@ToString
@Embeddable
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Name {
    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private Name(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Builder
    public static Name of(@NotNull String firstName, @NotNull String lastName) {
        return new Name(firstName, lastName);
    }

    public String getName() {
        return firstName + " " + lastName;
    }
}
