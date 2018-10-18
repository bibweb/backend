package ch.zuehlke.bibweb.bookrequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    public List<BookRequest> getBookRequests() {
        return this.bookRequestService.getBookRequests();
    }

    @PostMapping
    public ResponseEntity<BookRequest> createBookRequest(@RequestBody BookRequest bookRequest) {
        BookRequest savedBookRequest = bookRequestService.createBookRequest(bookRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedBookRequest);
    }

    @GetMapping("/{id}")
    public BookRequest getBookRequestDetails(@PathVariable long id) {
        return this.bookRequestService.getBookRequestDetails(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity updateBookRequest(@PathVariable long id, @RequestBody BookRequest bookRequest) {
        bookRequest.setId(id);
        this.bookRequestService.updateBookRequest(bookRequest);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private void bookRequestNotFound(BookRequestNotFoundException ex) {
    }
}
