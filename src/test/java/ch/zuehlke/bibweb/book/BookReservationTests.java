package ch.zuehlke.bibweb.book;

import ch.zuehlke.bibweb.book.exception.*;
import ch.zuehlke.bibweb.config.UserDetailTestService;
import ch.zuehlke.bibweb.reservation.Reservation;
import ch.zuehlke.bibweb.reservation.ReservationRepository;
import ch.zuehlke.bibweb.user.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
public class BookReservationTests {

    @TestConfiguration
    static class BookServiceTestContextConfiguration {
        @Bean
        public BookService bookService() {
            return new BookService();
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return new UserDetailTestService();
        }
    }

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    ReservationRepository reservationRepository;

    @Autowired
    private BookService bookService;

    private Book book;
    private User user;
    private Reservation reservation;

    @Before
    public void setUp() {
        book = new Book();
        book.setId(1L);

        user = new User();
        user.setId(1L);
        user.setUsername("Etienne");

        reservation = new Reservation();
        reservation.setUser(user);
        reservation.setActive(false);
    }

    @Test
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenReservingBook_thenCreateNewReservationIfAvailable() {
        reservation.setActive(false);

        Mockito.when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        Mockito.when(reservationRepository.findTop1ByBookIdOrderByReservedAtDesc(1L)).thenReturn(Optional.of(reservation));

        bookService.reserveBook(book.getId());

        ArgumentCaptor<Reservation> capture = ArgumentCaptor.forClass(Reservation.class);
        Mockito.verify(reservationRepository, Mockito.times(1)).saveAndFlush(capture.capture());
        Assert.assertEquals((long) book.getId(), (long) capture.getValue().getBookId());
        Assert.assertEquals(2L, (long) capture.getValue().getUser().getId()); // ID of user Stefan is == 2
        Assert.assertEquals("Stefan", capture.getValue().getUser().getUsername());
    }

    @Test(expected = BookCannotBeReservedException.class)
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenReservingBook_thenThrowErrorIfBookAlreadyReservedByOtherUser() {
        reservation.setActive(true);

        Mockito.when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        Mockito.when(reservationRepository.findTop1ByBookIdOrderByReservedAtDesc(1L)).thenReturn(Optional.of(reservation));

        Mockito.verify(reservationRepository, Mockito.times(0)).saveAndFlush(any(Reservation.class));
        Mockito.verify(reservationRepository, Mockito.times(0)).save(any(Reservation.class));

        bookService.reserveBook(book.getId());
    }

    @Test(expected = ReservationAlreadyExistsForUserException.class)
    @WithUserDetails(value = "Etienne", userDetailsServiceBeanName = "userDetailsService")
    public void whenReservingBook_thenReturnOldReservationIfAlreadyReservedBySameUser() {
        reservation.setActive(true);

        Mockito.when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        Mockito.when(reservationRepository.findTop1ByBookIdOrderByReservedAtDesc(1L)).thenReturn(Optional.of(reservation));

        bookService.reserveBook(book.getId());
    }

    @Test(expected = BookNotFoundException.class)
    @WithUserDetails(value = "Etienne", userDetailsServiceBeanName = "userDetailsService")
    public void whenReservingBookAndBookDoesNotExists_thenThrowBookNotFoundException() {
        Mockito.when(bookRepository.findById(book.getId())).thenThrow(BookNotFoundException.class);
        Mockito.when(reservationRepository.findTop1ByBookIdOrderByReservedAtDesc(1L)).thenReturn(Optional.of(reservation));

        bookService.reserveBook(book.getId());
    }

    @Test(expected = ReservationDoesNotExistException.class)
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenDeletingNonExistingReservation_thenThrowReservationDoesNotExistException() {
        bookService.deleteActiveReservationForBook(book.getId());
    }

    @Test(expected = CannotDeleteReservationForOtherUserException.class)
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenDeletingExistingReservationForOtherUser_thenThrowCannotDeleteReservationForOtherUserException() {
        reservation.setActive(true);
        Mockito.when(reservationRepository.findTop1ByBookIdOrderByReservedAtDesc(1L)).thenReturn(Optional.of(reservation));

        bookService.deleteActiveReservationForBook(book.getId());
    }

    @Test(expected = ReservationDoesNotExistException.class)
    @WithUserDetails(value = "Etienne", userDetailsServiceBeanName = "userDetailsService")
    public void whenDeletingNonActiveReservation_thenThrowReservationDoesNotExistException() {
        Mockito.when(reservationRepository.findTop1ByBookIdOrderByReservedAtDesc(1L)).thenReturn(Optional.of(reservation));
        bookService.deleteActiveReservationForBook(book.getId());
    }

    @Test
    @WithUserDetails(value = "Etienne", userDetailsServiceBeanName = "userDetailsService")
    public void whenDeletingReservation_thenSetActiveToFalse() {
        reservation.setActive(true);
        Mockito.when(reservationRepository.findTop1ByBookIdOrderByReservedAtDesc(1L)).thenReturn(Optional.of(reservation));
        bookService.deleteActiveReservationForBook(book.getId());

        ArgumentCaptor<Reservation> capture = ArgumentCaptor.forClass(Reservation.class);
        Mockito.verify(reservationRepository, Mockito.times(1)).saveAndFlush(capture.capture());

        Assert.assertEquals(reservation.getId(), capture.getValue().getId());
        Assert.assertFalse(capture.getValue().getActive());
    }
}
