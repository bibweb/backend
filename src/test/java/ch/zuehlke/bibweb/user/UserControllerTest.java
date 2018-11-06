package ch.zuehlke.bibweb.user;

import ch.zuehlke.bibweb.checkout.CheckoutService;
import ch.zuehlke.bibweb.config.WebSecurityTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UserController.class)
@ContextConfiguration(classes = {WebSecurityTestConfig.class, UserController.class})
@WebAppConfiguration
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private CheckoutService checkoutService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void whenRequestingAllUsers_thenReturnAll() throws Exception {
        given(userService.getUsers()).willReturn(new ArrayList<>());

        mvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void whenRequestingAllUsersWithUserRole_thenIsForbidden() throws Exception {
        given(userService.getUsers()).willReturn(new ArrayList<>());

        mvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void whenRequestingCurrentUser_thenReturnUser() throws Exception {
        BibwebUserDTO dto = new BibwebUserDTO();
        dto.setUsername("Etienne");

        given(userService.getCurrentUser()).willReturn(dto);

        mvc.perform(get("/users/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("Etienne")));
    }
}
