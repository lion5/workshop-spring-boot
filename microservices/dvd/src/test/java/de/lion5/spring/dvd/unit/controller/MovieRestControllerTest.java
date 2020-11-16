package de.lion5.spring.dvd.unit.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.lion5.spring.dvd.api.dto.MoviePostDTO;
import de.lion5.spring.dvd.model.Actor;
import de.lion5.spring.dvd.model.FilmStudio;
import de.lion5.spring.dvd.model.Movie;
import de.lion5.spring.dvd.service.ActorService;
import de.lion5.spring.dvd.service.CustomUserService;
import de.lion5.spring.dvd.service.FilmStudioService;
import de.lion5.spring.dvd.service.MovieService;
import de.lion5.spring.dvd.service.MovieServiceException;
import de.lion5.spring.dvd.users.User;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MovieRestControllerTest {

    public static final String BASE_PATH_MOVIES = "/v1/movies";
    @MockBean
    private MovieService movieService;
    @MockBean
    private FilmStudioService studioService;
    @MockBean
    private CustomUserService userService;
    @MockBean
    private ActorService actorService;
    @Autowired
    private MockMvc mvc;

    private List<Movie> movies;
    private Actor jonny;
    private FilmStudio studio;
    private User adminUser;

    private ObjectMapper mapper = new ObjectMapper();

    // mock service layer
    private void stubMovieServiceFindAll() {
        when(this.movieService.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(this.movies));
    }

    @BeforeEach
    public void initCommonUsedData() {
        this.movies = new ArrayList<>();
        this.jonny = new Actor(1L, "Jonny Depp", false, null, null);
        this.studio = new FilmStudio(1L, "DSG Film", null, null);
        this.adminUser = new User("testAdmin", "super", "Super User", "+49 170", "ROLE_ADMIN");
        this.movies.add(new Movie(1L, "Test Movie", false, 2000, "https://.png",
                Arrays.asList(this.jonny),
                this.studio,
                this.adminUser));
    }

    @Test
    public void getRequestMovies_invalidQueryParameterValues() throws Exception {
        // stubbing
        this.stubMovieServiceFindAll();
        Movie test = this.movies.get(0);

        this.mvc.perform(MockMvcRequestBuilders.get("/v1/movies?page=-1")
                                               .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getRequestSingleMovie_notFound() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.get(BASE_PATH_MOVIES + "/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getRequestSingleMovie_successful() throws Exception {
        Movie test = this.movies.get(0);
        when(this.movieService.findById(test.getId())).thenReturn(Optional.of(test));
        this.mvc.perform(MockMvcRequestBuilders.get(BASE_PATH_MOVIES + "/" + test.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is(test.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.wonOscar", Matchers.is(test.isWonOscar())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.year", Matchers.is(test.getYear())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.coverImage", Matchers.is(test.getCoverImage())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.actors[0].name", Matchers.is(jonny.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.actors[0]._links.self.href", Matchers.endsWith(jonny.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.filmStudio.name", Matchers.is(studio.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.filmStudio._links.self.href", Matchers.endsWith(studio.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is(adminUser.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self.href", Matchers.endsWith(test.getId().toString())));
    }

    @Test
    public void getRequestMovies_minimalFullAPICompliantResponse() throws Exception {
        // stubbing
        this.stubMovieServiceFindAll();
        Movie test = this.movies.get(0);

        this.mvc.perform(MockMvcRequestBuilders.get("/v1/movies")
                                               .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.movies[0].title", Matchers.is(test.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.movies[0].wonOscar", Matchers.is(test.isWonOscar())))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.movies[0].year", Matchers.is(test.getYear())))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.movies[0].coverImage", Matchers.is(test.getCoverImage())))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.movies[0].actors[0].name", Matchers.is(jonny.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.movies[0].actors[0]._links.self.href", Matchers.endsWith(jonny.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.movies[0].filmStudio.name", Matchers.is(studio.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.movies[0].filmStudio._links.self.href", Matchers.endsWith(studio.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.movies[0].username", Matchers.is(adminUser.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.movies[0]._links.self.href", Matchers.endsWith(test.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.movies.href", Matchers.endsWith("?page=0")));
    }

    private MoviePostDTO createMovieDTO(Movie m) throws MovieServiceException {
        // stubbing
        when(this.actorService.findActors(List.of(jonny.getId()))).thenReturn(List.of(jonny));
        when(this.studioService.findById(studio.getId())).thenReturn(studio);
        when(this.userService.findUserByUsername(adminUser.getUsername())).thenReturn(adminUser);

        return new MoviePostDTO(m.getTitle(), m.isWonOscar(), m.getYear(), m.getCoverImage(), List.of(jonny.getId()),
                studio.getId(), adminUser.getUsername());
    }

    @Test
    public void postRequestMovies() throws MovieServiceException, Exception {
        Movie m = this.movies.get(0);
        when(this.movieService.saveAndSetId(any(Movie.class))).thenReturn(m);
        MoviePostDTO movieDTO = this.createMovieDTO(m);

        this.mvc.perform(post(BASE_PATH_MOVIES).contentType(MediaType.APPLICATION_JSON)
                                               .content(mapper.writeValueAsString(this.createMovieDTO(this.movies.get(0)))))
                .andExpect(status().isCreated());
    }

    @Test
    public void postRequestMovies_actorNotFound() throws MovieServiceException, Exception {
        // stubbing
        when(this.actorService.findActors(any(List.class))).thenThrow(new MovieServiceException(""));
        when(this.studioService.findById(studio.getId())).thenReturn(studio);
        when(this.userService.findUserByUsername(adminUser.getUsername())).thenReturn(adminUser);
        Movie m = this.movies.get(0);
        when(this.movieService.saveAndSetId(any(Movie.class))).thenReturn(m);

        MoviePostDTO moviePostDTO = new MoviePostDTO(m.getTitle(), m.isWonOscar(), m.getYear(), m.getCoverImage(),
                List.of(jonny.getId()), studio.getId(), adminUser.getUsername());

        // when
        this.mvc.perform(post(BASE_PATH_MOVIES).contentType(MediaType.APPLICATION_JSON)
                                               .content(mapper.writeValueAsString(moviePostDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void postRequestMovies_filmStudioNotFound() throws MovieServiceException, Exception {
        // stubbing
        when(this.actorService.findActors(List.of(jonny.getId()))).thenReturn(List.of(jonny));
        when(this.studioService.findById(any(Long.class))).thenThrow(new MovieServiceException(""));
        when(this.userService.findUserByUsername(adminUser.getUsername())).thenReturn(adminUser);
        Movie m = this.movies.get(0);
        when(this.movieService.saveAndSetId(any(Movie.class))).thenReturn(m);

        MoviePostDTO moviePostDTO = new MoviePostDTO(m.getTitle(), m.isWonOscar(), m.getYear(), m.getCoverImage(), List.of(jonny.getId()), studio.getId(), adminUser.getUsername());

        // when
        this.mvc.perform(post(BASE_PATH_MOVIES).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(moviePostDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void postRequestMovies_userNameNotFound() throws Exception, MovieServiceException {
        // stubbing
        when(this.actorService.findActors(List.of(jonny.getId()))).thenReturn(List.of(jonny));
        when(this.studioService.findById(studio.getId())).thenReturn(studio);
        when(this.userService.findUserByUsername(any(String.class))).thenThrow(new MovieServiceException(""));
        Movie m = this.movies.get(0);
        when(this.movieService.saveAndSetId(any(Movie.class))).thenReturn(m);

        MoviePostDTO moviePostDTO = new MoviePostDTO(m.getTitle(), m.isWonOscar(), m.getYear(), m.getCoverImage(), List.of(jonny.getId()), studio.getId(), adminUser.getUsername());

        // when
        this.mvc.perform(post(BASE_PATH_MOVIES).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(moviePostDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void putRequestMovies_validationErrors() throws Exception {
        MoviePostDTO moviePostDTO = new MoviePostDTO("", true, 3020, "http:.png",
                List.of(jonny.getId()), studio.getId(), adminUser.getUsername());

        this.mvc.perform(put(BASE_PATH_MOVIES + "/1").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(moviePostDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void putRequestMovies_notFound() throws Exception {
        Movie m = this.movies.get(0);
        MoviePostDTO moviePostDTO = new MoviePostDTO("A new title", m.isWonOscar(), m.getYear(), m.getCoverImage(), List.of(jonny.getId()), studio.getId(), adminUser.getUsername());

        this.mvc.perform(put(BASE_PATH_MOVIES + "/1").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(moviePostDTO)))
                .andExpect(status().isNotFound());
    }

    private MoviePostDTO createMovieDTOForUpdate(Movie m) throws MovieServiceException {
        MoviePostDTO moviePostDTO = this.createMovieDTO(m);
        when(this.movieService.update(any(Movie.class))).thenReturn(m);
        when(this.movieService.findById(m.getId())).thenReturn(Optional.of(m));

        moviePostDTO.setTitle("A really new title");
        return moviePostDTO;
    }

    @Test
    public void putRequestMovies_created() throws MovieServiceException, Exception {
        Movie m = this.movies.get(0);
        MoviePostDTO moviePostDTO = this.createMovieDTOForUpdate(m);

        this.mvc.perform(put(BASE_PATH_MOVIES + "/" + m.getId())
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(moviePostDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    public void patchRequestMovies_ok() throws MovieServiceException, Exception {
        Movie m = this.movies.get(0);
        MoviePostDTO moviePostDTO = this.createMovieDTOForUpdate(m);

        this.mvc.perform(patch(BASE_PATH_MOVIES + "/" + m.getId())
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(moviePostDTO)))
                .andExpect(status().isOk());
    }

    @Test
    public void patchRequestMovies_notFound() throws MovieServiceException, Exception {
        Movie m = this.movies.get(0);
        MoviePostDTO moviePostDTO = this.createMovieDTOForUpdate(m);

        this.mvc.perform(patch(BASE_PATH_MOVIES + "/42")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(moviePostDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void patchRequestMovies_badRequest() throws MovieServiceException, Exception {
        Movie m = this.movies.get(0);
        MoviePostDTO moviePostDTO = this.createMovieDTOForUpdate(m);
        moviePostDTO.setTitle("");
        moviePostDTO.setYear(3000);
        moviePostDTO.setCoverImage("");

        this.mvc.perform(patch(BASE_PATH_MOVIES + "/" + m.getId())
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(moviePostDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteRequestMovies_noContent() throws Exception {
        Movie m = this.movies.get(0);
        when(this.movieService.findById(m.getId())).thenReturn(Optional.of(m));

        this.mvc.perform(delete(BASE_PATH_MOVIES + "/" + m.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteRequestMovies_notFound() throws Exception {

        this.mvc.perform(delete(BASE_PATH_MOVIES + "/42"))
                .andExpect(status().isNotFound());
    }
}
