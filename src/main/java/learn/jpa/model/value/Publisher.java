package learn.jpa.model.value;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@ToString
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Publisher {
    @NonNull
    @Column(name = "publisher_name")
    private String name;

    @NonNull
    @Column(name = "publisher_country")
    private String country;

    @Builder
    public static Publisher of(@NonNull String name, @NonNull String country) {
        return new Publisher(name, country);
    }
}
