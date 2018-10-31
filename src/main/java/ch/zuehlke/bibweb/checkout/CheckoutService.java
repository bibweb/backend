package ch.zuehlke.bibweb.checkout;

import ch.zuehlke.bibweb.book.BookCheckoutState;
import ch.zuehlke.bibweb.book.exception.*;
import ch.zuehlke.bibweb.user.UserSecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CheckoutService {

    @Autowired
    private CheckoutRepository checkoutRepository;

    @Autowired
    private AvailabilityService availabilityService;

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN') or hasAuthority('ROLE_ADMIN') or authentication.principal.getId() == #id")
    public List<Checkout> getCheckouts(long userId) {
        return checkoutRepository.findAllByUserId(userId);
    }

    public Checkout checkoutBookForCurrentUser(long bookId) {
        return checkoutBook(UserSecurityUtil.getCurrentUser().getId(), bookId);
    }

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN') or hasAuthority('ROLE_ADMIN') or authentication.principal.getId() == #id")
    public Checkout checkoutBook(long id, long bookId) throws BookCannotBeCheckedOut, CheckoutAlreadyExistsForUserException, BookNotFoundException {
        BookCheckoutState availabilityState = availabilityService.getAvailabilityBasedOnCheckouts(bookId);

        if (availabilityState.equals(BookCheckoutState.AVAILABLE)) {
            Checkout res = new Checkout();
            res.setStillOut(true);
            res.setBookId(bookId);
            res.setUserId(UserSecurityUtil.getCurrentUser().getId());

            res = checkoutRepository.saveAndFlush(res);
            return res;
        }
        if (availabilityState.equals(BookCheckoutState.CHECKEDOUT_BY_YOU)) {
            throw new CheckoutAlreadyExistsForUserException();
        }

        throw new BookCannotBeCheckedOut();
    }

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN') or hasAuthority('ROLE_ADMIN') or authentication.principal.getId() == #id")
    public void returnBook(long bookId) throws CheckoutDoesNotExistException, CannotDeleteCheckoutForOtherUserException {
        Optional<Checkout> reservation = checkoutRepository.findTop1ByBookIdOrderByCheckoutDateDesc(bookId);

        if (!reservation.isPresent()) throw new CheckoutDoesNotExistException();
        Checkout res = reservation.get();

        if (res.getUserId().equals(UserSecurityUtil.getCurrentUser().getId())) {
            if(res.getStillOut()) {
                res.setStillOut(false);
                checkoutRepository.saveAndFlush(res);
            } else {
                throw new CheckoutDoesNotExistException();
            }
        } else {
            throw new CannotDeleteCheckoutForOtherUserException();
        }
    }

}
