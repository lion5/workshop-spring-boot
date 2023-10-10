package de.lion5.spring.dvd.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserRepository extends JpaRepository<WebUser, Long> {

    public WebUser findUserByUsername(String username) throws UsernameNotFoundException;
}
