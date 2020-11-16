package de.lion5.spring.dvd.unit.controller;

import java.util.ArrayList;
import java.util.List;

import de.lion5.spring.dvd.model.Movie;
import de.lion5.spring.dvd.service.MovieService;
import de.lion5.spring.dvd.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;

// SpringBootTest does not start a server by default, only when port is configured
@SpringBootTest//(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class MovieControllerTest {

    @MockBean
    private MovieService movieService;
    @Autowired
    private MockMvc mvc;

    private List<Movie> movies;
    private User adminUser;
    private User user;

    @BeforeEach
    public void initCommonUsedData() {
        this.movies = new ArrayList<>();
        this.movies.add(new Movie(1L, "Test Movie", false, 2000, "http://.png", null, null, null));
        this.adminUser = new User("testAdmin", "super", "Super User", "+49 170", "ROLE_ADMIN");
        this.user = new User("testUser", "normal", "User", "+49 171", "ROLE_USER");
    }

    // stub service layer
    private void stubMovieServiceFindAll() {
        when(this.movieService.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(this.movies));
    }

    private MockHttpServletRequestBuilder createGetRequestBuilder() {
        return MockMvcRequestBuilders.get("/movies");
    }

    private MockHttpServletRequestBuilder createPostRequestBuilder(Movie m) {
        return MockMvcRequestBuilders.post("/movies")
                                     .param("title", m.getTitle())
                                     .param("year", "" + m.getYear())
                                     .param("coverImage", m.getCoverImage());
    }

    @Test
    public void getRequestMovies_anonymousUser_redirectToLogin() throws Exception {
        this.mvc.perform(this.createGetRequestBuilder().with(anonymous()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void getRequestMovies_authenticatedUser_gettingHTMLModel() throws Exception {
        // stub service layer and database layer
        this.stubMovieServiceFindAll();
        this.mvc.perform(this.createGetRequestBuilder().with(user(this.user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(4))
                .andExpect(MockMvcResultMatchers.model().attribute("movies", this.movies));
    }

    @Test
    public void getRequestMovies_authenticatedAdmin_gettingHTMLModel() throws Exception {
        // stub service layer and database layer
        this.stubMovieServiceFindAll();
        this.mvc.perform(
                this.createGetRequestBuilder().with(user(this.adminUser)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(4))
                .andExpect(MockMvcResultMatchers.model().attribute("movies", this.movies));
    }

    @Test
    public void postRequestMovies_authenticatedUser_forbidden() throws Exception {
        this.stubMovieServiceFindAll();
        this.mvc.perform(this.createPostRequestBuilder(this.movies.get(0)).with(user(this.user)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void postRequestMovies_authenticatedAdminCreateMovie_updatedMovieList() throws Exception {

        this.stubMovieServiceFindAll();
        Movie m = new Movie(null, "Random title", false, 2000, "https://.png", null, null, null);

        List<Movie> spyList = Mockito.spy(this.movies);
        when(this.movieService.saveAndSetId(any(Movie.class))).thenAnswer(invocation -> {
            Movie requested = invocation.getArgument(0, Movie.class);
            spyList.add(requested);
            return requested;
        });

        this.mvc.perform(this.createPostRequestBuilder(m).with(csrf()).with(user(this.adminUser)))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());

        verify(this.movieService).saveAndSetId(m);
        verify(this.movieService, times(1)).saveAndSetId(any(Movie.class));
        verify(spyList, times(1)).add(any(Movie.class));

        assertEquals(2, spyList.size());
    }

    @Test
    public void postRequestMovies_authenticatedAdminCreateMovie_validationError() throws Exception {
        this.stubMovieServiceFindAll();
        Movie m = new Movie(1L, "Test Movie", false, 2000, "http://.png", null, null, null);
        this.mvc.perform(this.createPostRequestBuilder(m).with(csrf()).with(user(this.adminUser)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().hasErrors());
    }
}
