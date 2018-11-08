package ch.zuehlke.bibweb.user;

import ch.zuehlke.bibweb.checkout.CheckoutService;
import ch.zuehlke.bibweb.reservation.ReservationDTO;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@ActiveProfiles("unit-test")
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

    @Test
    @WithMockUser(roles = "USER")
    public void whenGettingListOfReservations_thenStatusIsOkAndReceiveThem() throws Exception {
        given(reservationService.getAllReservationsByUser(1L)).willReturn(Arrays.asList(new ReservationDTO(), new ReservationDTO()));

        this.mvc.perform(get("/users/1/reservations")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void whenGettingListOfReservations_thenStatusIsOkAndReceiveEmptyList() throws Exception {
        this.mvc.perform(get("/users/1/reservations")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
