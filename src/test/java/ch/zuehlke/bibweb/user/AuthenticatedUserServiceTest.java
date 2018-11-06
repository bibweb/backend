package ch.zuehlke.bibweb.user;

import ch.zuehlke.bibweb.config.UserDetailTestService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class AuthenticatedUserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @TestConfiguration
    static class UserServiceTestContextConfiguration {
        @Bean
        public UserService userService() {
            return new UserService();
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return new UserDetailTestService();
        }
    }

    @MockBean
    private BCryptPasswordEncoder bCryptEncoder;

    @MockBean
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @Test
    @WithUserDetails(value = "Stefan", userDetailsServiceBeanName = "userDetailsService")
    public void whenRequestingCurrentUserAsStefan_thenReturnStefan() {
        BibwebUserDTO dto = userService.getCurrentUser();
        Assert.assertEquals("Stefan", dto.getUsername());
        Assert.assertEquals(2, (long) dto.getId());
    }

    @Test
    @WithUserDetails(value = "Etienne", userDetailsServiceBeanName = "userDetailsService")
    public void whenRequestingCurrentUserAsEtienne_thenReturnEtiennee() {
        BibwebUserDTO dto = userService.getCurrentUser();
        Assert.assertEquals("Etienne", dto.getUsername());
        Assert.assertEquals(1, (long) dto.getId());
    }
}
