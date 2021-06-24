package learn.jpa.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Publisher {
    @NonNull
    @Column(name = "publisher_name")
    String name;

    @NonNull
    @Column(name = "publisher_country")
    String country;

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        Publisher publisher = (Publisher) o;
        return name.equals(publisher.name) && country.equals(publisher.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, country);
    }
}
