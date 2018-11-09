package ch.zuehlke.bibweb.checkout;

import ch.zuehlke.bibweb.book.BookCheckoutState;
import ch.zuehlke.bibweb.book.BookService;
import ch.zuehlke.bibweb.book.exception.*;
import ch.zuehlke.bibweb.reservation.ReservationService;
import ch.zuehlke.bibweb.user.UserSecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CheckoutService {

    @Autowired
    private CheckoutRepository checkoutRepository;

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private BookService bookService;

    @Autowired
    private ReservationService reservationService;

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN') or hasAuthority('ROLE_ADMIN') or authentication.principal.getId() == #userId")
    public List<CheckoutDTO> getActiveCheckoutsByUser(long userId) {
        return checkoutRepository.findAllByUserId(userId)
                .stream()
                .map(this::mapCheckoutEntityToDTO)
                .filter(checkout -> checkout.getStillOut())
                .collect(Collectors.toList());
    }

    public CheckoutDTO checkoutBookForCurrentUser(long bookId) {
        return checkoutBook(UserSecurityUtil.getCurrentUser().getId(), bookId);
    }

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN') or hasAuthority('ROLE_ADMIN') or authentication.principal.getId() == #userId")
    public CheckoutDTO checkoutBook(long userId, long bookId) {
        BookCheckoutState availabilityState = availabilityService.getAvailabilityBasedOnCheckouts(bookId);

        if (availabilityState.equals(BookCheckoutState.AVAILABLE)) {
            Checkout checkout = new Checkout();
            checkout.setStillOut(true);
            checkout.setBookId(bookId);
            checkout.setUserId(userId);

            if (reservationService.reservationExistsForUser(checkout.getUserId(), bookId)) {
                reservationService.removeReservation(checkout.getUserId(), bookId);
            }
            checkout = checkoutRepository.saveAndFlush(checkout);
            return mapCheckoutEntityToDTO(checkout);
        }
        if (availabilityState.equals(BookCheckoutState.CHECKEDOUT_BY_YOU)) {
            throw new CheckoutAlreadyExistsForUserException();
        }

        throw new BookCannotBeCheckedOut();
    }

    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN') or hasAuthority('ROLE_ADMIN')")
    public void returnBook(long userId, long bookId) {
        Optional<Checkout> optionalCheckout = checkoutRepository.findTop1ByBookIdOrderByCheckoutDateDesc(bookId);

        if (!optionalCheckout.isPresent()) throw new CheckoutDoesNotExistException();
        Checkout checkout = optionalCheckout.get();

        if(userId == checkout.getUserId()) {
            if (checkout.getStillOut()) {
                checkout.setStillOut(false);
                checkoutRepository.saveAndFlush(checkout);
            } else {
                throw new CheckoutDoesNotExistException();
            }
        } else {
            throw new CheckoutDoesNotExistException();
        }
    }

    private CheckoutDTO mapCheckoutEntityToDTO(Checkout checkout) {
        CheckoutDTO dto = new CheckoutDTO();
        BeanUtils.copyProperties(checkout, dto);
        dto.setBookTitle(bookService.getBookTitleById(dto.getBookId()));
        return dto;
    }

}
