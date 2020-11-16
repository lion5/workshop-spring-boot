package de.lion5.spring.dvd.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import de.lion5.spring.dvd.model.Movie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping(value = "/movies")
public class MovieController {

    private final List<Movie> movies;

    public MovieController() {
        this.movies = new ArrayList<>();
        // init movie list
        this.movies.add(new Movie("1", "Inception", false, 2010, "https://cdn.pixabay.com/photo/2017/05/15/17/43/calm-2315559_960_720.jpg", null));
        this.movies.add(new Movie("2", "Cloud Atlas", false, 2012, "https://cdn.pixabay.com/photo/2020/03/02/16/19/vintage-4896141_960_720.jpg", null));
    }

    @GetMapping
    public String getMovies(Model model) {

        log.info("Client requested all movies");

        model.addAttribute("movies", this.movies);
        model.addAttribute("movie", new Movie());

        return "movies";
    }

    @PostMapping
    public String processMovie(@Valid Movie movie, Errors errors, Model model) {
        log.info("Client POSTed a new movie: " + movie);
        if(errors.hasErrors()){
            model.addAttribute("movies", this.movies);
            log.info(" . . . but there are errors included: " + movie);
            return "movies";
        }

        movie.setId("" + (this.movies.size()+1));
        this.movies.add(movie);

        return "redirect:/movies";
    }
}
