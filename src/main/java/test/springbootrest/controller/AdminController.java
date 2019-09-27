package test.springbootrest.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import test.springbootrest.exception.IsAlreadyExistException;
import test.springbootrest.model.User;
import test.springbootrest.service.UserService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/admin")
@PreAuthorize("hasAnyAuthority('admin')")
public class AdminController {
    private UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void addPageAttr(ModelMap model) {
        model.addAttribute("adminPage", true);
    }

    private void addAllUsersToModel(ModelMap model) {
        model.addAttribute("users",
                userService.getAllUsers().stream()
                        .sorted(Comparator.comparing(User::getId))
                        .collect(Collectors.toList()));
    }


    @GetMapping
    public String adminPage(ModelMap model) {
        addAllUsersToModel(model);
        return "admin";
    }

    @PostMapping(value = "/add")
    public String addUser(@RequestParam(defaultValue = "user") String[] rolesParam, @ModelAttribute User user, ModelMap model) {
        user.setRoles(rolesParam);

        try {
            userService.addUser(user);
        } catch (IsAlreadyExistException e) {
            model.addAttribute("isAlreadyExist", true);
        }

        addAllUsersToModel(model);
        return "admin";
    }

    @PostMapping(value = "/delete")
    public String deleteUser(@RequestParam Long id, ModelMap model) {
        userService.deleteUserById(id);

        addAllUsersToModel(model);
        return "admin";
    }

    @GetMapping("/update/{id}")
    public String updatePage(@PathVariable Long id, ModelMap model) {
        model.addAttribute("id", id);

        addAllUsersToModel(model);
        return "admin";
    }

    @PostMapping(value = "/update")
    public String updateUser(@ModelAttribute User user, ModelMap model) {
        try {
            userService.updateUser(user);
        } catch (IsAlreadyExistException e) {
            model.addAttribute("login", user.getLogin());
            model.addAttribute("id", user.getId());
            model.addAttribute("isAlreadyExist2", true);
        }

        addAllUsersToModel(model);
        return "admin";
    }
}
