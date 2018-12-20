package ch.skunky.authserver.controller;

import ch.skunky.authserver.model.User;
import ch.skunky.authserver.repository.UserRepository;
import ch.skunky.authserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping(value="/user/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public User registerUser(@RequestBody User user){
        User registeredUser = userService.registerNewUserAccount(user);
        return registeredUser;
    }

}
