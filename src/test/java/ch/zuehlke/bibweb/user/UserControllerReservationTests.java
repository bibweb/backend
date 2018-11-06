package ch.zuehlke.bibweb.user;

import ch.zuehlke.bibweb.checkout.CheckoutService;
import ch.zuehlke.bibweb.config.WebSecurityTestConfig;
import ch.zuehlke.bibweb.reservation.ReservationService;
import ch.zuehlke.bibweb.reservation.exception.ActiveReservationDoesNotExistsForUserException;
import ch.zuehlke.bibweb.reservation.exception.ReservationAlreadyExistsForUserException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UserController.class)
@ContextConfiguration(classes = {WebSecurityTestConfig.class, UserController.class})
@WebAppConfiguration
public class UserControllerReservationTests {

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
    public void whenTryingToRemoveReservationAndExceptionIsThrown_thenIs404() throws Exception {
        doThrow(ActiveReservationDoesNotExistsForUserException.class).when(reservationService).removeReservation(1L, 1L);

        this.mvc.perform(delete("/users/1/reservations/books/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void whenTryingToRemoveReservationAndNoExceptionIsThrown_thenIsNoContent() throws Exception {
        this.mvc.perform(delete("/users/1/reservations/books/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void whenTryingToCreateReservationAndExceptionIsThrown_thenIsNoContent() throws Exception {
        doThrow(ReservationAlreadyExistsForUserException.class).when(reservationService).createReservation(1L, 1L);

        this.mvc.perform(put("/users/1/reservations/books/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void whenTryingToCreateReservationAndNoExceptionIsThrown_thenIsCreated() throws Exception {
        this.mvc.perform(put("/users/1/reservations/books/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}
