package ch.zuehlke.bibweb.bookrequest;

import ch.zuehlke.bibweb.user.UserSecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookRequestService {

    @Autowired
    private BookRequestRepository bookRequestRepository;

    public List<BookRequest> getBookRequests() {
        if (UserSecurityUtil.currentUserHasRole("ROLE_ADMIN")) {
            return this.bookRequestRepository.findAll();
        } else {
            return this.bookRequestRepository.findAllByUser(UserSecurityUtil.currentUserName());
        }
    }

    public BookRequest createBookRequest(BookRequest bookRequest) {
        bookRequest.setUser(UserSecurityUtil.currentUserName());
        return this.bookRequestRepository.save(bookRequest);
    }

    public BookRequest getBookRequestDetails(final long bookRequestId) {
        final Optional<BookRequest> bookRequest = this.bookRequestRepository.findById(bookRequestId);
        if (bookRequest.isPresent()) {
            return bookRequest.get();
        } else {
            throw new BookRequestNotFoundException();
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public BookRequest updateBookRequest(final BookRequest bookRequest) {
        if (this.bookRequestRepository.existsById(bookRequest.getId())) {
            return this.bookRequestRepository.save(bookRequest);
        } else {
            throw new BookRequestNotFoundException();
        }
    }
}
