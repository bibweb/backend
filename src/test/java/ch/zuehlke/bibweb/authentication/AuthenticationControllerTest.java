package ch.zuehlke.bibweb.authentication;

import ch.zuehlke.bibweb.book.BookController;
import ch.zuehlke.bibweb.book.BookService;
import ch.zuehlke.bibweb.checkout.CheckoutService;
import ch.zuehlke.bibweb.config.WebSecurityTestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest({AuthenticationController.class, BookController.class})
@ContextConfiguration(classes = {WebSecurityTestConfig.class, AuthenticationController.class, BookController.class})
@WebAppConfiguration
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenProvider jwtTokenUtil;

    @MockBean
    private CheckoutService checkoutService;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void whenNoTokenSubmitted_thenStatusIsNotOk() throws Exception {
        mvc.perform(get("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF8"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void whenRequestingToken_thenGetTokenBack() throws Exception {
        given(jwtTokenUtil.generateToken(null)).willReturn("54321TOKEN");

        LoginUser user = new LoginUser();
        user.setUsername("Etienne");
        user.setPassword("12345");

        mvc.perform(post("/token/generate-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user))
                .characterEncoding("UTF8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("54321TOKEN"));
    }

}
