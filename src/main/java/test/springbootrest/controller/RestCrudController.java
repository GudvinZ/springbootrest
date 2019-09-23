package test.springbootrest.controller;

import org.springframework.web.bind.annotation.*;
import test.springbootrest.exception.IsAlreadyExistException;
import test.springbootrest.exception.NotFoundException;
import test.springbootrest.model.User;
import test.springbootrest.service.RoleService;
import test.springbootrest.service.UserService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/rest")
//@PreAuthorize("hasAnyAuthority('admin')")
public class RestCrudController {
    private final UserService userService;
    private final RoleService roleService;

    public RestCrudController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostMapping("/add")
    public User addUser(@RequestBody User user) {
        if (userService.addUser(user))
            return userService.getUserByLogin(user.getLogin());
        else
            throw new IsAlreadyExistException("this user is already exist");
    }

    @GetMapping("/get/{id}")
    public User getUniqueUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null)
            throw new NotFoundException("user not found");
        else
            return user;
    }


    @GetMapping("/getAll")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/update")
    public User updateUser(@RequestBody User user) {
        if (userService.updateUser(user))
            return userService.getUserById(user.getId());
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
