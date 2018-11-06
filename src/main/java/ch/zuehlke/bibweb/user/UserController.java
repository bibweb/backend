package ch.zuehlke.bibweb.user;

import ch.zuehlke.bibweb.book.exception.*;
import ch.zuehlke.bibweb.checkout.Checkout;
import ch.zuehlke.bibweb.checkout.CheckoutDTO;
import ch.zuehlke.bibweb.checkout.CheckoutService;
import ch.zuehlke.bibweb.reservation.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/me")
    public BibwebUserDTO getCurrentUser() {
        return userService.getCurrentUser();
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<BibwebUserDTO> getUsers() {
        return this.userService.getUsers();
    }

    @GetMapping("/{id}/checkouts")
    public List<CheckoutDTO> getCheckoutsByUser(@PathVariable("id") int id) {
        return checkoutService.getCheckouts((long) id);
    }

    @PutMapping("/{id}/checkouts/books/{bookId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void checkoutBook(@PathVariable("id") int id, @PathVariable("bookId") long bookId) {
        checkoutService.checkoutBook((long) id, bookId);
    }

    @DeleteMapping("/{id}/checkouts/books/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void returnBook(@PathVariable("id") int id, @PathVariable("bookId") int bookId) {
        checkoutService.returnBook((long) id, (long) bookId);
    }

    @PutMapping("/{id}/reservations/books/{bookdId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void reserveBook(@PathVariable("id") int id, @PathVariable("bookId") long bookId) {
        reservationService.createReservation((long) id, (long) bookId);
    }

    @DeleteMapping("/{id}/reservations/books/{bookdId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeReservation(@PathVariable("id") int id, @PathVariable("bookId") int bookId) {
        reservationService.removeReservation((long) id, (long) bookId);
    }

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
