package ch.zuehlke.bibweb.user;

import ch.zuehlke.bibweb.book.exception.BookCannotBeCheckedOut;
import ch.zuehlke.bibweb.book.exception.CannotDeleteCheckoutForOtherUserException;
import ch.zuehlke.bibweb.book.exception.CheckoutAlreadyExistsForUserException;
import ch.zuehlke.bibweb.book.exception.CheckoutDoesNotExistException;
import ch.zuehlke.bibweb.checkout.CheckoutService;
import ch.zuehlke.bibweb.config.WebSecurityTestConfig;
import ch.zuehlke.bibweb.reservation.ReservationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UserController.class)
@ContextConfiguration(classes = {WebSecurityTestConfig.class, UserController.class})
@WebAppConfiguration
public class UserControllerCheckoutTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private CheckoutService checkoutService;

    @MockBean
    private ReservationService reservationService;

    @Test
    @WithMockUser(roles = "USER")
    public void whenCheckingOutBook_thenStatusShouldBeIsCreated() throws Exception {
        this.mvc.perform(put("/users/1/checkouts/books/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void whenCheckingOutBookAndNotPossible_thenStatusShouldBeForbidden() throws Exception {
        Mockito.when(checkoutService.checkoutBook(1L, 1L)).thenThrow(BookCannotBeCheckedOut.class);

        this.mvc.perform(put("/users/1/checkouts/books/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void whenCheckingOutBookAndAlreadyExistsForUser_thenStatusShouldBeNoContent() throws Exception {
        Mockito.when(checkoutService.checkoutBook(1L, 1L)).thenThrow(CheckoutAlreadyExistsForUserException.class);

        this.mvc.perform(put("/users/1/checkouts/books/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void whenDeletingCheckoutForOtherUser_thenStatusShouldBeForbidden() throws Exception {
        Mockito.doThrow(CannotDeleteCheckoutForOtherUserException.class).when(checkoutService).returnBook(1L,1L);

        this.mvc.perform(delete("/users/1/checkouts/books/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void whenDeletingNonExistingCheckout_thenStatusShouldBeNotFound() throws Exception {
        Mockito.doThrow(CheckoutDoesNotExistException.class).when(checkoutService).returnBook(1L, 1L);

        this.mvc.perform(delete("/users/1/checkouts/books/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void whenDeletingExistingCheckout_thenStatusShouldBeNoContent() throws Exception {
        this.mvc.perform(delete("/users/1/checkouts/books/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

}
