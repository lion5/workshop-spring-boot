package de.lion5.spring.dvd.users;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping(value = "/register")
public class RegistrationController {

    private UserRepository userRepo;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationController(UserRepository userRepo, PasswordEncoder passwordEncoder){
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String getRegistrationForm(Model model){

        log.info("Show registration page");
        model.addAttribute("registrationForm", new RegistrationForm());

        return "register";
    }

    @PostMapping
    public String createUser(@Valid RegistrationForm registrationForm, Errors errors) {

        if(errors.hasErrors()) {
            log.info("User registration contained errors: " + registrationForm.toString());
            return "register";
        }

        // first user is admin
        if(userRepo.count() == 0) {
            userRepo.save(registrationForm.toUser(passwordEncoder, "ROLE_ADMIN"));
        } else {
            userRepo.save(registrationForm.toUser(passwordEncoder, "ROLE_USER"));
        }

        return "login";
    }
}
