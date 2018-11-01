package ch.zuehlke.bibweb.book;

import ch.zuehlke.bibweb.book.exception.*;
import ch.zuehlke.bibweb.checkout.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private CheckoutService checkoutService;

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

    @PutMapping("/books/{id}/checkouts")
    @ResponseStatus(HttpStatus.CREATED)
    public void checkoutBook(@PathVariable("id") int id) {
        checkoutService.checkoutBookForCurrentUser((long) id);
    }

    @DeleteMapping("/books/{id}/checkouts")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void returnBook(@PathVariable("id") int id) { checkoutService.returnBookForCurrentUser((long) id); }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void checkoutBelongsToOtherUser(CannotDeleteCheckoutForOtherUserException ex){
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void checkoutDoesNotExist(CheckoutDoesNotExistException ex){
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void checkoutAlreadyExists(CheckoutAlreadyExistsForUserException ex){
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void bookIsCheckedoutByAnotherUser(BookCannotBeCheckedOut ex) {
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private void bookNotFound(BookNotFoundException ex) {
    }

}
