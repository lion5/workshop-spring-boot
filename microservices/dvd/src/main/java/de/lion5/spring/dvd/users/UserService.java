package de.lion5.spring.dvd.users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepo;

    @Autowired
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public WebUser loadUserByUsername(String username) throws UsernameNotFoundException {
        WebUser user = userRepo.findUserByUsername(username);

        if (user != null) {
            return user;
        }

        throw new UsernameNotFoundException("User '" + username + "' not found!");
    }

    public long count() {
        return this.userRepo.count();
    }

    public WebUser save(WebUser user) {
        return this.userRepo.save(user);
    }

    public List<WebUser> findAll() {
        return this.userRepo.findAll();
    }
}
