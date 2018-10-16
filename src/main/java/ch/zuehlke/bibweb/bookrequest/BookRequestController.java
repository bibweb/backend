package ch.zuehlke.bibweb.bookrequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookRequestController {

    private final
    BookRequestService bookRequestService;

    public BookRequestController(BookRequestService bookRequestService) {
        this.bookRequestService = bookRequestService;
    }

    @GetMapping("/bookrequest")
    public List<BookRequest> getBookRequests() {
        return this.bookRequestService.getBookRequests();
    }

    @GetMapping("/bookrequest/{id}")
    public BookRequest getBookRequestDetails(@PathVariable long id) {
        return this.bookRequestService.getBookRequestDetails(id);
    }

    @PostMapping("/bookrequest")
    public ResponseEntity<BookRequest> createBookRequest(@RequestBody BookRequest bookRequest) {
        BookRequest savedBookRequest = bookRequestService.createBookRequest(bookRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedBookRequest);
    }

    @PutMapping("/bookrequest/{id}")
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
