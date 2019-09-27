package test.springbootrest.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.springbootrest.exception.IsAlreadyExistException;
import test.springbootrest.exception.NotFoundException;
import test.springbootrest.model.User;
import test.springbootrest.repository.UserRepository;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private boolean isValidString(String sParam) {
        return sParam != null && !sParam.isEmpty();
    }

    @Override
    public void addUser(User user) {
        if (!isValidString(user.getLogin()) || !isValidString(user.getPassword()) || !isValidString(user.getName()))
            throw new InvalidParameterException();

        if (userRepository.findByLogin(user.getLogin()).isPresent())
            throw new IsAlreadyExistException();

        User newUser = new User(user.getLogin(), user.getPassword(), user.getName());

        if (user.getRoles() == null || user.getRoles().size() < 1) {
            newUser.setRoles(Collections.singletonList(roleService.getRoleByName("user")));
        } else {
            newUser.setRoles(user.getRoles().stream().map(role -> roleService.getRoleByName(role.getRoleName())).collect(Collectors.toList()));
        }

        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(newUser);
    }

    @Override
    public boolean validateUser(String login, String password) {
        if (!isValidString(login) && !isValidString(password))
            throw new InvalidParameterException();

        return userRepository.findByLogin(login).map(x -> x.getPassword().equals(password)).orElse(false);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void updateUser(User user) {
        User old = getUserById(user.getId());

        boolean isValidLogin = isValidString(user.getLogin()) && !old.getLogin().equals(user.getLogin());
        boolean isValidPassword = isValidString(user.getPassword()) && !old.getPassword().equals(user.getPassword());
        boolean isValidName = isValidString(user.getName()) && !old.getName().equals(user.getName());

        if (isValidLogin) {
            if (userRepository.findByLogin(user.getLogin()).isPresent())
                throw new IsAlreadyExistException();
            old.setLogin(user.getLogin());
        }
        if (isValidPassword)
            old.setPassword(passwordEncoder.encode(user.getPassword()));
        if (isValidName)
            old.setName(user.getName());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public User getUserByLogin(String login) {
        return userRepository.findByLogin(login).orElseThrow(NotFoundException::new);
    }

    @Override
    public List<User> getUsersByRoles(String... roles) {
        return userRepository.findAllByRolesContains(
                Stream.of(roles)
                        .map(roleService::getRoleByName)
                        .collect(Collectors.toList()));
    }

    @Override
    public List<User> getUsersByName(String name) {
        return userRepository.findAllByName(name);
    }

    public UserDetails loadUserByUsername(String login) {
        User user = getUserByLogin(login);

        return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toList()));
    }
}
