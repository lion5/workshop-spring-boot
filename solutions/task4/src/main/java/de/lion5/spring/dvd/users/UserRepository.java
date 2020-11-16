package de.lion5.spring.dvd.users;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserRepository extends CrudRepository<User, Long> {

    public User findUserByUsername(String username) throws UsernameNotFoundException;
}
