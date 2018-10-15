package ch.zuehlke.bibweb.book;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
public class BookServiceTest {

    @TestConfiguration
    static class BookServiceTestContextConfiguration {
        @Bean
        public BookService bookService() {
            return new BookService();
        }
    }

    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    @Before
    public void setUp() {
        Book book = new Book();
        book.setTitle("Testbook 1");

        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
    }

    @Test
    public void whenValidId_thenBookShouldBeReturned() {
        Long id = 1L;
        Book found = bookService.getBookById(id);

        Assert.assertEquals("Testbook 1", found.getTitle());
    }

    @Test(expected = BookNotFoundExcpetion.class)
    public void whenNonPresentId_thenExceptionShouldBeThrown() {
        Mockito.when(bookRepository.findById(2L)).thenReturn(Optional.empty());
        bookService.getBookById(2L);
    }
}
