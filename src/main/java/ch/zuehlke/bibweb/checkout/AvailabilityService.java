package ch.zuehlke.bibweb.checkout;

import ch.zuehlke.bibweb.book.BookCheckoutState;
import ch.zuehlke.bibweb.book.BookRepository;
import ch.zuehlke.bibweb.book.exception.BookNotFoundException;
import ch.zuehlke.bibweb.user.UserSecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AvailabilityService {

    @Autowired
    private CheckoutRepository checkoutRepository;

    @Autowired
    private BookRepository bookRepository;

    public BookCheckoutState getAvailabilityBasedOnCheckouts(Long bookId) throws BookNotFoundException {
        if(!bookRepository.findById(bookId).isPresent()) throw new BookNotFoundException();

        BookCheckoutState retVal = BookCheckoutState.AVAILABLE;

        Optional<Checkout> reservation = checkoutRepository.findTop1ByBookIdOrderByCheckoutDateDesc(bookId);
        if (reservation.isPresent()) {
            if (reservation.get().getStillOut() == false) {
                retVal = BookCheckoutState.AVAILABLE;
            } else {
                if (reservation.get().getUserId().equals(UserSecurityUtil.getCurrentUser().getId())) {
                    retVal = BookCheckoutState.CHECKEDOUT_BY_YOU;
                } else {
                    retVal = BookCheckoutState.UNAVAILABLE;
                }
            }
        }

        return retVal;
    }

}
