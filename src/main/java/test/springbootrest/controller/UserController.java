package test.springbootrest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
public class UserController {

    @ModelAttribute
    public void addPageAttr(ModelMap model) {
        model.addAttribute("userPage", true);
    }

    @GetMapping
    public String userPage() {
        return "user";
    }
}
