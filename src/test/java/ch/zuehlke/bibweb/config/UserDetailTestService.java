package ch.zuehlke.bibweb.config;

import ch.zuehlke.bibweb.user.BibwebUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;

@Service
public class UserDetailTestService implements UserDetailsService {

    public static final long USER_ETIENNE_ID = 1L;
    public static final long USER_STEFAN_ID = 2L;

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
    }};

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userList.get(username);
    }

}
