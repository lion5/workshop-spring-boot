package de.lion5.spring.dvd;

import de.lion5.spring.dvd.users.User;
import de.lion5.spring.dvd.users.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository userRepo;

    private static final String BASE_PATH = "/users";

    private User adminUser = new User("junitadmin", "1#2Asdfjklö", "Test Admin", "+49", "ROLE_ADMIN");
    private User user = new User("junituser", "1#2Asdfjklö", "TestUser", "+49", "ROLE_USER");

    @BeforeEach
    public void initDemoData() {
        userRepo.saveAndFlush(adminUser);
        userRepo.saveAndFlush(user);
    }

    @Test
    @Transactional
    public void postUsersRequest_changeUserRightsAdminToUser() throws Exception {

        mvc.perform(post(BASE_PATH + "/" + adminUser.getUsername() + "/roles").with(user(adminUser)).with(csrf())
                                                                              .param("newRole", "ROLE_USER"))
           .andExpect(redirectedUrl("/users"));

        assertEquals("ROLE_USER", userRepo.findUserByUsername(adminUser.getUsername()).getRole());
    }

    @Test
    @Transactional
    public void postUsersRequest_changeUserRightsUserToAdmin() throws Exception {

        mvc.perform(post(BASE_PATH + "/" + user.getUsername() + "/roles").with(user(user)).with(csrf())
                                                                         .param("newRole", "ROLE_ADMIN"))
           .andExpect(redirectedUrl("/users"));

        assertEquals("ROLE_ADMIN", userRepo.findUserByUsername(user.getUsername()).getRole());
    }
}
