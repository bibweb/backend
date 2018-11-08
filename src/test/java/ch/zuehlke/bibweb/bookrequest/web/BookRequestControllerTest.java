package ch.zuehlke.bibweb.bookrequest.web;

import ch.zuehlke.bibweb.bookrequest.business.BookRequestDTO;
import ch.zuehlke.bibweb.bookrequest.business.BookRequestService;
import ch.zuehlke.bibweb.bookrequest.data.BookRequest;
import ch.zuehlke.bibweb.bookrequest.data.BookRequestState;
import ch.zuehlke.bibweb.bookrequest.exception.BookRequestNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = BookRequestController.class, secure = false)
public class BookRequestControllerTest {
    @MockBean
    private BookRequestService bookRequestService;

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllBookRequests_asADMIN() throws Exception {
        given(this.bookRequestService.getAllBookRequests()).willReturn(Arrays.asList(
                new BookRequestDTO("45453535455", "user1", BookRequestState.NEW),
                new BookRequestDTO("32153211535", "user2", BookRequestState.NEW)
        ));

        this.mvc.perform(get("/bookrequest")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void getAllBookRequests_asUSER() throws Exception {
        given(this.bookRequestService.getBookRequestsForUser("user")).willReturn(
                Collections.singletonList(new BookRequestDTO("123", "user", BookRequestState.NEW))
        );

        this.mvc.perform(get("/bookrequest")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].isbn", is("123")))
                .andExpect(jsonPath("$[0].user", is("user")))
                .andExpect(jsonPath("$[0].state", is(BookRequestState.NEW.ordinal())));
    }

    @Test
    public void getBookRequestDetails() throws Exception {
        final BookRequestDTO bookRequest = new BookRequestDTO((long) 1, "123", "user1");

        given(this.bookRequestService.getBookRequestDetails(anyLong())).willReturn(bookRequest);

        this.mvc.perform(get("/bookrequest/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn", is(bookRequest.getIsbn())))
                .andExpect(jsonPath("$.user", is(bookRequest.getUser())))
                .andExpect(jsonPath("$.state", is(bookRequest.getState().ordinal())));
    }

    @Test
    @WithMockUser
    public void getBookRequestDetails_NotFound() throws Exception {
        given(this.bookRequestService.getBookRequestDetails(anyLong())).willThrow(new BookRequestNotFoundException());

        this.mvc.perform(get("/bookrequest/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createBookRequest() throws Exception {
        final BookRequestDTO bookRequest = new BookRequestDTO("123", "user");

        given(this.bookRequestService.createBookRequest(any(BookRequestDTO.class))).willReturn(bookRequest);

        this.mvc.perform(post("/bookrequest")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new BookRequest("123")))
                .characterEncoding("UTF8"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isbn", is(bookRequest.getIsbn())))
                .andExpect(jsonPath("$.user", is(bookRequest.getUser())))
                .andExpect(jsonPath("$.state", is(bookRequest.getState().ordinal())));
    }

    @Test
    public void updateBookRequest() throws Exception {
        final BookRequestDTO bookRequest = new BookRequestDTO((long) 1, "3932420942092", "testuser", BookRequestState.ACCEPTED);

        given(this.bookRequestService.updateBookRequest(any(BookRequestDTO.class))).willReturn(bookRequest);

        this.mvc.perform(put("/bookrequest/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(bookRequest))
                .characterEncoding("UTF8"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void acceptBookRequest() throws Exception {
        final BookRequestDTO bookRequest = new BookRequestDTO((long) 10, "131561161516", "user", BookRequestState.NEW);

        given(this.bookRequestService.acceptBookRequest(any(BookRequestDTO.class))).willReturn(
                new BookRequestDTO((long) 10, "131561161516", "user", BookRequestState.ACCEPTED)
        );

        this.mvc.perform(put("/bookrequest/10/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(bookRequest))
                .characterEncoding("UTF8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state", is(BookRequestState.ACCEPTED.ordinal())));
    }

    @Test
    public void acceptBookRequest_IdBodyMismatch() throws Exception {
        final BookRequestDTO bookRequest = new BookRequestDTO((long) 10, "131561161516", "user", BookRequestState.NEW);

        given(this.bookRequestService.acceptBookRequest(any(BookRequestDTO.class)))
                .willThrow(new IllegalArgumentException());

        this.mvc.perform(put("/bookrequest/11/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(bookRequest))
                .characterEncoding("UTF8"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void declineBookRequest() throws Exception {
        final BookRequestDTO bookRequest = new BookRequestDTO((long) 10, "131561161516", "user", BookRequestState.NEW);

        given(this.bookRequestService.declineBookRequest(any(BookRequestDTO.class))).willReturn(
                new BookRequestDTO((long) 10, "131561161516", "user", BookRequestState.DECLINED)
        );

        this.mvc.perform(put("/bookrequest/10/decline")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(bookRequest))
                .characterEncoding("UTF8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state", is(BookRequestState.DECLINED.ordinal())));
    }

    @Test
    public void declineBookRequest_IdBodyMismatch() throws Exception {
        final BookRequestDTO bookRequest = new BookRequestDTO((long) 10, "131561161516", "user", BookRequestState.NEW);

        given(this.bookRequestService.declineBookRequest(any(BookRequestDTO.class))).willReturn(
                new BookRequestDTO((long) 10, "131561161516", "user", BookRequestState.DECLINED)
        );

        this.mvc.perform(put("/bookrequest/11/decline")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(bookRequest))
                .characterEncoding("UTF8"))
                .andExpect(status().isBadRequest());
    }
}