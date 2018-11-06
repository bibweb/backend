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

import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UserController.class)
@ContextConfiguration(classes = {WebSecurityTestConfig.class, UserController.class})
@WebAppConfiguration
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private CheckoutService checkoutService;

    @MockBean
    private ReservationService reservationService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void whenRequestingAllUsers_thenReturnAll() throws Exception {
        given(userService.getUsers()).willReturn(new ArrayList<>());

        mvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void whenRequestingAllUsersWithUserRole_thenIsForbidden() throws Exception {
        given(userService.getUsers()).willReturn(new ArrayList<>());

        mvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void whenRequestingCurrentUser_thenReturnUser() throws Exception {
        BibwebUserDTO dto = new BibwebUserDTO();
        dto.setUsername("Etienne");

        given(userService.getCurrentUser()).willReturn(dto);

        mvc.perform(get("/users/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("Etienne")));
    }

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
