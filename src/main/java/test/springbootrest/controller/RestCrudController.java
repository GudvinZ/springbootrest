package test.springbootrest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.springbootrest.model.User;
import test.springbootrest.service.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest")
//@PreAuthorize("hasAnyAuthority('admin')")
public class RestCrudController {
    private final UserService userService;

    public RestCrudController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getUniqueUser(@PathVariable Long id) {
        return new User();
    }

    @GetMapping
    public List<User> getUniqueUser() {
        return new ArrayList<>();
    }
}
