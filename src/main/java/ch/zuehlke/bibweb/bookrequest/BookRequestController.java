package ch.zuehlke.bibweb.bookrequest;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<BookRequest> getBookRequests() {
        return this.bookRequestService.getBookRequests();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookRequest createBookRequest(@RequestBody BookRequest bookRequest) {
        return bookRequestService.createBookRequest(bookRequest);
    }

    @GetMapping("/{id}")
    @PostAuthorize("hasRole('ADMIN') or returnObject.user.equals(authentication.name)")
    public BookRequest getBookRequestDetails(@PathVariable Integer id) {
        return this.bookRequestService.getBookRequestDetails(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public BookRequest updateBookRequest(@PathVariable long id, @RequestBody BookRequest bookRequest) {
        bookRequest.setId(id);
        return this.bookRequestService.updateBookRequest(bookRequest);
    }

    @PutMapping("/{id}/accept")
    @PreAuthorize("hasRole('ADMIN')")
    public BookRequest acceptBookRequest(@PathVariable long id, @RequestBody BookRequest bookRequest) {
        if (id != bookRequest.getId()) {
            throw new IllegalArgumentException("Body doesn't match url.");
        }
        return this.bookRequestService.acceptBookRequest(bookRequest);
    }

    @PutMapping("/{id}/decline")
    @PreAuthorize("hasRole('ADMIN')")
    public BookRequest declineBookRequest(@PathVariable long id, @RequestBody BookRequest bookRequest) {
        if (id != bookRequest.getId()) {
            throw new IllegalArgumentException("Body doesn't match url.");
        }
        return this.bookRequestService.declineBookRequest(bookRequest);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private void bookRequestNotFound(BookRequestNotFoundException ex) {
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private void illegalArgument(IllegalArgumentException ex) {
    }
}
