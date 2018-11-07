package ch.zuehlke.bibweb.book;

import ch.zuehlke.bibweb.book.exception.BookNotFoundException;
import ch.zuehlke.bibweb.checkout.AvailabilityService;
import ch.zuehlke.bibweb.checkout.CheckoutRepository;
import ch.zuehlke.bibweb.config.MethodSecurityConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@Import(MethodSecurityConfig.class)
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

    @MockBean
    private AvailabilityService availabilityService;

    @Autowired
    private BookService bookService;

    @Before
    public void setUp() {
        Book book = new Book();
        book.setTitle("Testbook 1");

        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Mockito.when(availabilityService.getAvailabilityBasedOnCheckouts(1L)).thenReturn(BookCheckoutState.AVAILABLE);
    }

    @Test
    public void whenGetAllBooks_thenCorrectDTOsShouldBeReturned() {
        Book book1 = BookTestUtil.buildBook(1L, "Title1", "978-3-12-732320-7", 123, 1999, BookType.COMEDY);
        Book book2 = BookTestUtil.buildBook(2L, "Name2", "3-680-08783-7", 321, 2018, BookType.THRILLER);
        Book book3 = BookTestUtil.buildBook(3L, "Buch", "978-3-86680-192-9", 111, 1965, BookType.UNKNOWN);

        Mockito.when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2, book3));

        List<BookDTO> bookList = bookService.getAllBooks();

        Assert.assertEquals(3, bookList.size());
        Assert.assertTrue(BookTestUtil.compareBookWithBookDTO(book1, BookTestUtil.getDTOFromBookEntity(book1)));
        Assert.assertTrue(BookTestUtil.compareBookWithBookDTO(book2, BookTestUtil.getDTOFromBookEntity(book2)));
        Assert.assertTrue(BookTestUtil.compareBookWithBookDTO(book3, BookTestUtil.getDTOFromBookEntity(book3)));
    }

    @Test
    public void whenValidId_thenBookShouldBeReturned() {
        Long id = 1L;
        BookDTO found = bookService.getBookById(id);

        Assert.assertEquals("Testbook 1", found.getTitle());
    }

    @Test(expected = BookNotFoundException.class)
    public void whenNonPresentId_thenExceptionShouldBeThrown() {
        Mockito.when(bookRepository.findById(2L)).thenReturn(Optional.empty());
        bookService.getBookById(2L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void whenUpdatingBook_thenBookShouldBeUpdated() {
        Book oldBook = new Book();
        oldBook.setId(1L);
        oldBook.setTitle("Testtitle");
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(oldBook));
        BookDTO dto = new BookDTO();
        dto.setId(1L);
        dto.setTitle("Updated book");

        bookService.updateBook(1L, dto);

        ArgumentCaptor<Book> capture = ArgumentCaptor.forClass(Book.class);
        Mockito.verify(bookRepository, Mockito.times(1)).save(capture.capture());
        Assert.assertEquals("Updated book", capture.getValue().getTitle());
    }

}
