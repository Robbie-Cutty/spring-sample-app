package com.jeffspring.store.controller;

import com.jeffspring.store.model.User;
import com.jeffspring.store.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService,
                          PasswordEncoder passwordEncoder){
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model, HttpSession session){
        model.addAttribute("user",new User());
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, HttpServletResponse res){
        res.setHeader("Cache-Control","no-cache, no-store, must-revalidate");
        res.setHeader("Pragma","no-cache");
        res.setHeader("Expires","0");
        if(session.getAttribute("isLoggedIn") == null){
            session.setAttribute("msg","Please log in first");
            return "redirect:/login";
        }
        return "tasks";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session){
        String nor = email.trim().toLowerCase();
        User user = userService.findByEmail(nor);
        if(user != null && passwordEncoder.matches(password,user.getPassword())){
            session.setAttribute("user",user);
            session.setAttribute("isLoggedIn",true);
            return "redirect:/tasks";
        } else{
            session.setAttribute("msg","Invalid credentials, please try again later");
            return "redirect:/login";
        }
    }

    @PostMapping("/createuser")
    public String createUser(@ModelAttribute("user") User user,
                             HttpSession session){
        String nor = user.getEmail().trim().toLowerCase();
        if(user.getEmail() == null || user.getEmail().isEmpty()){
            session.setAttribute("msg","Please fill in email");
            return "redirect:/register";
        }
        if(userService.checkEmail(nor)){
            session.setAttribute("msg","Email already exists, please try again");
            return "redirect:/register";
        }
        user.setEmail(nor);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(userService.createUser(user) != null){
            session.setAttribute("msg","Successful Registration");
        } else{
            session.setAttribute("msg","Failed Registration, please try again");
        }
        return "redirect:/login";
    }
}
