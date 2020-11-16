package de.lion5.spring.dvd.unit.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.lion5.spring.dvd.model.Actor;
import de.lion5.spring.dvd.model.Movie;
import de.lion5.spring.dvd.service.ActorService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ActorRestControllerTest {

    @MockBean
    private ActorService actorService;
    @Autowired
    private MockMvc mvc;

    private Actor tomHanks, diCaprio;
    private Movie inception, cloudAtlas;
    private List<Actor> actorList;

    private static final String BASE_PATH = "/v1/actors";

    @BeforeEach
    public void initDemoData() {
        inception = new Movie(1L, "Inception", false, 2010, "https://cdn.pixabay.com/photo/2017/05/15/17/43/calm-2315559_960_720.jpg", null, null, null);
        cloudAtlas = new Movie(2L, "Cloud Atlas", false, 2012, "https://cdn.pixabay.com/photo/2020/03/02/16/19/vintage-4896141_960_720.jpg", null, null, null);

        tomHanks = new Actor(1L, "Tom Hanks", true, LocalDate.of(1956, 7, 9), List.of(inception, cloudAtlas));
        actorList = new ArrayList<>(List.of(tomHanks));
    }

    @Test
    public void getRequestActors_minimalFullAPICompliantResponse() throws Exception {

        when(actorService.findAll()).thenReturn(this.actorList);

        mvc.perform(MockMvcRequestBuilders.get(BASE_PATH).accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$._embedded.actors[0].name", Matchers.is(tomHanks.getName())))
           .andExpect(jsonPath("$._embedded.actors[0].wonOscar", Matchers.is(tomHanks.isWonOscar())))
           .andExpect(jsonPath("$._embedded.actors[0].birthday", Matchers.is(tomHanks.getBirthday().toString())))
           .andExpect(jsonPath("$._embedded.actors[0].movies[0].title", Matchers.is(inception.getTitle())))
           .andExpect(jsonPath("$._embedded.actors[0].movies[0].year", Matchers.is(inception.getYear())))
           .andExpect(jsonPath("$._embedded.actors[0].movies[0]._links.self.href", Matchers.endsWith(inception.getId().toString())))
           .andExpect(jsonPath("$._embedded.actors[0].movies[1].title", Matchers.is(cloudAtlas.getTitle())))
           .andExpect(jsonPath("$._embedded.actors[0].movies[1].year", Matchers.is(cloudAtlas.getYear())))
           .andExpect(jsonPath("$._embedded.actors[0].movies[1]._links.self.href", Matchers.endsWith(cloudAtlas.getId().toString())))
           .andExpect(jsonPath("$._embedded.actors[0]._links.self.href", Matchers.endsWith(tomHanks.getId().toString())));
    }

    @Test
    public void getRequestActorById_notFound() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/1")).andExpect(status().isNotFound());
    }

    @Test
    public void getRequestActorById_minimalFullAPICompliantResponse() throws Exception {
        when(actorService.findById(tomHanks.getId())).thenReturn(Optional.of(tomHanks));
        mvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + tomHanks.getId())
                                          .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.name", Matchers.is(tomHanks.getName())))
           .andExpect(jsonPath("$.wonOscar", Matchers.is(tomHanks.isWonOscar())))
           .andExpect(jsonPath("$.birthday", Matchers.is(tomHanks.getBirthday().toString())))
           .andExpect(jsonPath("$.movies[0].title", Matchers.is(inception.getTitle())))
           .andExpect(jsonPath("$.movies[0].year", Matchers.is(inception.getYear())))
           .andExpect(jsonPath("$.movies[0]._links.self.href", Matchers.endsWith(inception.getId().toString())))
           .andExpect(jsonPath("$.movies[1].title", Matchers.is(cloudAtlas.getTitle())))
           .andExpect(jsonPath("$.movies[1].year", Matchers.is(cloudAtlas.getYear())))
           .andExpect(jsonPath("$.movies[1]._links.self.href", Matchers.endsWith(cloudAtlas.getId().toString())))
           .andExpect(jsonPath("$._links.self.href", Matchers.endsWith(tomHanks.getId().toString())));
    }

    @Test
    public void deleteRequestActorById_notFound() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + "/1")).andExpect(status().isNotFound());
    }

    @Test
    public void deleteRequestActorById_noContent() throws Exception {
        when(actorService.findById(tomHanks.getId())).thenReturn(Optional.of(tomHanks));
        mvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + "/1"))
           .andExpect(status().isNoContent());
    }
}
