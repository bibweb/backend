package ch.zuehlke.bibweb.authentication;

import ch.zuehlke.bibweb.user.SignUpUserDTO;
import ch.zuehlke.bibweb.user.User;
import ch.zuehlke.bibweb.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignUpController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public User signUp(@RequestBody SignUpUserDTO newUser) {
        return this.userService.signUp(newUser);
    }
}
