package ch.zuehlke.bibweb.authentication;

import ch.zuehlke.bibweb.book.Book;
import ch.zuehlke.bibweb.book.BookController;
import ch.zuehlke.bibweb.book.BookService;
import ch.zuehlke.bibweb.config.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest({BookController.class, AuthenticationController.class})
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenProvider jwtTokenUtil;

    @Test
    public void whenNoTokenSubmitted_thenStatusIsUnauthorized() throws Exception {
        Book book0 = new Book();
        book0.setId(0L);
        book0.setTitle("Buch 0");

        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Buch 1");

        given(bookService.getAllBooks()).willReturn(new ArrayList<Book>() {{
            add(book0);
            add(book1);
        }});

        mvc.perform(get("/book")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Ignore("not yet working")
    public void whenTokenSubmitted_thenStatusIsAuthorized() throws Exception {
        LoginUser user = new LoginUser();
        user.setUsername("admin");
        user.setPassword("admin");

        given(jwtTokenUtil.generateToken(any(Authentication.class))).willReturn("GENERATED-TOKEN");
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(null);

        mvc.perform(post("/token/generate-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(user))
                .characterEncoding("UTF-8")
                .with(csrf()))
                .andExpect(status().isOk());
    }

}
