package ch.zuehlke.bibweb.reservation;

import ch.zuehlke.bibweb.config.UserDetailTestService;
import ch.zuehlke.bibweb.config.WebSecurityTestConfig;
import ch.zuehlke.bibweb.reservation.exception.ReservationAlreadyExistsForUserException;
import ch.zuehlke.bibweb.reservation.exception.ActiveReservationDoesNotExistsForUserException;
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

import java.util.Arrays;
import java.util.List;

import static ch.zuehlke.bibweb.config.UserDetailTestService.USER_ETIENNE_ID;
import static ch.zuehlke.bibweb.config.UserDetailTestService.USER_STEFAN_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@Import(WebSecurityTestConfig.class)
public class ReservationServiceTests {

    @TestConfiguration
    static class BookServiceTestContextConfiguration {
        @Bean
        public ReservationService reservationService() {
            return new ReservationService();
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return new UserDetailTestService();
        }
    }

    @MockBean
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationService reservationService;

    private Reservation reservation1;
    private Reservation reservation2;

    @Before
    public void setUp() {
        reservation1 = new Reservation();
        reservation1.setId(1L);
        reservation1.setActive(false);
        reservation1.setBookId(1L);
        reservation1.setUserId(USER_STEFAN_ID);
        reservation2 = new Reservation();
        reservation2.setId(2L);
        reservation2.setBookId(2L);
        reservation1.setUserId(USER_STEFAN_ID);
        reservation2.setActive(true);

        given(reservationRepository.findReservationsByUserId(USER_STEFAN_ID)).willReturn(Arrays.asList(reservation1, reservation2));
    }

    @Test
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenUserHasTwoReservations_thenReturnThem() {
        List<ReservationDTO> retVal = reservationService.getAllReservationsByUser(USER_STEFAN_ID);
        Assert.assertEquals(2, retVal.size());
    }

    @Test
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenUserHasJustOneReservation_thenReturnIt() {
        given(reservationRepository.findReservationsByUserId(USER_STEFAN_ID)).willReturn(Arrays.asList(reservation1));

        List<ReservationDTO> retVal = reservationService.getAllReservationsByUser(USER_STEFAN_ID);
        Assert.assertEquals(1, retVal.size());
    }

    @Test
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenCheckingIfReservationExistsForUser_thenReturnTrueIfReservationExistsAndIsActive() {
        Assert.assertTrue(reservationService.reservationExistsForUser(USER_STEFAN_ID, 2L));
    }

    @Test
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenCheckingIfReservationExistsForUser_thenReturnFalseIfReservationExistsAndIsNotActive() {
        Assert.assertFalse(reservationService.reservationExistsForUser(USER_STEFAN_ID, 1L));
    }

    @Test
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenCheckingIfReservationExistsForUser_thenReturnFalseIfReservatioDoesNotExist() {
        Assert.assertFalse(reservationService.reservationExistsForUser(USER_STEFAN_ID, 99L));
    }

    @Test(expected = ReservationAlreadyExistsForUserException.class)
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenCreatingReservationAndItAlreadyExistsForBook_thenThrowError() {
        reservationService.createReservation(USER_STEFAN_ID, 2L);
    }

    @Test
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenCreatingReservationAndItDoesNotExistActive_thenCreate() {
        reservationService.createReservation(USER_STEFAN_ID, 1L);

        ArgumentCaptor<Reservation> capture = ArgumentCaptor.forClass(Reservation.class);
        Mockito.verify(reservationRepository, Mockito.times(1)).saveAndFlush(capture.capture());

        Assert.assertEquals(1L, (long) capture.getValue().getBookId());
        Assert.assertEquals(USER_STEFAN_ID, (long) capture.getValue().getUserId());
    }

    @Test
    @WithUserDetails(value = "Etienne", userDetailsServiceBeanName = "userDetailsService")
    public void whenCreatingReservationAndItDoesNotExist_thenCreate() {
        reservationService.createReservation(USER_ETIENNE_ID, 2L);

        ArgumentCaptor<Reservation> capture = ArgumentCaptor.forClass(Reservation.class);
        Mockito.verify(reservationRepository, Mockito.times(1)).saveAndFlush(capture.capture());

        Assert.assertEquals(2L, (long) capture.getValue().getBookId());
        Assert.assertEquals(USER_ETIENNE_ID, (long) capture.getValue().getUserId());
    }

    @Test(expected = ActiveReservationDoesNotExistsForUserException.class)
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenRemovingReservationAndItIsNotActive_thenThrowException() {
        reservationService.removeReservation(USER_STEFAN_ID, 34L);
        Mockito.verify(reservationRepository, Mockito.times(0)).saveAndFlush(any(Reservation.class));
    }

    @Test(expected = ActiveReservationDoesNotExistsForUserException.class)
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenRemovingReservationAndItDoesNotExist_thenThrowException() {
        reservationService.removeReservation(USER_STEFAN_ID, 1L);
    }

    @Test
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenRemovingReservationAndItIsActive_thenSetActiveToFalse() {
        reservationService.removeReservation(USER_STEFAN_ID, 2L);
        ArgumentCaptor<Reservation> capture = ArgumentCaptor.forClass(Reservation.class);
        Mockito.verify(reservationRepository, Mockito.times(1)).saveAndFlush(capture.capture());

        Assert.assertEquals(2L, (long) capture.getValue().getId());
        Assert.assertEquals(false, capture.getValue().getActive());
    }
}
