package learn.jpa.model;

import learn.jpa.model.value.Publisher;
import lombok.*;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends BaseEntity {
    @NotNull
    private String title;

    @NotNull
    private String author;

    @NotNull
    private Publisher publisher;

    private Book(String title, String author, Publisher publisher) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
    }

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
