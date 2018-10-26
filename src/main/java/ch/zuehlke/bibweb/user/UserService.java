package ch.zuehlke.bibweb.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User signUp(SignUpUserDTO newUser) {
        if (this.userRepository.findByUsername(newUser.getUsername()) == null) {
            User userToRegister = new User();

            userToRegister.setUsername(newUser.getUsername());
            userToRegister.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));

            Role userRole = this.roleRepository.findByRolename("USER");

            userToRegister.addRole(userRole);

            return this.userRepository.saveAndFlush(userToRegister);
        } else {
            throw new UserAlreadyExistsException(String.format("Username %s is already taken.", newUser.getUsername()));
        }

    }
}
