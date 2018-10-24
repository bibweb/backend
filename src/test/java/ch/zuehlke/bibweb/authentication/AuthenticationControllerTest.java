package ch.zuehlke.bibweb.authentication;

import ch.zuehlke.bibweb.book.Book;
import ch.zuehlke.bibweb.book.BookController;
import ch.zuehlke.bibweb.book.BookDTO;
import ch.zuehlke.bibweb.book.BookService;
import ch.zuehlke.bibweb.config.TokenProvider;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest({BookController.class, AuthenticationController.class})
@ContextConfiguration
@WebAppConfiguration
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenProvider jwtTokenUtil;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void whenNoTokenSubmitted_thenStatusIsUnauthorized() throws Exception {
        BookDTO book0 = new BookDTO();
        book0.setId(0L);
        book0.setTitle("Buch 0");

        BookDTO book1 = new BookDTO();
        book1.setId(1L);
        book1.setTitle("Buch 1");

        given(bookService.getAllBooks()).willReturn(new ArrayList<BookDTO>() {{
            add(book0);
            add(book1);
        }});

        mvc.perform(get("/book")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Ignore("not yet working")
    public void whenTokenSubmitted_thenStatusIsOk() throws Exception {
        BookDTO book0 = new BookDTO();
        book0.setId(0L);
        book0.setTitle("Buch 0");

        BookDTO book1 = new BookDTO();
        book1.setId(1L);
        book1.setTitle("Buch 1");

        given(bookService.getAllBooks()).willReturn(new ArrayList<BookDTO>() {{
            add(book0);
            add(book1);
        }});

        mvc.perform(post("/token/generate-token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"admin\", \"password\": \"password\"")
                .characterEncoding("UTF8"))
                .andExpect(status().isOk());


        mvc.perform(get("/book/1").with(user("etienne").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
