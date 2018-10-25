package ch.zuehlke.bibweb.book;

import ch.zuehlke.bibweb.book.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/books")
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/books/{id}")
    public BookDTO getBookById(@PathVariable("id") int id) {
        return bookService.getBookById((long) id);
    }

    @PutMapping("/books/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void updateBook(@PathVariable("id") int id, @RequestBody BookDTO book) {
        bookService.updateBook((long) id, book);
    }

    @PutMapping("/books/{id}/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public void createReservationForBook(@PathVariable("id") int id) {
        bookService.reserveBook((long) id);
    }

    @DeleteMapping("/books/{id}/reservations")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteActiveReservationForBook(@PathVariable("id") int id) { bookService.deleteActiveReservationForBook((long) id); }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void reservationBelongsToOtherUser(CannotDeleteReservationForOtherUserException ex){
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void reservationDoesNotExist(ReservationDoesNotExistException ex){
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reservationAlreadyExists(ReservationAlreadyExistsForUserException ex){
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void bookIsReservedByAnotherUser(BookCannotBeReservedException ex) {
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private void bookNotFound(BookNotFoundException ex) {
    }

}
