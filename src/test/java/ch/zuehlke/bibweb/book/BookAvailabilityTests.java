package ch.zuehlke.bibweb.book;

import ch.zuehlke.bibweb.checkout.AvailabilityService;
import ch.zuehlke.bibweb.config.UserDetailTestService;
import ch.zuehlke.bibweb.checkout.Checkout;
import ch.zuehlke.bibweb.checkout.CheckoutRepository;
import ch.zuehlke.bibweb.user.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
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
    static class BookAvailabilityTestContextConfiguration {
        @Bean
        public AvailabilityService availabilityService() {
            return new AvailabilityService();
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return new UserDetailTestService();
        }
    }

    @MockBean
    private CheckoutRepository checkoutRepository;

    @Autowired
    private AvailabilityService availabilityService;

    @MockBean BookRepository bookRepository;

    @Test
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenRequestingBook_thenReturnUnavailableIfOtherUserHasBookReserved() {
        testAvailabilityWithPresentCheckout(BookCheckoutState.UNAVAILABLE, true);
    }

    @Test
    @WithUserDetails(value = "Etienne", userDetailsServiceBeanName = "userDetailsService")
    public void whenRequestingBook_thenReturnReservedByYouIfUserHasBookReserved() {
        testAvailabilityWithPresentCheckout(BookCheckoutState.CHECKEDOUT_BY_YOU, true);
    }

    @Test
    @WithUserDetails(value = "Etienne", userDetailsServiceBeanName = "userDetailsService")
    public void whenRequestingBook_thenReturnAvailableIfNotLastCheckoutIsNotReturnedSameUser() {
        testAvailabilityWithPresentCheckout(BookCheckoutState.AVAILABLE, false);
    }

    @Test
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenRequestingBook_thenReturnAvailableIfNotLastCheckoutIsNotReturnedDifferentUser() {
        testAvailabilityWithPresentCheckout(BookCheckoutState.AVAILABLE, false);
    }

    @Test
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenRequestingBook_thenReturnAvailableIfNoCheckoutsYet() {
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(new Book()));
        Assert.assertEquals(BookCheckoutState.AVAILABLE, availabilityService.getAvailabilityBasedOnCheckouts(1L));
    }

    private void testAvailabilityWithPresentCheckout(BookCheckoutState expected, Boolean active) {
        User user = new User();
        user.setUsername("Etienne");
        user.setId(1L);

        Checkout checkout = new Checkout();
        checkout.setUserId(user.getId());
        checkout.setStillOut(active);

        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(new Book()));
        Mockito.when(checkoutRepository.findTop1ByBookIdOrderByCheckoutDateDesc(1L)).thenReturn(Optional.of(checkout));

        Assert.assertEquals(expected, availabilityService.getAvailabilityBasedOnCheckouts(1L));
    }
}
