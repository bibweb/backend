package ch.zuehlke.bibweb.bookrequest.business;

import ch.zuehlke.bibweb.bookrequest.data.BookRequest;
import ch.zuehlke.bibweb.bookrequest.data.BookRequestRepository;
import ch.zuehlke.bibweb.bookrequest.data.BookRequestState;
import ch.zuehlke.bibweb.bookrequest.exception.BookRequestNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

public class BookRequestServiceTest {
    private BookRequestService bookRequestService;
    private BookRequestRepository bookRequestRepository = Mockito.mock(BookRequestRepository.class);

    @Before
    public void setUp() {
        this.bookRequestService = new BookRequestService(bookRequestRepository);
    }

    @Test
    public void getAllBookRequests() {
        given(this.bookRequestRepository.findAll()).willReturn(Arrays.asList(
                new BookRequest((long) 1, "1351381379", "user1", BookRequestState.NEW),
                new BookRequest((long) 2, "1316503155", "user2", BookRequestState.NEW)
        ));

        List<BookRequestDTO> bookRequests = this.bookRequestService.getAllBookRequests();

        assertEquals(2, bookRequests.size());
    }

    @Test
    public void getBookRequestForUser() {
        given(this.bookRequestRepository.findAllByUser("user")).willReturn(Collections.singletonList(
                new BookRequest((long) 1, "156415416+", "user", BookRequestState.NEW)
        ));

        List<BookRequestDTO> bookRequests = this.bookRequestService.getBookRequestsForUser("user");

        assertEquals(1, bookRequests.size());
        assertEquals("user", bookRequests.get(0).getUser());
    }

    @Test
    public void getBookRequestDetails() {
        BookRequest bookRequest = new BookRequest((long) 99, "2131513135", "user", BookRequestState.NEW);

        given(this.bookRequestRepository.findById((long) 99)).willReturn(Optional.of(bookRequest));

        BookRequestDTO retrievedBookRequest = this.bookRequestService.getBookRequestDetails(99);

        assertEquals(bookRequest.getId(), retrievedBookRequest.getId());
        assertEquals(bookRequest.getIsbn(), retrievedBookRequest.getIsbn());
        assertEquals(bookRequest.getUser(), retrievedBookRequest.getUser());
        assertEquals(bookRequest.getState(), retrievedBookRequest.getState());
    }

    @Test(expected = BookRequestNotFoundException.class)
    public void getBookRequestDetails_NotFound() {
        given(bookRequestRepository.findById(anyLong())).willReturn(Optional.empty());

        this.bookRequestService.getBookRequestDetails(99);
    }

    @Test
    @WithMockUser
    public void createBookRequest() {
        given(this.bookRequestRepository.save(any(BookRequest.class))).willReturn(
                new BookRequest((long) 1, "1321515315", "user", BookRequestState.NEW)
        );

        BookRequestDTO bookRequestToSave = new BookRequestDTO("1315531383518");
        BookRequestDTO savedBookRequest = this.bookRequestService.createBookRequest(bookRequestToSave);

        assertEquals("user", savedBookRequest.getUser());
        assertEquals(BookRequestState.NEW, savedBookRequest.getState());
    }

    @Test
    public void updateBookRequest() {
        final BookRequestDTO bookRequest = new BookRequestDTO((long) 1, "215155151", "user", BookRequestState.NEW);
        final BookRequest bookRequestEntity = new BookRequest((long) 1, "215155151", "user", BookRequestState.NEW);

        given(this.bookRequestRepository.existsById(anyLong())).willReturn(true);
        given(this.bookRequestRepository.save(any(BookRequest.class))).willReturn(bookRequestEntity);

        final BookRequestDTO updatedBookRequest = this.bookRequestService.updateBookRequest(bookRequest);

        assertEquals(bookRequest.getId(), updatedBookRequest.getId());
    }

    @Test(expected = BookRequestNotFoundException.class)
    public void updateBookRequest_nonExistingBookRequest() {
        final BookRequestDTO bookRequest = new BookRequestDTO((long) 1, "215155151", "user", BookRequestState.NEW);

        given(this.bookRequestRepository.existsById(anyLong())).willReturn(false);

        this.bookRequestService.updateBookRequest(bookRequest);
    }

    @Test
    public void acceptBookRequest() {
        final BookRequestDTO bookRequest = new BookRequestDTO((long) 1, "215155151", "user", BookRequestState.NEW);

        given(this.bookRequestRepository.findById((long) 1)).willReturn(Optional.of(
                new BookRequest((long) 1, "215155151", "user", BookRequestState.NEW)
        ));
        given(this.bookRequestRepository.save(any(BookRequest.class)))
                .willReturn(new BookRequest((long) 1, "215155151", "user", BookRequestState.ACCEPTED));

        BookRequestDTO acceptedBookRequest = this.bookRequestService.acceptBookRequest(bookRequest);

        assertEquals(BookRequestState.ACCEPTED, acceptedBookRequest.getState());

    }

    @Test(expected = BookRequestNotFoundException.class)
    public void acceptBookRequest_nonExistingBookRequest() {
        final BookRequestDTO bookRequest = new BookRequestDTO((long) 1, "215155151", "user", BookRequestState.NEW);

        given(this.bookRequestRepository.existsById((long) 1)).willReturn(false);

        this.bookRequestService.acceptBookRequest(bookRequest);
    }

    @Test
    public void declineBookRequest() {
        final BookRequestDTO bookRequest = new BookRequestDTO((long) 1, "215155151", "user", BookRequestState.NEW);

        given(this.bookRequestRepository.findById((long) 1)).willReturn(Optional.of(
                new BookRequest((long) 1, "215155151", "user", BookRequestState.NEW)
        ));
        given(this.bookRequestRepository.save(any(BookRequest.class)))
                .willReturn(new BookRequest((long) 1, "215155151", "user", BookRequestState.DECLINED));

        BookRequestDTO acceptedBookRequest = this.bookRequestService.declineBookRequest(bookRequest);

        assertEquals(BookRequestState.DECLINED, acceptedBookRequest.getState());

    }

    @Test(expected = BookRequestNotFoundException.class)
    public void declineBookRequest_nonExistingBookRequest() {
        final BookRequestDTO bookRequest = new BookRequestDTO((long) 1, "215155151", "user", BookRequestState.NEW);

        given(this.bookRequestRepository.existsById((long) 1)).willReturn(false);

        this.bookRequestService.declineBookRequest(bookRequest);
    }
}