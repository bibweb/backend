package ch.zuehlke.bibweb.book;

import ch.zuehlke.bibweb.checkout.AvailabilityService;
import ch.zuehlke.bibweb.config.UserDetailTestService;
import ch.zuehlke.bibweb.checkout.Checkout;
import ch.zuehlke.bibweb.checkout.CheckoutRepository;
import ch.zuehlke.bibweb.user.BibwebUser;
import ch.zuehlke.bibweb.user.BibwebUserDTO;
import ch.zuehlke.bibweb.user.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockJspWriter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

public class BookAvailabilityTests {

    private BookRepository bookRepository;
    private CheckoutRepository checkoutRepository;
    private UserService userService;
    private AvailabilityService availabilityService;

    private BibwebUserDTO etienne;
    private BibwebUserDTO stefan;

    @Before
    public void setUp() {
        etienne = new BibwebUserDTO();
        etienne.setUsername("Etienne");
        etienne.setId(1L);

        stefan = new BibwebUserDTO();
        stefan.setUsername("Stefan");
        stefan.setId(2L);

        bookRepository = Mockito.mock(BookRepository.class);
        checkoutRepository = Mockito.mock(CheckoutRepository.class);
        userService = Mockito.mock(UserService.class);

        availabilityService = new AvailabilityService(bookRepository, checkoutRepository, userService);

        given(userService.getCurrentUser()).willReturn(etienne);
    }

    @Test
    public void whenRequestingBook_thenReturnUnavailableIfOtherUserHasBookReserved() {
        given(userService.getCurrentUser()).willReturn(stefan);
        testAvailabilityWithPresentCheckout(BookCheckoutState.UNAVAILABLE, true);
    }

    @Test
    public void whenRequestingBook_thenReturnReservedByYouIfUserHasBookReserved() {
        testAvailabilityWithPresentCheckout(BookCheckoutState.CHECKEDOUT_BY_YOU, true);
    }

    @Test
    public void whenRequestingBook_thenReturnAvailableIfNotLastCheckoutIsNotReturnedSameUser() {
        testAvailabilityWithPresentCheckout(BookCheckoutState.AVAILABLE, false);
    }

    @Test
    public void whenRequestingBook_thenReturnAvailableIfNotLastCheckoutIsNotReturnedDifferentUser() {
        given(userService.getCurrentUser()).willReturn(stefan);
        testAvailabilityWithPresentCheckout(BookCheckoutState.AVAILABLE, false);
    }

    @Test
    public void whenRequestingBook_thenReturnAvailableIfNoCheckoutsYet() {
        given(userService.getCurrentUser()).willReturn(stefan);
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(new Book()));
        Assert.assertEquals(BookCheckoutState.AVAILABLE, availabilityService.getAvailabilityBasedOnCheckouts(1L));
    }

    private void testAvailabilityWithPresentCheckout(BookCheckoutState expected, Boolean active) {
        Checkout checkout = new Checkout();
        checkout.setUserId(etienne.getId());
        checkout.setStillOut(active);

        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(new Book()));
        Mockito.when(checkoutRepository.findTop1ByBookIdOrderByCheckoutDateDesc(1L)).thenReturn(Optional.of(checkout));

        Assert.assertEquals(expected, availabilityService.getAvailabilityBasedOnCheckouts(1L));
    }
}
