package ch.zuehlke.bibweb.book;

import ch.zuehlke.bibweb.book.exception.*;
import ch.zuehlke.bibweb.config.UserDetailTestService;
import ch.zuehlke.bibweb.checkout.Checkout;
import ch.zuehlke.bibweb.checkout.CheckoutRepository;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
public class BookCheckoutTests {

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
    CheckoutRepository checkoutRepository;

    @Autowired
    private BookService bookService;

    private Book book;
    private User user;
    private Checkout checkout;

    @Before
    public void setUp() {
        book = new Book();
        book.setId(1L);

        user = new User();
        user.setId(1L);
        user.setUsername("Etienne");

        checkout = new Checkout();
        checkout.setUserId(user.getId());
        checkout.setStillOut(false);
    }

    @Test
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenCheckingOutBook_thenCreateNewCheckoutIfAvailable() {
        checkout.setStillOut(false);

        Mockito.when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        Mockito.when(checkoutRepository.findTop1ByBookIdOrderByCheckoutDateAtDesc(1L)).thenReturn(Optional.of(checkout));

        bookService.checkoutBook(book.getId());

        ArgumentCaptor<Checkout> capture = ArgumentCaptor.forClass(Checkout.class);
        Mockito.verify(checkoutRepository, Mockito.times(1)).saveAndFlush(capture.capture());
        Assert.assertEquals((long) book.getId(), (long) capture.getValue().getBookId());
        Assert.assertEquals(2L, (long) capture.getValue().getUserId()); // ID of user Stefan is == 2
    }

    @Test(expected = BookCannotBeCheckedOut.class)
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenCheckingOutBook_thenThrowErrorIfBookAlreadyCheckedOutByOtherUser() {
        checkout.setStillOut(true);

        Mockito.when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        Mockito.when(checkoutRepository.findTop1ByBookIdOrderByCheckoutDateAtDesc(1L)).thenReturn(Optional.of(checkout));

        Mockito.verify(checkoutRepository, Mockito.times(0)).saveAndFlush(any(Checkout.class));
        Mockito.verify(checkoutRepository, Mockito.times(0)).save(any(Checkout.class));

        bookService.checkoutBook(book.getId());
    }

    @Test(expected = CheckoutAlreadyExistsForUserException.class)
    @WithUserDetails(value = "Etienne", userDetailsServiceBeanName = "userDetailsService")
    public void whenCheckingOutBook_thenReturnOldCheckoutIfAlreadyCheckedOutBySameUser() {
        checkout.setStillOut(true);

        Mockito.when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        Mockito.when(checkoutRepository.findTop1ByBookIdOrderByCheckoutDateAtDesc(1L)).thenReturn(Optional.of(checkout));

        bookService.checkoutBook(book.getId());
    }

    @Test(expected = BookNotFoundException.class)
    @WithUserDetails(value = "Etienne", userDetailsServiceBeanName = "userDetailsService")
    public void whenCheckingOutBookAndBookDoesNotExists_thenThrowBookNotFoundException() {
        Mockito.when(bookRepository.findById(book.getId())).thenThrow(BookNotFoundException.class);
        Mockito.when(checkoutRepository.findTop1ByBookIdOrderByCheckoutDateAtDesc(1L)).thenReturn(Optional.of(checkout));

        bookService.checkoutBook(book.getId());
    }

    @Test(expected = CheckoutDoesNotExistException.class)
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenDeletingNonExistingCheckout_thenThrowCheckoutDoesNotExistException() {
        bookService.returnBook(book.getId());
    }

    @Test(expected = CannotDeleteCheckoutForOtherUserException.class)
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenDeletingExistingCheckoutForOtherUser_thenThrowCannotDeleteCheckoutForOtherUserException() {
        checkout.setStillOut(true);
        Mockito.when(checkoutRepository.findTop1ByBookIdOrderByCheckoutDateAtDesc(1L)).thenReturn(Optional.of(checkout));

        bookService.returnBook(book.getId());
    }

    @Test(expected = CheckoutDoesNotExistException.class)
    @WithUserDetails(value = "Etienne", userDetailsServiceBeanName = "userDetailsService")
    public void whenDeletingNonActiveCheckout_thenThrowCheckoutDoesNotExistException() {
        Mockito.when(checkoutRepository.findTop1ByBookIdOrderByCheckoutDateAtDesc(1L)).thenReturn(Optional.of(checkout));
        bookService.returnBook(book.getId());
    }

    @Test
    @WithUserDetails(value = "Etienne", userDetailsServiceBeanName = "userDetailsService")
    public void whenDeletingCheckout_thenSetReturnedToFalse() {
        checkout.setStillOut(true);
        Mockito.when(checkoutRepository.findTop1ByBookIdOrderByCheckoutDateAtDesc(1L)).thenReturn(Optional.of(checkout));
        bookService.returnBook(book.getId());

        ArgumentCaptor<Checkout> capture = ArgumentCaptor.forClass(Checkout.class);
        Mockito.verify(checkoutRepository, Mockito.times(1)).saveAndFlush(capture.capture());

        Assert.assertEquals(checkout.getId(), capture.getValue().getId());
        Assert.assertFalse(capture.getValue().getStillOut());
    }
}
