package test.springbootrest.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import test.springbootrest.model.User;
import test.springbootrest.service.UserService;

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
        model.addAttribute("users", userService.getAllUsers());
    }


    @GetMapping
    public String adminPage(ModelMap model/*, Authentication authentication*/) {
//        if(!authentication.getAuthorities().contains(new SimpleGrantedAuthority("admin")))
//            return "redirect:/user";
        addAllUsersToModel(model);
        return "admin";
    }

    @PostMapping(value = "/add")
    public String addUser(@RequestParam(defaultValue = "user") String[] rolesParam, @ModelAttribute() User user, ModelMap model) {
        user.setRoles(rolesParam);
        model.addAttribute("isAlreadyExist", !userService.addUser(user));

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
    public String updatePage(@PathVariable String id, ModelMap model) {
        model.addAttribute("id", Long.parseLong(id));

        addAllUsersToModel(model);
        return "admin";
    }

    @PostMapping(value = "/update")

    public String updateUser(@ModelAttribute User user, ModelMap model) {
        addAllUsersToModel(model);
        if (!userService.updateUser(user)) {
            model.addAttribute("login", user.getLogin());
            model.addAttribute("id", user.getId());
            model.addAttribute("isAlreadyExist2", true);
        }

        return "admin";
    }
}
