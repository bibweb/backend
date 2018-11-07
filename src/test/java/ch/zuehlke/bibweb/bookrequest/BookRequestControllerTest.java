package ch.zuehlke.bibweb.bookrequest;

import ch.zuehlke.bibweb.bookrequest.exception.BookRequestNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

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
@WebMvcTest(BookRequestController.class)
@ActiveProfiles("unit-test")
public class BookRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookRequestService bookRequestService;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void getAllBookRequests() throws Exception {
        given(this.bookRequestService.getBookRequests()).willReturn(
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
    @WithMockUser(roles = "ADMIN")
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
    @WithMockUser(username = "user2", roles = "USER")
    public void getBookRequestDetails_Forbidden() throws Exception {
        final BookRequestDTO bookRequest = new BookRequestDTO((long) 1, "123", "user1");

        given(this.bookRequestService.getBookRequestDetails(anyLong())).willReturn(bookRequest);

        this.mvc.perform(get("/bookrequest/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void getBookRequestDetails_NotFound() throws Exception {
        given(this.bookRequestService.getBookRequestDetails(anyLong())).willThrow(new BookRequestNotFoundException());

        this.mvc.perform(get("/bookrequest/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
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
    @WithMockUser(username = "admin", roles = "ADMIN")
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
    @WithMockUser(username = "user")
    public void updateBookRequest_Forbidden() throws Exception {
        final BookRequestDTO bookRequest = new BookRequestDTO("3932420942092", "user", BookRequestState.ACCEPTED);

        this.mvc.perform(put("/bookrequest/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(bookRequest))
                .characterEncoding("UTF8"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
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
    @WithMockUser(roles = "ADMIN")
    public void acceptBookRequest_notFound() throws Exception {
        final BookRequestDTO bookRequest = new BookRequestDTO((long) 10, "131561161516", "user", BookRequestState.NEW);

        given(this.bookRequestService.acceptBookRequest(any(BookRequestDTO.class)))
                .willThrow(new BookRequestNotFoundException());

        this.mvc.perform(put("/bookrequest/10/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(bookRequest))
                .characterEncoding("UTF8"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void acceptBookRequest_wrongState() throws Exception {
        final BookRequestDTO bookRequest = new BookRequestDTO((long) 10, "131561161516", "user", BookRequestState.NEW);

        given(this.bookRequestService.acceptBookRequest(any(BookRequestDTO.class)))
                .willThrow(new IllegalArgumentException());

        this.mvc.perform(put("/bookrequest/10/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(bookRequest))
                .characterEncoding("UTF8"))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
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
}