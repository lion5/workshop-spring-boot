package de.lion5.spring.dvd.unit.controller;

import de.lion5.spring.dvd.users.User;
import de.lion5.spring.dvd.users.UserRepository;
import de.lion5.spring.dvd.users.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;

    private User adminUser = new User("junitadmin", "1#2Asdfjklö", "Test Admin", "+49", "ROLE_ADMIN");
    private User user = new User("junituser", "1#2Asdfjklö", "TestUser", "+49", "ROLE_USER");
    private static final String BASE_PATH = "/users";

    @Test
    public void getUsersRequest_allUsers_authenticated() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(BASE_PATH).with(user(adminUser)))
           .andExpect(status().isOk())
           .andExpect(view().name("users"))
           .andExpect(model().size(3));
    }

    @Test
    public void getUsersRequest_allUsers_notAuthenticated() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(BASE_PATH).with(user(user)))
           .andExpect(status().isForbidden());
    }

    @Test
    public void postUsersRequest_userNotFound() throws Exception {
        mvc.perform(post(BASE_PATH + "/asdf/roles").with(user(adminUser)).with(csrf())
                                                   .param("newRole", "ROLE_USER"))
           .andExpect(redirectedUrlPattern("/*"));
    }

    @Test
    @Transactional
    public void postUsersRequest_changeUserRightsToAdmin(@Autowired UserRepository userRepo) throws Exception {

        userRepo.save(adminUser);

        mvc.perform(post(BASE_PATH + "/" + adminUser.getUsername() + "/roles").with(user(adminUser)).with(csrf())
                                                                              .param("newRole", "ROLE_USER"))
           .andExpect(redirectedUrlPattern("/*"));

        assertEquals(adminUser, userRepo.findUserByUsername(adminUser.getUsername()));
    }
}
