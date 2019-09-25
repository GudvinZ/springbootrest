package test.springbootrest.controller;

import com.sun.deploy.net.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import test.springbootrest.exception.IsAlreadyExistException;
import test.springbootrest.exception.NotFoundException;
import test.springbootrest.model.User;
import test.springbootrest.service.RoleService;
import test.springbootrest.service.UserService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@RestController
@RequestMapping("/rest")
//@PreAuthorize("hasAnyAuthority('admin')")
public class RestCrudController {
    private final UserService userService;
    private final RoleService roleService;

    @ExceptionHandler(NotFoundException.class)
    public void handleNotFoundException(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(IsAlreadyExistException.class)
    public void handleIsAlreadyExistException(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    public RestCrudController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostMapping("/add")
    public User addUser(@RequestBody User user) {
        if (userService.addUser(user))
            return user;
        else
            throw new IsAlreadyExistException("this user is already exist");

//        userService.addUser(user);
//        return userService.getUserByLogin(user.getLogin()).orElseThrow(IsAlreadyExistException::new);
    }

    @GetMapping("/get/{id}")
    public User getUniqueUser(@PathVariable Long id) {
        return userService.getUserById(id).orElseThrow(NotFoundException::new);
    }


    @GetMapping("/getAll")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/update")
    public User updateUser(@RequestBody User user) {
        if (userService.updateUser(user))
            return userService.getUserById(user.getId()).orElseThrow(NotFoundException::new);
        else
            throw new IsAlreadyExistException("this user is already exist");
    }

    @DeleteMapping("/delete")
    public void deleteUser(@RequestParam Long id) {
        userService.deleteUserById(id);
    }

    @GetMapping("/testUser")
    public User getTestUser() {
        return new User("login", "password", "name", Collections.singletonList(roleService.getRoleByName("user")));
    }
}
