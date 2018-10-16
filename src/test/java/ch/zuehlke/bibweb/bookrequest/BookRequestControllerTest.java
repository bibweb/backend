package ch.zuehlke.bibweb.bookrequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(BookRequestController.class)
public class BookRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookRequestService bookRequestService;

    private static final List<BookRequest> bookRequests = Arrays.asList(
            new BookRequest((long) 1, "123", "user1"),
            new BookRequest((long) 2, "456", "user2")
    );

    @Test
    public void getAllBookRequests() throws Exception {
        given(this.bookRequestService.getBookRequests()).willReturn(BookRequestControllerTest.bookRequests);

        this.mvc.perform(get("/bookrequest")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].isbn", is(BookRequestControllerTest.bookRequests.get(0).getIsbn())))
                .andExpect(jsonPath("$[0].user", is(BookRequestControllerTest.bookRequests.get(0).getUser())))
                .andExpect(jsonPath("$[0].state", is(BookRequestControllerTest.bookRequests.get(0).getState())))
                .andExpect(jsonPath("$[1].isbn", is(BookRequestControllerTest.bookRequests.get(1).getIsbn())))
                .andExpect(jsonPath("$[1].user", is(BookRequestControllerTest.bookRequests.get(1).getUser())))
                .andExpect(jsonPath("$[1].state", is(BookRequestControllerTest.bookRequests.get(1).getState())));

    }

    @Test
    public void getBookRequestDetails() throws Exception {
        final BookRequest bookRequest = new BookRequest((long) 1, "123", "user1");

        given(this.bookRequestService.getBookRequestDetails(anyLong())).willReturn(bookRequest);

        this.mvc.perform(get("/bookrequest/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn", is(bookRequest.getIsbn())))
                .andExpect(jsonPath("$.user", is(bookRequest.getUser())))
                .andExpect(jsonPath("$.state", is(bookRequest.getState())));
    }

    @Test
    public void getBookRequestDetails_NotFound() throws Exception {
        given(this.bookRequestService.getBookRequestDetails(anyLong())).willThrow(new BookRequestNotFoundException());

        this.mvc.perform(get("/bookrequest/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createBookRequest() throws Exception {
        final BookRequest bookRequest = new BookRequest("123", "user1");

        given(this.bookRequestService.createBookRequest(any(BookRequest.class))).willReturn(bookRequest);

        this.mvc.perform(post("/bookrequest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(bookRequest))
                .characterEncoding("UTF8"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isbn", is(bookRequest.getIsbn())))
                .andExpect(jsonPath("$.user", is(bookRequest.getUser())))
                .andExpect(jsonPath("$.state", is(bookRequest.getState())));
    }

}