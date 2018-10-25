package ch.zuehlke.bibweb.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/book")
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/book/{id}")
    public BookDTO getBookById(@PathVariable("id") int id) {
        return bookService.getBookById((long) id);
    }

    @PutMapping("/book/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void updateBook(@PathVariable("id") int id, @RequestBody BookDTO book) {
        bookService.updateBook((long) id, book);
    }

    @PostMapping("/book/{id}/reserve")
    @ResponseStatus(HttpStatus.CREATED)
    public void createReservationForBookd(@PathVariable("id") int id) {
        bookService.reserveBook((long) id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reservationAlreadyExists(ReservationAlreadyExistsForUser ex){
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void bookIsReservedByAnotherUser(BookCannotBeReservedException ex) {
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private void bookNotFound(BookNotFoundExcpetion ex) {
    }

}
