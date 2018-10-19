package ch.zuehlke.bibweb.bookrequest;

import ch.zuehlke.bibweb.config.WebSecurityTestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
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
@Import(WebSecurityTestConfig.class)
public class BookRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookRequestService bookRequestService;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void getAllBookRequests() throws Exception {
        given(this.bookRequestService.getBookRequests()).willReturn(
                Collections.singletonList(new BookRequest("123", "user", BookRequestState.NEW))
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
        final BookRequest bookRequest = new BookRequest((long) 1, "123", "user1");

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
        final BookRequest bookRequest = new BookRequest((long) 1, "123", "user1");

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
        final BookRequest bookRequest = new BookRequest("123", "user");

        given(this.bookRequestService.createBookRequest(any(BookRequest.class))).willReturn(bookRequest);

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
        final BookRequest bookRequest = new BookRequest((long) 1, "3932420942092", "testuser", BookRequestState.ACCEPTED);

        given(this.bookRequestService.updateBookRequest(any(BookRequest.class))).willReturn(bookRequest);

        this.mvc.perform(put("/bookrequest/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(bookRequest))
                .characterEncoding("UTF8"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user")
    public void updateBookRequest_Forbidden() throws Exception {
        final BookRequest bookRequest = new BookRequest("3932420942092", "user", BookRequestState.ACCEPTED);

        this.mvc.perform(put("/bookrequest/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(bookRequest))
                .characterEncoding("UTF8"))
                .andExpect(status().isForbidden());
    }
}