package ch.zuehlke.bibweb.bookrequest;

import ch.zuehlke.bibweb.book.BookNotFoundExcpetion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
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
    public void getBookRequestDetails() {
        BookRequest bookRequest = new BookRequest((long) 99, "2131513135", "user", BookRequestState.NEW);

        given(this.bookRequestRepository.findById((long) 99)).willReturn(Optional.of(bookRequest));

        BookRequest retrievedBookRequest = this.bookRequestService.getBookRequestDetails(99);

        assertEquals(bookRequest.getId(), retrievedBookRequest.getId());
        assertEquals(bookRequest.getIsbn(), retrievedBookRequest.getIsbn());
        assertEquals(bookRequest.getUser(), retrievedBookRequest.getUser());
        assertEquals(bookRequest.getState(), retrievedBookRequest.getState());


    }

    @Test(expected = BookNotFoundExcpetion.class)
    public void getBookRequestDetails_NotFound() {
        given(bookRequestRepository.findById(anyLong())).willThrow(new BookNotFoundExcpetion());

        this.bookRequestService.getBookRequestDetails(99);
    }
}