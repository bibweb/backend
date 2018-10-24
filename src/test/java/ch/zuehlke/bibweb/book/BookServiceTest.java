package ch.zuehlke.bibweb.book;

import ch.zuehlke.bibweb.reservation.Reservation;
import ch.zuehlke.bibweb.reservation.ReservationRepository;
import ch.zuehlke.bibweb.user.User;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.hamcrest.Matchers.anyOf;

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

    @MockBean
    ReservationRepository reservationRepository;

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
        BookDTO found = bookService.getBookById(id);

        Assert.assertEquals("Testbook 1", found.getTitle());
    }

    @Test(expected = BookNotFoundExcpetion.class)
    public void whenNonPresentId_thenExceptionShouldBeThrown() {
        Mockito.when(bookRepository.findById(2L)).thenReturn(Optional.empty());
        bookService.getBookById(2L);
    }

    @Test
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

    @Test
    @WithMockUser(username = "Stefan")
    public void whenRequestingBook_thenReturnUnavailableIfOtherUserHasBookReserved() {
        testAvailabilityWithPresentReservation(BookAvailabilityState.UNAVAILABLE, true);
    }

    @Test
    @WithMockUser(username = "Etienne")
    public void whenRequestingBook_thenReturnReservedByYouIfUserHasBookReserved() {
        testAvailabilityWithPresentReservation(BookAvailabilityState.RESERVED_BY_YOU, true);
    }

    @Test
    @WithMockUser(username = "Etienne")
    public void whenRequestingBook_thenReturnAvailableIfNotLastReservationIsNotActiveSameUser() {
        testAvailabilityWithPresentReservation(BookAvailabilityState.AVAILABLE, false);
    }

    @Test
    @WithMockUser(username = "Stefan")
    public void whenRequestingBook_thenReturnAvailableIfNotLastReservationIsNotActiveDifferentUser() {
        testAvailabilityWithPresentReservation(BookAvailabilityState.AVAILABLE, false);
    }

    @Test
    @WithMockUser(username="Stefan")
    public void whenRequestingBook_thenReturnAvailableIfNoReservationsYet() {
        Book book = new Book();
        book.setId(1L);

        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Assert.assertEquals(BookAvailabilityState.AVAILABLE, bookService.getBookById(1L).getAvailability());
    }

    private void testAvailabilityWithPresentReservation(BookAvailabilityState expected, Boolean active) {
        Book book = new Book();
        book.setId(1L);

        User user = new User();
        user.setUsername("Etienne");

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setActive(active);

        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Mockito.when(reservationRepository.findTop1ByBookIdOrderByReservedAtDesc(1L)).thenReturn(Optional.of(reservation));

        Assert.assertEquals(expected, bookService.getBookById(1L).getAvailability());
    }
}
