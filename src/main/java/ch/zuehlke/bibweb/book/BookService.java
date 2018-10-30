package ch.zuehlke.bibweb.book;

import ch.zuehlke.bibweb.book.exception.BookNotFoundException;
import ch.zuehlke.bibweb.checkout.AvailabilityService;
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
    private AvailabilityService availabilityService;

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

    private BookDTO mapBookToBookDTO(Book book) {
        BookDTO dto = new BookDTO();
        BeanUtils.copyProperties(book, dto);
        dto.setAvailability(availabilityService.getAvailabilityBasedOnCheckouts(dto.getId()));
        return dto;
    }

}
