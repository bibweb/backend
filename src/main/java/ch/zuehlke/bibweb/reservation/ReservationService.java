package ch.zuehlke.bibweb.reservation;

import ch.zuehlke.bibweb.reservation.exception.ReservationAlreadyExistsForUserException;
import ch.zuehlke.bibweb.reservation.exception.ActiveReservationDoesNotExistsForUserException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN') or hasAuthority('ROLE_ADMIN') or authentication.principal.getId() == #userId")
    public void createReservation(long userId, long bookId) throws ReservationAlreadyExistsForUserException {
        if(reservationExistsForUser(userId, bookId)) throw new ReservationAlreadyExistsForUserException();

        Reservation reservation = new Reservation();
        reservation.setActive(true);
        reservation.setUserId(userId);
        reservation.setBookId(bookId);

        reservationRepository.saveAndFlush(reservation);
    }

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN') or hasAuthority('ROLE_ADMIN') or authentication.principal.getId() == #userId")
    public boolean reservationExistsForUser(long userId, long bookId) {
        List<ReservationDTO> reservations = getAllReservationsByUser(userId);
        return (reservations.stream().filter(reservation -> reservation.getBookId().equals(bookId) && reservation.getActive()).count() > 0);
    }

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN') or hasAuthority('ROLE_ADMIN') or authentication.principal.getId() == #userId")
    public List<ReservationDTO> getAllReservationsByUser(long userId) {
        return reservationRepository.findReservationsByUserId(userId).stream().map(this::mapReservationEntityToDto).collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN') or hasAuthority('ROLE_ADMIN') or authentication.principal.getId() == #userId")
    public void removeReservation(long userId, long bookId) throws ActiveReservationDoesNotExistsForUserException {
        if(!reservationExistsForUser(userId, bookId)) {
            throw new ActiveReservationDoesNotExistsForUserException();
        }

        for(Reservation res : reservationRepository
                .findReservationsByUserId(userId)
                .stream()
                .filter(reservation -> reservation.getBookId().equals(bookId) && reservation.getActive()).collect(Collectors.toList())) {
            res.setActive(false);
            reservationRepository.saveAndFlush(res);
        }
    }

    private ReservationDTO mapReservationEntityToDto(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        BeanUtils.copyProperties(reservation, dto);
        return dto;
    }
}
