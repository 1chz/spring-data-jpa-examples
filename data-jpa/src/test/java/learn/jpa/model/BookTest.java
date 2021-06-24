package learn.jpa.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookTest {
    static final String title = "jpa-in-action";
    static final String author = "siro";
    static final String publisherName = "publishing-company";
    static final String publisherCountry = "korea";

    @Test
    @DisplayName("객체_필드_테스트")
    void objectField() {
        // given
        Publisher publisher = new Publisher(publisherName, publisherCountry);

        // when
        Book book = Book.createBook(title, author, publisher);

        // then
        assertThat(book.getPublisherName()).isEqualTo(publisherName);
        assertThat(book.getPublisherCountry()).isEqualTo(publisherCountry);
        assertThat(book).extracting("title", "author", "publisher")
                        .containsExactly(title, author, publisher);
    }
}
