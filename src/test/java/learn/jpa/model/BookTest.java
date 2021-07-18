package learn.jpa.model;

import learn.jpa.model.Book;
import learn.jpa.model.value.Publisher;
import learn.jpa.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookTest {
    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("객체_필드_테스트")
    void objectField() {
        // given
        String title = "jpa-in-action";
        String author = "siro";
        String publisherName = "publishing-company";
        String publisherCountry = "korea";
        Publisher publisher = Publisher.of(publisherName, publisherCountry);
        Book book = Book.of(title, author, publisher);

        // when
        repository.saveAndFlush(book);

        // then
        assertThat(book.getPublisherName()).isEqualTo(publisherName);
        assertThat(book.getPublisherCountry()).isEqualTo(publisherCountry);
        assertThat(book).extracting("title", "author", "publisher")
                        .containsExactly(title, author, publisher);
    }
}
