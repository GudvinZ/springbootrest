package test.springbootrest.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import test.springbootrest.model.User;
import test.springbootrest.service.UserService;

@Controller
public class HomeController {
    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void addPageAttr(ModelMap model) {
        model.addAttribute("homePage", true);
    }

    @GetMapping("/")
    public String homePage(ModelMap model) {

        return "home";
    }

    @PostMapping("/reg")
    public String registration(@ModelAttribute User user, ModelMap model) {
        user.setRoles("user");
        if (userService.addUser(user)) {
            org.springframework.security.core.userdetails.UserDetails userDetails = userService.loadUserByUsername(user.getLogin());
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        } else
            model.addAttribute("isAlreadyExist", true);
        return "home";
    }
}
