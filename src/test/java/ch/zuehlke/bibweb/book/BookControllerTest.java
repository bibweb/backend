package ch.zuehlke.bibweb.book;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import java.util.Optional;


public class BookControllerTest {

    @TestConfiguration
    static class BookControllerTestContextConfiguration {
        @Bean
        public BookController bookController() {
            return new BookController();
        }
    }

    @Autowired
    private BookController bookController;

    @MockBean
    private BookService bookService;

    @Before
    public void setUp() {
        Book book = new Book();
        book.setId(3000L);
        book.setTitle("Buch 1");

        Mockito.when(bookService.getBookById(book.getId())).thenReturn(Optional.of(book));
    }

    /*@Test
    public void whenValidId_thenBookShouldBeFound() {
        Long id = 3000L;
        Optional<Book> found = bookController.getBookById(id);

        Assert.assertThat(found.isPresent());
    }*/
}
