package ch.zuehlke.bibweb.book;

import ch.zuehlke.bibweb.reservation.Reservation;
import ch.zuehlke.bibweb.reservation.ReservationRepository;
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
    private ReservationRepository reservationRepository;

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream().map(book -> mapBookToBookDTO(book)).collect(Collectors.toList());
    }

    public BookDTO getBookById(Long id) throws BookNotFoundExcpetion {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) return mapBookToBookDTO(book.get());

        throw new BookNotFoundExcpetion();
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

    public Reservation reserveBook(Long bookId) throws BookCannotBeReservedException, ReservationAlreadyExistsForUser {
        BookAvailabilityState availabilityState = getAvailabilityBasedOnReservations(bookId);
        if(availabilityState.equals(BookAvailabilityState.AVAILABLE)) {
            Reservation res = new Reservation();
            res.setActive(true);
            res.setBookId(bookId);
            res.setUser(UserSecurityUtil.getCurrentUser());

            res = reservationRepository.saveAndFlush(res);
            return res;
        }
        if(availabilityState.equals(BookAvailabilityState.RESERVED_BY_YOU)) {
            throw new ReservationAlreadyExistsForUser();
        }

        throw new BookCannotBeReservedException();
    }

    private BookAvailabilityState getAvailabilityBasedOnReservations(Long bookId) {
        BookAvailabilityState retVal = BookAvailabilityState.AVAILABLE;

        Optional<Reservation> reservation = reservationRepository.findTop1ByBookIdOrderByReservedAtDesc(bookId);
        if (reservation.isPresent()) {
            if (reservation.get().getActive() == false) {
                retVal = BookAvailabilityState.AVAILABLE;
            } else {
                if (reservation.get().getUser().getId().equals(UserSecurityUtil.getCurrentUser().getId())) {
                    retVal = BookAvailabilityState.RESERVED_BY_YOU;
                } else {
                    retVal = BookAvailabilityState.UNAVAILABLE;
                }
            }
        }

        return retVal;
    }

    private BookAvailabilityState checkAvailability(Long bookId) {
        return getAvailabilityBasedOnReservations(bookId);
    }

    private BookDTO mapBookToBookDTO(Book book) {
        BookDTO dto = new BookDTO();
        BeanUtils.copyProperties(book, dto);
        dto.setAvailability(checkAvailability(dto.getId()));
        return dto;
    }
}
