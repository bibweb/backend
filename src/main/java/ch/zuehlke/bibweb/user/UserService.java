package ch.zuehlke.bibweb.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public BibwebUser signUp(SignUpUserDTO newUser) {
        if (this.userRepository.findByUsername(newUser.getUsername()) == null) {
            BibwebUser userToRegister = new BibwebUser();

            userToRegister.setUsername(newUser.getUsername());
            userToRegister.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));

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
}
