package ch.zuehlke.bibweb.user;

import ch.zuehlke.bibweb.checkout.Checkout;
import ch.zuehlke.bibweb.checkout.CheckoutService;
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

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<BibwebUser> getUsers() {
        return this.userService.getUsers();
    }

    @GetMapping("/{id}/checkouts")
    public List<Checkout> getCheckoutsByUser(@PathVariable("id") int id) {
        return checkoutService.getCheckouts((long) id);
    }

    @PutMapping("/{id}/checkouts/book/{bookId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void checkoutBook(@PathVariable("id") int id, @PathVariable("bookId") long bookId) {
        checkoutService.checkoutBook((long) id, (long) bookId);
    }
}
