package ch.zuehlke.bibweb.bookrequest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookRequestService {

    private final BookRequestRepository bookRequestRepository;

    public BookRequestService(BookRequestRepository bookRequestRepository) {
        this.bookRequestRepository = bookRequestRepository;
    }

    public List<BookRequest> getBookRequests() {
        return this.bookRequestRepository.findAll();
    }

    public BookRequest createBookRequest(final BookRequest bookRequest) {
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
    public void updateBookRequest(final BookRequest bookRequest) {
        if (this.bookRequestRepository.existsById(bookRequest.getId())) {
            this.bookRequestRepository.save(bookRequest);
        } else {
            throw new BookRequestNotFoundException();
        }
    }
}
