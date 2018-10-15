package ch.zuehlke.bibweb.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/book")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/book/{id}")
    public Book getBookById(@PathVariable("id") int id) {
        return bookService.getBookById((long) id);
    }

    @PutMapping("/book/{id}")
    public void updateBook(@PathVariable("id") int id, @RequestBody Book book) {
        bookService.updateBook((long) id, book);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private void bookNotFound(BookNotFoundExcpetion ex) {
    }

}
