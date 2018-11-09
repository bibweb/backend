package ch.zuehlke.bibweb.book;

import ch.zuehlke.bibweb.book.exception.BookNotFoundException;
import ch.zuehlke.bibweb.book.projection.BookIdAndTitle;
import ch.zuehlke.bibweb.checkout.AvailabilityService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    private BookRepository bookRepository;
    private AvailabilityService availabilityService;

    @Autowired
    public BookService(BookRepository bookRepository, AvailabilityService availabilityService) {
        this.bookRepository = bookRepository;
        this.availabilityService = availabilityService;
    }

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream().map(this::mapBookToBookDTO).collect(Collectors.toList());
    }

    public BookDTO getBookById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) return mapBookToBookDTO(book.get());

        throw new BookNotFoundException();
    }

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

    private BookDTO mapBookToBookDTO(Book book) {
        BookDTO dto = new BookDTO();
        BeanUtils.copyProperties(book, dto);
        dto.setAvailability(availabilityService.getAvailabilityBasedOnCheckouts(dto.getId()));
        dto.setReservationState(availabilityService.getBookReservationStateForCurrentUser(dto.getId()));
        return dto;
    }

    public String getBookTitleById(Long bookId) {
        Optional<BookIdAndTitle> bookWithTitle = bookRepository.findIdAndTitleById(bookId);

        if(bookWithTitle.isPresent()) return bookWithTitle.get().getTitle();
        return "";
    }

    public List<Long> getAllBookIds() {
        return bookRepository.findAll().stream().map(book -> book.getId()).collect(Collectors.toList());
    }
}
