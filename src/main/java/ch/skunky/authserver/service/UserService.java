package ch.skunky.authserver.service;

import ch.skunky.authserver.model.User;
import ch.skunky.authserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerNewUserAccount(User user) {
        if (userExist(user)) {
            throw new IllegalArgumentException("There is an account with that email adress: " + user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    private boolean userExist(final User user) {
        return (userRepository.findByEmail(user.getEmail()) != null) ||
                (userRepository.findByUsername(user.getUsername()) != null);
    }

}
