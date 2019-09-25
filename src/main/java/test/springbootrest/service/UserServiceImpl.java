package test.springbootrest.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.springbootrest.exception.NotFoundException;
import test.springbootrest.model.Role;
import test.springbootrest.model.User;
import test.springbootrest.repository.UserRepository;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

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

    private boolean isValidParam(String param) {
        return param != null && !param.isEmpty();
    }

    @Override
    public boolean addUser(User user) {
        if (isValidParam(user.getLogin()) && !getUserByLogin(user.getLogin()).isPresent()
                && isValidParam(user.getPassword()) && isValidParam(user.getName())) {
            if (user.getRoles() == null || user.getRoles().size() < 1) {
                user.setRoles(Collections.singletonList(roleService.getRoleByName("user")));
            }
            else {
                user.setRoles(user.getRoles().stream().map(role -> roleService.getRoleByName(role.getRoleName())).collect(Collectors.toList()));
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean validateUser(String login, String password) {
        if (isValidParam(login) && isValidParam(password)) {
            return getUserByLogin(login).map(x -> x.getPassword().equals(password)).orElse(false);
        } else
            throw new InvalidParameterException();
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
        User old = getUserById(user.getId()).orElseThrow(NotFoundException::new);

        if (old.getLogin().equals(user.getLogin()) || !getUserByLogin(user.getLogin()).isPresent()) {
            boolean isValidLogin = isValidParam(user.getLogin()) && !old.getLogin().equals(user.getLogin());
            boolean isValidPassword = isValidParam(user.getPassword()) && !old.getPassword().equals(user.getPassword());
            boolean isValidName = isValidParam(user.getName()) && !old.getName().equals(user.getName());

            if (isValidLogin || isValidPassword || isValidName) {
                if (isValidLogin)
                    old.setLogin(user.getLogin());
                if (isValidPassword)
                    old.setPassword(passwordEncoder.encode(user.getPassword()));
                if (isValidName)
                    old.setName(user.getName());
                return true;
            }
        }
        return false;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByLogin(String login) {
        return userRepository.findByLogin(login);
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
        User user = getUserByLogin(login).orElseThrow(NotFoundException::new);

        return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(),
                user.getRoles().stream().map(x -> new SimpleGrantedAuthority(x.getRoleName())).collect(Collectors.toList()));
    }
}
