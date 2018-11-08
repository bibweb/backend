package ch.zuehlke.bibweb.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public BibwebUser signUp(SignUpUserDTO newUser) {
        if (this.userRepository.findByUsername(newUser.getUsername()) == null) {
            BibwebUser userToRegister = new BibwebUser();
            userToRegister.setUsername(newUser.getUsername());
            userToRegister.setPassword(passwordEncoder.encode(newUser.getPassword()));

            Role userRole = this.roleRepository.findByRolename("USER");
            userToRegister.addRole(userRole);

            return this.userRepository.saveAndFlush(userToRegister);
        } else {
            throw new UserAlreadyExistsException(String.format("Username %s is already taken.", newUser.getUsername()));
        }

    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<BibwebUserDTO> getUsers() {
        return this.userRepository.findAll().stream().map(this::mapBibwebUserEntityToDto).collect(Collectors.toList());
    }

    public BibwebUserDTO getCurrentUser() {
        return mapBibwebUserEntityToDto(UserSecurityUtil.getCurrentUser());
    }

    private BibwebUserDTO mapBibwebUserEntityToDto(BibwebUser user) {
        BibwebUserDTO dto = new BibwebUserDTO();
        BeanUtils.copyProperties(user, dto, "password");
        return dto;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        BibwebUser user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return user;
    }

    private Set<SimpleGrantedAuthority> getAuthority(BibwebUser user) {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRolename()))
                .collect(Collectors.toSet());
    }
}
