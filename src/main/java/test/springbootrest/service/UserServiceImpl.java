package test.springbootrest.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.springbootrest.model.Role;
import test.springbootrest.model.User;
import test.springbootrest.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean addUser(User user) {
        if (getUserByLogin(user.getLogin()) != null)
            return false;
        user.setRoles(user.getRoles().stream().map(role -> roleService.getRoleByName(role.getRoleName())).collect(Collectors.toList()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean validateUser(String login, String password) {
        User user = getUserByLogin(login);
        return user != null && user.getPassword().equals(password);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

//    @Override
//    public void deleteAllUsers() {
//
//    }

    @Override
    public boolean updateUser(User user) {
        User old = getUserById(user.getId());
        if (old.getLogin().equals(user.getLogin())) {
            if (!old.getPassword().equals(user.getPassword()) && !user.getPassword().isEmpty())
                old.setPassword(passwordEncoder.encode(user.getPassword()));
            old.setName(user.getName());
            return true;
        } else if (getUserByLogin(user.getLogin()) == null) {
            old.setLogin(user.getLogin());
            old.setPassword(passwordEncoder.encode(user.getPassword()));
            old.setName(user.getName());
            return true;
        } else
            return false;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getUserByLogin(String login) {
        return userRepository.findByLogin(login).orElse(null);
    }

    @Override
    public List<User> getUsersByRoles(String... roles) {
        List<Role> list = new ArrayList<>();
        for (String role : roles) {
            list.add(roleService.getRoleByName(role));
        }
        return userRepository.findAllByRolesContains(list);
    }

    @Override
    public List<User> getUsersByName(String name) {
        return userRepository.findAllByName(name);
    }

    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = getUserByLogin(login);
        if (user == null) {
            throw new UsernameNotFoundException("user is not exist");
        }

        return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(),
                user.getRoles().stream().map(x -> new SimpleGrantedAuthority(x.getRoleName())).collect(Collectors.toList()));
    }
}
