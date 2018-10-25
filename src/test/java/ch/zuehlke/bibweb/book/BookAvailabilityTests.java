package ch.zuehlke.bibweb.book;

import ch.zuehlke.bibweb.config.UserDetailTestService;
import ch.zuehlke.bibweb.reservation.Reservation;
import ch.zuehlke.bibweb.reservation.ReservationRepository;
import ch.zuehlke.bibweb.user.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
public class BookAvailabilityTests {

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

    @Test
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenRequestingBook_thenReturnUnavailableIfOtherUserHasBookReserved() {
        testAvailabilityWithPresentReservation(BookAvailabilityState.UNAVAILABLE, true);
    }

    @Test
    @WithUserDetails(value = "Etienne", userDetailsServiceBeanName = "userDetailsService")
    public void whenRequestingBook_thenReturnReservedByYouIfUserHasBookReserved() {
        testAvailabilityWithPresentReservation(BookAvailabilityState.RESERVED_BY_YOU, true);
    }

    @Test
    @WithUserDetails(value = "Etienne", userDetailsServiceBeanName = "userDetailsService")
    public void whenRequestingBook_thenReturnAvailableIfNotLastReservationIsNotActiveSameUser() {
        testAvailabilityWithPresentReservation(BookAvailabilityState.AVAILABLE, false);
    }

    @Test
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenRequestingBook_thenReturnAvailableIfNotLastReservationIsNotActiveDifferentUser() {
        testAvailabilityWithPresentReservation(BookAvailabilityState.AVAILABLE, false);
    }

    @Test
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
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
        user.setId(1L);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setActive(active);

        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Mockito.when(reservationRepository.findTop1ByBookIdOrderByReservedAtDesc(1L)).thenReturn(Optional.of(reservation));

        Assert.assertEquals(expected, bookService.getBookById(1L).getAvailability());
    }
}
