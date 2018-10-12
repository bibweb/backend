package ch.zuehlke.bibweb.book;

import ch.zuehlke.bibweb.BibwebApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = BibwebApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class BookControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BookRepository bookRepository;

    private final Long bookId = 1234L;
    private final String bookTitle = "ABC";

    private Book createBook() {
        Book book = new Book();
        book.setId(this.bookId);
        book.setTitle(this.bookTitle);

        bookRepository.save(book);

        return book;
    }

    @Test
    @Ignore("not yet available on build server")
    public void givenBook_whenGetBookById_ThenStatus200() throws Exception {
        createBook();

        mvc.perform(get("/book/" + this.bookId)
            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is("ABC")));
    }

    @Test
    @Ignore("not yet available on build server")
    public void givenUpdatedBook_returnCorrectBook() throws Exception {
        Book book = createBook();

        mvc.perform(get("/book/" + this.bookId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is(this.bookTitle)));

        book.setTitle("DEF");

        mvc.perform(put("/book/" + this.bookId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(book)))
            .andExpect(status().isOk());

        mvc.perform(get("/book/" + this.bookId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.title", is("DEF")));
    }

}
