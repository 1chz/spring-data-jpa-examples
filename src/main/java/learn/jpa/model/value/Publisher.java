package learn.jpa.model.value;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Publisher {
    @NotNull
    @Column(name = "publisher_name")
    private String name;

    @NotNull
    @Column(name = "publisher_country")
    private String country;

    private Publisher(String name, String country) {
        this.name = name;
        this.country = country;
    }

    @Builder
    public static Publisher of(@NotNull String name, @NotNull String country) {
        return new Publisher(name, country);
    }
}
