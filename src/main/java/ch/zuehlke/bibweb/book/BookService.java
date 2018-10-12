package ch.zuehlke.bibweb.book;

import org.springframework.beans.factory.annotation.Autowired;
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

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

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