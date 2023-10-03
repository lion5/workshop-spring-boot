package de.lion5.spring.dvd.service;

import de.lion5.spring.dvd.users.UserRepository;
import de.lion5.spring.dvd.users.WebUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserService {

    private UserRepository userRepository;

    @Autowired
    public CustomUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public WebUser findUserByUsername(String username) throws MovieServiceException {
        if (username == null) {
            return null;
        }

        WebUser user = this.userRepository.findUserByUsername(username);
        if (user == null) {
            throw new MovieServiceException("User not found with this username");
        }
        return user;
    }
}
