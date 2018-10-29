package ch.zuehlke.bibweb.book;

import ch.zuehlke.bibweb.book.exception.*;
import ch.zuehlke.bibweb.checkout.Checkout;
import ch.zuehlke.bibweb.checkout.CheckoutRepository;
import ch.zuehlke.bibweb.user.UserSecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CheckoutRepository checkoutRepository;

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream().map(book -> mapBookToBookDTO(book)).collect(Collectors.toList());
    }

    public BookDTO getBookById(Long id) throws BookNotFoundException {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) return mapBookToBookDTO(book.get());

        throw new BookNotFoundException();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void updateBook(Long id, BookDTO book) {
        if (book != null) {
            book.setId(id);

            Optional<Book> bookInDb = bookRepository.findById(id);
            if (bookInDb.isPresent()) {
                Book oldBook = bookInDb.get();
                oldBook.setNumberOfPages(book.getNumberOfPages());
                oldBook.setReleaseYear(book.getReleaseYear());
                oldBook.setIsbn(book.getIsbn());
                oldBook.setTitle(book.getTitle());

                bookRepository.save(oldBook);
            }
        }
    }

    public Checkout checkoutBook(Long bookId) throws BookCannotBeCheckedOut, CheckoutAlreadyExistsForUserException, BookNotFoundException {
        BookCheckoutState availabilityState = getAvailabilityBasedOnReservations(bookId);

        final BookDTO book = getBookById(bookId);

        if (availabilityState.equals(BookCheckoutState.AVAILABLE)) {
            Checkout res = new Checkout();
            res.setStillOut(true);
            res.setBookId(book.getId());
            res.setUserId(UserSecurityUtil.getCurrentUser().getId());

            res = checkoutRepository.saveAndFlush(res);
            return res;
        }
        if (availabilityState.equals(BookCheckoutState.CHECKEDOUT_BY_YOU)) {
            throw new CheckoutAlreadyExistsForUserException();
        }

        throw new BookCannotBeCheckedOut();
    }

    private BookCheckoutState getAvailabilityBasedOnReservations(Long bookId) {
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

    private BookCheckoutState checkAvailability(Long bookId) {
        return getAvailabilityBasedOnReservations(bookId);
    }

    private BookDTO mapBookToBookDTO(Book book) {
        BookDTO dto = new BookDTO();
        BeanUtils.copyProperties(book, dto);
        dto.setAvailability(checkAvailability(dto.getId()));
        return dto;
    }

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
