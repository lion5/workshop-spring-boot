package de.lion5.spring.dvd.controller;

import javax.validation.Valid;

import de.lion5.spring.dvd.model.Movie;
import de.lion5.spring.dvd.properties.MovieProperties;
import de.lion5.spring.dvd.repository.MovieRepository;
import de.lion5.spring.dvd.users.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping(value = "/movies")
public class MovieController {

    private final MovieRepository movieRepository;
    private final MovieProperties props;

    @Autowired
    public MovieController( MovieRepository movieRepository, MovieProperties props) {
        this.movieRepository = movieRepository;
        this.props = props;
    }

    @GetMapping
    public String getMovies(Model model, @RequestParam(defaultValue = "0") int page) {

        log.info("Client requested movies");

        Page<Movie> pagedMovies = this.movieRepository.findAll(PageRequest.of(page, props.getPageSize()));

        model.addAttribute("movies", pagedMovies.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("noOfPages", pagedMovies.getTotalPages());
        model.addAttribute("movie", new Movie());

        return "movies";
    }

    @PostMapping
    public String processMovie(@Valid Movie movie, Errors errors, Model model, @AuthenticationPrincipal User user) {
        log.info("Client POSTed a new movie: " + movie);
        if(errors.hasErrors()){
            model.addAttribute("movies", this.movieRepository.findAll());
            log.info(" . . . but there are errors included: " + movie);
            return "movies";
        }

        movie.setId(this.movieRepository.count() + 1L);
        movie.setCreatedBy(user);
        this.movieRepository.save(movie);

        return "redirect:/movies";
    }
}
