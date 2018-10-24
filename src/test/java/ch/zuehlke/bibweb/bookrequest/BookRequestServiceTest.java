package ch.zuehlke.bibweb.bookrequest;

import ch.zuehlke.bibweb.config.WebSecurityTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@Import(WebSecurityTestConfig.class)
public class BookRequestServiceTest {

    @MockBean
    private BookRequestRepository bookRequestRepository;

    @TestConfiguration
    static class BookRequestServiceTestContextConfiguration {

        @Bean
        public BookRequestService bookRequestService() {
            return new BookRequestService();
        }
    }

    @Autowired
    private BookRequestService bookRequestService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllBookRequest_asADMIN() {
        given(this.bookRequestRepository.findAll()).willReturn(Arrays.asList(
                new BookRequest((long) 1, "1351381379", "user1", BookRequestState.NEW),
                new BookRequest((long) 2, "1316503155", "user2", BookRequestState.NEW)
        ));

        List<BookRequest> bookRequests = this.bookRequestService.getBookRequests();

        assertEquals(2, bookRequests.size());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void getAllBookRequest_asUser() {
        given(this.bookRequestRepository.findAllByUser(anyString())).willReturn(Collections.singletonList(
                new BookRequest((long) 1, "156415416+", "user", BookRequestState.NEW)
        ));

        List<BookRequest> bookRequests = this.bookRequestService.getBookRequests();

        assertEquals(1, bookRequests.size());
        assertEquals("user", bookRequests.get(0).getUser());
    }

    @Test
    @WithMockUser(username = "otheruser", roles = "USER")
    public void getAllBookRequest_asUser_withoutRequests() {
        given(this.bookRequestRepository.findAllByUser(anyString())).willReturn(Collections.emptyList());

        List<BookRequest> bookRequests = this.bookRequestService.getBookRequests();

        assertEquals(0, bookRequests.size());
    }

    @Test
    public void getBookRequestDetails() {
        BookRequest bookRequest = new BookRequest((long) 99, "2131513135", "user", BookRequestState.NEW);

        given(this.bookRequestRepository.findById((long) 99)).willReturn(Optional.of(bookRequest));

        BookRequest retrievedBookRequest = this.bookRequestService.getBookRequestDetails(99);

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

        BookRequest bookRequestToSave = new BookRequest("1315531383518");
        BookRequest savedBookRequest = this.bookRequestService.createBookRequest(bookRequestToSave);

        assertEquals("user", savedBookRequest.getUser());
        assertEquals(BookRequestState.NEW, savedBookRequest.getState());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateBookRequest_asADMIN() {
        final BookRequest bookRequest = new BookRequest((long) 1, "215155151", "user", BookRequestState.NEW);

        given(this.bookRequestRepository.existsById(anyLong())).willReturn(true);
        given(this.bookRequestRepository.save(any(BookRequest.class))).willReturn(bookRequest);

        final BookRequest updatedBookRequest = this.bookRequestService.updateBookRequest(bookRequest);

        assertEquals(bookRequest.getId(), updatedBookRequest.getId());
    }

    @Test(expected = BookRequestNotFoundException.class)
    @WithMockUser(roles = "ADMIN")
    public void updateBookRequest_asADMIN_nonExistingBookRequest() {
        final BookRequest bookRequest = new BookRequest((long) 1, "215155151", "user", BookRequestState.NEW);

        given(this.bookRequestRepository.existsById(anyLong())).willReturn(false);

        this.bookRequestService.updateBookRequest(bookRequest);
    }

    @Test(expected = AccessDeniedException.class)
    @WithMockUser(roles = "USER")
    public void updateBookRequest_asUSER_AccessDenied() {
        final BookRequest bookRequest = new BookRequest((long) 1, "215155151", "user", BookRequestState.NEW);

        this.bookRequestService.updateBookRequest(bookRequest);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void acceptBookRequest() {
        final BookRequest bookRequest = new BookRequest((long) 1, "215155151", "user", BookRequestState.NEW);

        given(this.bookRequestRepository.existsById((long) 1)).willReturn(true);
        given(this.bookRequestRepository.saveAndFlush(any(BookRequest.class)))
                .willReturn(new BookRequest((long) 1, "215155151", "user", BookRequestState.ACCEPTED));

        BookRequest acceptedBookRequest = this.bookRequestService.acceptBookRequest(bookRequest);

        assertEquals(BookRequestState.ACCEPTED, acceptedBookRequest.getState());

    }

    @Test(expected = BookRequestNotFoundException.class)
    @WithMockUser(roles = "ADMIN")
    public void acceptBookRequest_notFound() {
        final BookRequest bookRequest = new BookRequest((long) 1, "215155151", "user", BookRequestState.NEW);

        given(this.bookRequestRepository.existsById((long) 1)).willReturn(false);

        this.bookRequestService.acceptBookRequest(bookRequest);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void declineBookRequest() {
        final BookRequest bookRequest = new BookRequest((long) 1, "215155151", "user", BookRequestState.NEW);

        given(this.bookRequestRepository.existsById((long) 1)).willReturn(true);
        given(this.bookRequestRepository.saveAndFlush(any(BookRequest.class)))
                .willReturn(new BookRequest((long) 1, "215155151", "user", BookRequestState.DECLINED));

        BookRequest acceptedBookRequest = this.bookRequestService.declineBookRequest(bookRequest);

        assertEquals(BookRequestState.DECLINED, acceptedBookRequest.getState());

    }

    @Test(expected = BookRequestNotFoundException.class)
    @WithMockUser(roles = "ADMIN")
    public void declineBookRequest_notFound() {
        final BookRequest bookRequest = new BookRequest((long) 1, "215155151", "user", BookRequestState.NEW);

        given(this.bookRequestRepository.existsById((long) 1)).willReturn(false);

        this.bookRequestService.declineBookRequest(bookRequest);
    }
}