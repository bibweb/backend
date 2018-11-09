package ch.zuehlke.bibweb.config;

import ch.zuehlke.bibweb.user.BibwebUser;
import ch.zuehlke.bibweb.user.Role;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

@Service
public class UserDetailTestService implements UserDetailsService {

    public static final long USER_ETIENNE_ID = 1L;
    public static final long USER_STEFAN_ID = 2L;
    public static final long USER_ADMIN_ID = 3L;

    private static Role adminRole = new Role();

    private HashMap<String, BibwebUser> userList = new HashMap<String, BibwebUser>() {{
        put("Etienne", new BibwebUser() {{
            setId(USER_ETIENNE_ID);
            setUsername("Etienne");
            setRoles(new HashSet<>());
        }});
        put("Stefan", new BibwebUser() {{
            setId(USER_STEFAN_ID);
            setUsername("Stefan");
            setRoles(new HashSet<>());
        }});
        put("admin", new BibwebUser() {{
            setId(USER_ADMIN_ID);
            setUsername("admin");
            setRoles(new HashSet<>(Arrays.asList(new Role("ADMIN"))));
        }});
    }};

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userList.get(username);
    }

}
