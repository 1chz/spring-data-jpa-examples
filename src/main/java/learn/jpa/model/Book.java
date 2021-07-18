package learn.jpa.model;

import learn.jpa.model.value.Publisher;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Book extends BaseEntity {
    @NonNull
    private String title;

    @NonNull
    private String author;

    @NonNull
    private Publisher publisher;

    @Builder
    public static Book of(String title, String author, Publisher publisher) {
        return new Book(title, author, publisher);
    }

    public String getPublisherName() {
        return this.publisher.getName();
    }

    public String getPublisherCountry() {
        return this.publisher.getCountry();
    }
}
