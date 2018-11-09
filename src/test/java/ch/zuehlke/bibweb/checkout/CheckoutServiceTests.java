package ch.zuehlke.bibweb.checkout;

import ch.zuehlke.bibweb.book.Book;
import ch.zuehlke.bibweb.book.BookCheckoutState;
import ch.zuehlke.bibweb.book.BookService;
import ch.zuehlke.bibweb.book.exception.*;
import ch.zuehlke.bibweb.config.MethodSecurityConfig;
import ch.zuehlke.bibweb.config.UserDetailTestService;
import ch.zuehlke.bibweb.config.WebSecurityTestConfig;
import ch.zuehlke.bibweb.reservation.ReservationService;
import ch.zuehlke.bibweb.user.BibwebUser;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@Import(MethodSecurityConfig.class)
public class CheckoutServiceTests {

    @TestConfiguration
    static class BookServiceTestContextConfiguration {
        @Bean
        public CheckoutService checkoutService() {
            return new CheckoutService();
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return new UserDetailTestService();
        }
    }

    @MockBean
    private CheckoutRepository checkoutRepository;

    @MockBean
    private AvailabilityService availabilityService;

    @MockBean
    private BookService bookService;

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private CheckoutService checkoutService;

    private Book book;
    private BibwebUser user;
    private Checkout checkout;

    @Before
    public void setUp() {
        book = new Book();
        book.setId(1L);

        user = new BibwebUser();
        user.setId(1L);
        user.setUsername("Etienne");

        checkout = new Checkout();
        checkout.setUserId(user.getId());
        checkout.setStillOut(false);

        given(reservationService.reservationExistsForUser(1L, 1L)).willReturn(false);
        given(reservationService.reservationExistsForUser(2L, 1L)).willReturn(false);
    }

    @Test
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenCheckingOutBook_thenCreateNewCheckoutIfAvailable() {
        checkout.setStillOut(false);

        Mockito.when(availabilityService.getAvailabilityBasedOnCheckouts(1L)).thenReturn(BookCheckoutState.AVAILABLE);
        Mockito.when(checkoutRepository.findTop1ByBookIdOrderByCheckoutDateDesc(1L)).thenReturn(Optional.of(checkout));
        Mockito.when(checkoutRepository.saveAndFlush(any(Checkout.class))).thenReturn(new Checkout());

        checkoutService.checkoutBookForCurrentUser(book.getId());

        ArgumentCaptor<Checkout> capture = ArgumentCaptor.forClass(Checkout.class);
        Mockito.verify(checkoutRepository, Mockito.times(1)).saveAndFlush(capture.capture());
        Assert.assertEquals((long) book.getId(), (long) capture.getValue().getBookId());
        Assert.assertEquals(2L, (long) capture.getValue().getUserId()); // ID of user Stefan is == 2
    }

    @Test
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenCheckingOutBook_thenRemoveReservationIfExists() {
        checkout.setStillOut(false);

        Mockito.when(availabilityService.getAvailabilityBasedOnCheckouts(1L)).thenReturn(BookCheckoutState.AVAILABLE);
        Mockito.when(checkoutRepository.findTop1ByBookIdOrderByCheckoutDateDesc(1L)).thenReturn(Optional.of(checkout));
        Mockito.when(checkoutRepository.saveAndFlush(any(Checkout.class))).thenReturn(new Checkout());

        given(reservationService.reservationExistsForUser(2L, 1L)).willReturn(true);

        checkoutService.checkoutBookForCurrentUser(book.getId());

        Mockito.verify(reservationService, Mockito.times(1)).removeReservation(2L, 1L);
    }

    @Test(expected = BookCannotBeCheckedOut.class)
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenCheckingOutBook_thenThrowErrorIfBookAlreadyCheckedOutByOtherUser() {
        checkout.setStillOut(true);

        Mockito.when(availabilityService.getAvailabilityBasedOnCheckouts(1L)).thenReturn(BookCheckoutState.UNAVAILABLE);
        Mockito.when(checkoutRepository.findTop1ByBookIdOrderByCheckoutDateDesc(1L)).thenReturn(Optional.of(checkout));

        Mockito.verify(checkoutRepository, Mockito.times(0)).saveAndFlush(any(Checkout.class));
        Mockito.verify(checkoutRepository, Mockito.times(0)).save(any(Checkout.class));

        checkoutService.checkoutBookForCurrentUser(book.getId());
    }

    @Test(expected = CheckoutAlreadyExistsForUserException.class)
    @WithUserDetails(value = "Etienne", userDetailsServiceBeanName = "userDetailsService")
    public void whenCheckingOutBook_thenReturnOldCheckoutIfAlreadyCheckedOutBySameUser() {
        checkout.setStillOut(true);

        Mockito.when(availabilityService.getAvailabilityBasedOnCheckouts(1L)).thenReturn(BookCheckoutState.CHECKEDOUT_BY_YOU);
        Mockito.when(checkoutRepository.findTop1ByBookIdOrderByCheckoutDateDesc(1L)).thenReturn(Optional.of(checkout));

        checkoutService.checkoutBookForCurrentUser(book.getId());
    }

    @Test(expected = BookNotFoundException.class)
    @WithUserDetails(value = "Etienne", userDetailsServiceBeanName = "userDetailsService")
    public void whenCheckingOutBookAndBookDoesNotExists_thenThrowBookNotFoundException() {
        Mockito.when(availabilityService.getAvailabilityBasedOnCheckouts(1L)).thenThrow(BookNotFoundException.class);
        Mockito.when(checkoutRepository.findTop1ByBookIdOrderByCheckoutDateDesc(1L)).thenReturn(Optional.of(checkout));

        checkoutService.checkoutBookForCurrentUser(book.getId());
    }

    @Test(expected = CheckoutDoesNotExistException.class)
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenDeletingNonExistingCheckout_thenThrowCheckoutDoesNotExistException() {
        checkoutService.returnBookForCurrentUser(book.getId());
    }

    @Test(expected = CannotDeleteCheckoutForOtherUserException.class)
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenDeletingExistingCheckoutForOtherUser_thenThrowCannotDeleteCheckoutForOtherUserException() {
        checkout.setStillOut(true);
        Mockito.when(checkoutRepository.findTop1ByBookIdOrderByCheckoutDateDesc(1L)).thenReturn(Optional.of(checkout));

        checkoutService.returnBookForCurrentUser(book.getId());
    }

    @Test(expected = CheckoutDoesNotExistException.class)
    @WithUserDetails(value = "Etienne", userDetailsServiceBeanName = "userDetailsService")
    public void whenDeletingNonActiveCheckout_thenThrowCheckoutDoesNotExistException() {
        Mockito.when(checkoutRepository.findTop1ByBookIdOrderByCheckoutDateDesc(1L)).thenReturn(Optional.of(checkout));
        checkoutService.returnBookForCurrentUser(book.getId());
    }

    @Test
    @WithUserDetails(value = "Etienne", userDetailsServiceBeanName = "userDetailsService")
    public void whenDeletingCheckout_thenSetReturnedToFalse() {
        checkout.setStillOut(true);
        Mockito.when(checkoutRepository.findTop1ByBookIdOrderByCheckoutDateDesc(1L)).thenReturn(Optional.of(checkout));
        checkoutService.returnBookForCurrentUser(book.getId());

        ArgumentCaptor<Checkout> capture = ArgumentCaptor.forClass(Checkout.class);
        Mockito.verify(checkoutRepository, Mockito.times(1)).saveAndFlush(capture.capture());

        Assert.assertEquals(checkout.getId(), capture.getValue().getId());
        Assert.assertFalse(capture.getValue().getStillOut());
    }
}
