package ch.zuehlke.bibweb.bookrequest.web;

import ch.zuehlke.bibweb.bookrequest.business.BookRequestDTO;
import ch.zuehlke.bibweb.bookrequest.business.BookRequestService;
import ch.zuehlke.bibweb.bookrequest.data.BookRequest;
import ch.zuehlke.bibweb.bookrequest.exception.BookRequestNotFoundException;
import ch.zuehlke.bibweb.user.UserSecurityUtil;
import org.springframework.beans.BeanUtils;
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
    public List<BookRequestDTO> getBookRequests() {
        if (UserSecurityUtil.currentUserHasAuthority("ROLE_ADMIN")) {
           return this.bookRequestService.getAllBookRequests();
        } else {
            return this.bookRequestService.getBookRequestsForUser(UserSecurityUtil.currentUserName());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookRequestDTO createBookRequest(@RequestBody BookRequestDTO bookRequest) {
        return bookRequestService.createBookRequest(bookRequest);
    }

    @GetMapping("/{id}")
    @PostAuthorize("hasRole('ADMIN') or returnObject.user.equals(authentication.name)")
    public BookRequestDTO getBookRequestDetails(@PathVariable Integer id) {
        return this.bookRequestService.getBookRequestDetails(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public BookRequestDTO updateBookRequest(@PathVariable long id, @RequestBody BookRequestDTO bookRequest) {
        bookRequest.setId(id);
        return this.bookRequestService.updateBookRequest(bookRequest);
    }

    @PutMapping("/{id}/accept")
    @PreAuthorize("hasRole('ADMIN')")
    public BookRequestDTO acceptBookRequest(@PathVariable long id, @RequestBody BookRequestDTO bookRequest) {
        if (id != bookRequest.getId()) {
            throw new IllegalArgumentException("Body doesn't match url.");
        }
        return this.bookRequestService.acceptBookRequest(bookRequest);
    }

    @PutMapping("/{id}/decline")
    @PreAuthorize("hasRole('ADMIN')")
    public BookRequestDTO declineBookRequest(@PathVariable long id, @RequestBody BookRequestDTO bookRequest) {
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
