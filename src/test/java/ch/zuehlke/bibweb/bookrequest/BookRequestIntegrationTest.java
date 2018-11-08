package ch.zuehlke.bibweb.bookrequest;

import ch.zuehlke.bibweb.bookrequest.data.BookRequest;
import ch.zuehlke.bibweb.bookrequest.data.BookRequestRepository;
import ch.zuehlke.bibweb.bookrequest.data.BookRequestState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAutoConfiguration
@AutoConfigureMockMvc
public class BookRequestIntegrationTest {
    @Autowired
    private BookRequestRepository bookRequestRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void getBookRequests() throws Exception {
        List<BookRequest> bookRequests = Arrays.asList(
                new BookRequest("123156158", "user1", BookRequestState.NEW),
                new BookRequest("123156158", "user2", BookRequestState.ACCEPTED),
                new BookRequest("123156158", "user4", BookRequestState.DECLINED)
        );

        this.bookRequestRepository.saveAll(bookRequests);

        this.mockMvc.perform(get("/bookrequest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4))); // 3 + 1 from sql migrations
    }
}
