package ch.zuehlke.bibweb.checkout;

import ch.zuehlke.bibweb.book.BookCheckoutState;
import ch.zuehlke.bibweb.book.BookRepository;
import ch.zuehlke.bibweb.book.BookReservationState;
import ch.zuehlke.bibweb.book.exception.BookNotFoundException;
import ch.zuehlke.bibweb.reservation.ReservationService;
import ch.zuehlke.bibweb.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AvailabilityService {

    private CheckoutRepository checkoutRepository;
    private BookRepository bookRepository;
    private UserService userService;
    private ReservationService reservationService;

    @Autowired
    public AvailabilityService(BookRepository bookRepository,
                               CheckoutRepository checkoutRepository,
                               UserService userService,
                               ReservationService reservationService) {
        this.bookRepository = bookRepository;
        this.checkoutRepository = checkoutRepository;
        this.userService = userService;
        this.reservationService = reservationService;
    }

    public BookCheckoutState getAvailabilityBasedOnCheckouts(Long bookId) {
        if(!bookRepository.findById(bookId).isPresent()) throw new BookNotFoundException();

        BookCheckoutState retVal = BookCheckoutState.AVAILABLE;

        Optional<Checkout> reservation = checkoutRepository.findTop1ByBookIdOrderByCheckoutDateDesc(bookId);
        if (reservation.isPresent()) {
            if (!reservation.get().getStillOut()) {
                retVal = BookCheckoutState.AVAILABLE;
            } else {
                if (reservation.get().getUserId().equals(userService.getCurrentUser().getId())) {
                    retVal = BookCheckoutState.CHECKEDOUT_BY_YOU;
                } else {
                    retVal = BookCheckoutState.UNAVAILABLE;
                }
            }
        }

        return retVal;
    }

    public BookReservationState getBookReservationStateForCurrentUser(Long bookId) {
        return reservationService.reservationExistsForUser(userService.getCurrentUser().getId(), bookId)
                ? BookReservationState.RESERVED_BY_YOU
                : BookReservationState.NOT_RESERVED_BY_YOU;
    }

}
