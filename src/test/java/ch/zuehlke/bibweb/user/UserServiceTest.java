package ch.zuehlke.bibweb.user;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
public class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @TestConfiguration
    static class UserServiceServiceTestContextConfiguration {

        @Bean
        public BCryptPasswordEncoder bCryptPasswordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public UserService userService() {
            return new UserService();
        }
    }

    @Autowired
    private UserService userService;

    @Test
    public void signUp() {
        SignUpUserDTO signUpUserDTO = setUpUserDTO();
        Role role = setUpRole();
        User user = setUpUser(role);

        given(this.userRepository.findByUsername(any(String.class))).willReturn(null);
        given(this.roleRepository.findByRolename("test")).willReturn(role);
        given(this.userRepository.saveAndFlush(any(User.class))).willReturn(user);

        User registeredUser = this.userService.signUp(signUpUserDTO);

        assertEquals("test", registeredUser.getUsername());
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void signUp_UserAlreadyExists() {
        given(this.userRepository.findByUsername(any(String.class))).willReturn(new User());

        SignUpUserDTO signUpUserDTO = setUpUserDTO();

        this.userService.signUp(signUpUserDTO);
    }

    private Role setUpRole() {
        Role role = new Role();
        role.setRolename("TEST");
        role.setId((long) 1);
        return role;
    }

    private SignUpUserDTO setUpUserDTO() {
        SignUpUserDTO signUpUserDTO = new SignUpUserDTO();
        signUpUserDTO.setUsername("test");
        signUpUserDTO.setPassword("test");
        return signUpUserDTO;
    }

    private User setUpUser(Role role) {
        User user = new User();
        user.setUsername("test");
        user.setPassword("sdfasfsdafsadf");
        HashSet<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        return user;
    }
}