package ch.zuehlke.bibweb.reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN') or hasAuthority('ROLE_ADMIN') or authentication.principal.getId() == #userId")
    public void createReservation(long userId, long bookId) {
    }

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN') or hasAuthority('ROLE_ADMIN') or authentication.principal.getId() == #userId")
    public void removeReservation(long userId, long bookId) {
    }
}
