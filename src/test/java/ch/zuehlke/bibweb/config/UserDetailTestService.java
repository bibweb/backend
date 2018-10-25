package ch.zuehlke.bibweb.config;

import ch.zuehlke.bibweb.user.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;

@Service
public class UserDetailTestService implements UserDetailsService {

    private HashMap<String, User> userList = new HashMap<String, User>() {{
        put("Etienne", new User() {{
            setId(1L);
            setUsername("Etienne");
            setRoles(new HashSet<>());
        }});
        put("Stefan", new User() {{
            setId(2L);
            setUsername("Stefan");
            setRoles(new HashSet<>());
        }});
    }};

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userList.get(username);
    }

}
