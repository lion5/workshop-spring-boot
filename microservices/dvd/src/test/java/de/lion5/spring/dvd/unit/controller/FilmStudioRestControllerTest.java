package de.lion5.spring.dvd.unit.controller;

import java.time.LocalDate;
import java.util.List;

import de.lion5.spring.dvd.model.FilmStudio;
import de.lion5.spring.dvd.model.Movie;
import de.lion5.spring.dvd.service.FilmStudioService;
import de.lion5.spring.dvd.service.MovieServiceException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmStudioRestControllerTest {

    @MockBean
    private FilmStudioService studioService;
    @Autowired
    private MockMvc mvc;

    private static final String BASE_PATH = "/v1/studios";

    private FilmStudio warner = new FilmStudio(1L, "Warner Bros. Pictures",
            LocalDate.of(1923, 4, 4),
            List.of(new Movie(1L, "Inception", false, 2010,
                    "https://cdn.pixabay.com/photo/2017/05/15/17/43/calm-2315559_960_720.jpg",
                    null, null, null)));

    @Test
    public void getRequestStudios_minimalApiCompliant() throws Exception {
        when(studioService.findAll()).thenReturn(List.of(warner));

        mvc.perform(MockMvcRequestBuilders.get(BASE_PATH))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$._embedded.studios[0].name", Matchers.is(warner.getName())))
           .andExpect(jsonPath("$._embedded.studios[0].since", Matchers.is(warner.getSince().toString())))
           .andExpect(jsonPath("$._embedded.studios[0].movies[0].title", Matchers.is(warner.getMovies().get(0).getTitle())))
           .andExpect(jsonPath("$._embedded.studios[0].movies[0].year", Matchers.is(warner.getMovies().get(0).getYear())))
           .andExpect(jsonPath("$._embedded.studios[0].movies[0]._links.self.href", Matchers.endsWith(warner.getMovies().get(0).getId().toString())))
           .andExpect(jsonPath("$._embedded.studios[0]._links.self.href", Matchers.endsWith(warner.getId().toString())));
    }

    @Test
    public void getRequestStudio_notFound() throws Exception, MovieServiceException {
        when(studioService.findById(any(Long.class))).thenThrow(new MovieServiceException(""));
        mvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/2"))
           .andExpect(status().isNotFound());
    }

    @Test
    public void getRequestStudio_minimalApiCompliant() throws Exception, MovieServiceException {
        when(studioService.findById(warner.getId())).thenReturn(warner);

        mvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + warner.getId()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.name", Matchers.is(warner.getName())))
           .andExpect(jsonPath("$.since", Matchers.is(warner.getSince().toString())))
           .andExpect(jsonPath("$.movies[0].title", Matchers.is(warner.getMovies().get(0).getTitle())))
           .andExpect(jsonPath("$.movies[0].year", Matchers.is(warner.getMovies().get(0).getYear())))
           .andExpect(jsonPath("$.movies[0]._links.self.href", Matchers.endsWith(warner.getMovies().get(0).getId().toString())))
           .andExpect(jsonPath("$._links.self.href", Matchers.endsWith(warner.getId().toString())));
    }
}
