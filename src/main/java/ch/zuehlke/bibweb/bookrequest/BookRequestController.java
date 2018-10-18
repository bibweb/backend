package ch.zuehlke.bibweb.bookrequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookrequest")
public class BookRequestController {

    private final
    BookRequestService bookRequestService;

    public BookRequestController(BookRequestService bookRequestService) {
        this.bookRequestService = bookRequestService;
    }

    @GetMapping
    public List<BookRequest> getBookRequests(Authentication authentication) {
        isAdmin(authentication);

        List<BookRequest> bookRequests;

        if (isAdmin(authentication)) {
            bookRequests = this.bookRequestService.getBookRequests();
        } else {
            bookRequests = this.bookRequestService.getBookRequestsFromUser(authentication.getName());
        }
        return bookRequests;
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(s -> s.equals("ROLE_ADMIN"));
    }

    @PostMapping
    public ResponseEntity<BookRequest> createBookRequest(@RequestBody BookRequest bookRequest) {
        BookRequest savedBookRequest = bookRequestService.createBookRequest(bookRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedBookRequest);
    }

    @GetMapping("/{id}")
    @PostAuthorize("hasRole('ADMIN') or returnObject.user.equals(authentication.name)")
    public BookRequest getBookRequestDetails(@PathVariable long id) {
        return this.bookRequestService.getBookRequestDetails(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBookRequest(@PathVariable long id, @RequestBody BookRequest bookRequest) {
        bookRequest.setId(id);
        this.bookRequestService.updateBookRequest(bookRequest);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private void bookRequestNotFound(BookRequestNotFoundException ex) {
    }
}
