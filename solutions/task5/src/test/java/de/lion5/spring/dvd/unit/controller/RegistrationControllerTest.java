package de.lion5.spring.dvd.unit.controller;

import de.lion5.spring.dvd.users.RegistrationForm;
import de.lion5.spring.dvd.users.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerTest {

    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;

    private static final String BASE_PATH = "/register";

    @Test
    public void getRegistrationRequest_gettingHTMLModel() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.get(BASE_PATH))
                .andExpect(model().size(1))
                .andExpect(status().isOk());
    }

    private MockHttpServletRequestBuilder createPostRequest(RegistrationForm registrationForm) {
        return post(BASE_PATH)
                .param("username", registrationForm.getUsername())
                .param("fullName", registrationForm.getFullName())
                .param("password", registrationForm.getPassword())
                .param("phoneNumer", registrationForm.getPhoneNumber());
    }

    @Test
    public void postRegistrationRequest_validationErrors() throws Exception {
        RegistrationForm registrationForm = new RegistrationForm("AX", "1!Asdfjkö", "", "0951");

        mvc.perform(this.createPostRequest(registrationForm)
                        .with(csrf()))
           .andExpect(model().hasErrors())
           .andExpect(view().name("register"));
    }

    @Test
    public void postRegistrationRequest_createUser() throws Exception {
        when(userService.count()).thenReturn(0L);
        RegistrationForm registrationForm = new RegistrationForm("testuser", "1#Asdfjklö", "Test User", "0951");

        mvc.perform(this.createPostRequest(registrationForm)
                        .with(csrf()))
           .andExpect(status().isOk())
           .andExpect(view().name("login"));
    }
}
