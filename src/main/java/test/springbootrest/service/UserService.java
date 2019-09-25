package test.springbootrest.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import test.springbootrest.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    boolean addUser(User user);

    boolean validateUser(String login, String password);

    void deleteUserById(Long id);

//    void deleteAllUsers();

    boolean updateUser(User user);

    List<User> getAllUsers();

    Optional<User> getUserById(Long param);

    Optional<User> getUserByLogin(String param);

    List<User> getUsersByRoles(String... params);

    List<User> getUsersByName(String param);
}
