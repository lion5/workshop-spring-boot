package de.lion5.spring.dvd;

import de.lion5.spring.dvd.users.RegistrationForm;
import de.lion5.spring.dvd.users.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerIntegrationTest {

    @SpyBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;

    private static final String BASE_PATH = "/register";

    private MockHttpServletRequestBuilder createPostRequest(RegistrationForm registrationForm) {
        return post(BASE_PATH)
                .param("username", registrationForm.getUsername())
                .param("fullName", registrationForm.getFullName())
                .param("password", registrationForm.getPassword())
                .param("phoneNumer", registrationForm.getPhoneNumber());
    }

    @Test
    @Transactional
    public void postRegistrationRequest_createAdminUserAsFirstUser() throws Exception {

        // store user in DB
        when(userService.count()).thenReturn(0L);
        RegistrationForm registrationForm = new RegistrationForm("junitadmin", "1#Asdfjklö", "Test User", "0951");

        // test storing admin user
        mvc.perform(this.createPostRequest(registrationForm)
                        .with(csrf()))
           .andExpect(status().isOk())
           .andExpect(view().name("login"));

        // confirm that stored user has role admin
        assertTrue(this.userService.loadUserByUsername("junitadmin")
                                   .getAuthorities()
                                   .contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    @Transactional
    public void postRegistrationRequest_createUserAsSecondUser() throws Exception {

        // store user in DB
        when(userService.count()).thenReturn(1L);
        RegistrationForm registrationForm = new RegistrationForm("junituser", "1#Asdfjklö", "Test User", "0951");

        // test storing user
        mvc.perform(this.createPostRequest(registrationForm)
                        .with(csrf()))
           .andExpect(status().isOk())
           .andExpect(view().name("login"));

        // confirm that stored user has role user
        assertTrue(this.userService.loadUserByUsername("junituser")
                                   .getAuthorities()
                                   .contains(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
