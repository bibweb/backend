package ch.zuehlke.bibweb.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) throws BookNotFoundExcpetion {
        Optional<Book> book = bookRepository.findById(id);
        if(book.isPresent()) return book.get();

        throw new BookNotFoundExcpetion();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void updateBook(Long id, Book book) {
        if(book != null) {
            book.setId(id);

            Optional<Book> bookInDb = bookRepository.findById(id);
            if(bookInDb.isPresent()) {
                Book oldBook = bookInDb.get();
                oldBook.setNumberOfPages(book.getNumberOfPages());
                oldBook.setReleaseYear(book.getReleaseYear());
                oldBook.setIsbn(book.getIsbn());
                oldBook.setTitle(book.getTitle());

                bookRepository.save(oldBook);
            }
        }
    }

}
