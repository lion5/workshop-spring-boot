package de.lion5.spring.dvd.api.controller;

import java.util.Optional;

import javax.validation.Valid;

import de.lion5.spring.dvd.api.RestControllerException;
import de.lion5.spring.dvd.api.assembler.MovieRepresentationalModelAssembler;
import de.lion5.spring.dvd.api.dto.MoviePostDTO;
import de.lion5.spring.dvd.api.model.MovieRepresentationalModel;
import de.lion5.spring.dvd.model.Movie;
import de.lion5.spring.dvd.properties.MovieProperties;
import de.lion5.spring.dvd.service.ActorService;
import de.lion5.spring.dvd.service.CustomUserService;
import de.lion5.spring.dvd.service.FilmStudioService;
import de.lion5.spring.dvd.service.MovieService;
import de.lion5.spring.dvd.service.MovieServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping(path = "/v1/movies",
        produces = "application/json")
@CrossOrigin("*")
public class MovieRestController {

    // attributes & constructor

    private MovieProperties props;
    private MovieService movieService;
    private ActorService actorService;
    private FilmStudioService filmStudioService;
    private CustomUserService customUserService;
    private Validator validator;

    @Autowired
    public MovieRestController(MovieProperties props, MovieService movieService, ActorService actorService, FilmStudioService filmStudioService, CustomUserService customUserService, Validator validator) {
        this.props = props;
        this.movieService = movieService;
        this.actorService = actorService;
        this.filmStudioService = filmStudioService;
        this.customUserService = customUserService;
        this.validator = validator;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<MovieRepresentationalModel>> getMovies(@RequestParam(defaultValue = "0") int page) {
        if (page < 0) {
            return ResponseEntity.badRequest().build();
        }
        log.info("Find movies . . .");
        Iterable<Movie> movies = this.movieService.findAll(PageRequest.of(page, this.props.getPageSize())).getContent();

        CollectionModel<MovieRepresentationalModel> collection = CollectionModel.of(new MovieRepresentationalModelAssembler().toCollectionModel(movies));
        collection.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MovieRestController.class).getMovies(page)).withRel("movies"));

        return new ResponseEntity<>(collection, OK);
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<MovieRepresentationalModel> findMovie(@PathVariable("id") Long id) {
        Optional<Movie> m = this.movieService.findById(id);
        if (m.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(new MovieRepresentationalModelAssembler().toModel(m.get()), OK);
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<MovieRepresentationalModel> createMovie(@RequestBody @Valid MoviePostDTO movieDTO, Errors errors) {
        // javax validation
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Movie movie = new Movie(movieDTO.getTitle(), movieDTO.isWonOscar(),
                    movieDTO.getYear(), movieDTO.getCoverImage(), this.actorService.findActors(movieDTO.getActorIds()),
                    this.filmStudioService.findById(movieDTO.getFilmStudioId()), this.customUserService.findUserByUsername(movieDTO.getUsername()));

            return new ResponseEntity<>(new MovieRepresentationalModelAssembler().toModel(this.movieService.saveAndSetId(movie)), CREATED);
        } catch (MovieServiceException e) {
            throw new RestControllerException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping(path = "{id}", consumes = "application/json")
    public ResponseEntity<MovieRepresentationalModel> updateMovie(@RequestBody @Valid MoviePostDTO movieDTO, @PathVariable("id") long id, Errors errors) {

        if (this.movieService.findById(id).isEmpty()) {
            throw new RestControllerException(HttpStatus.NOT_FOUND, "Movie not in db. Adapt id or post it to the server");
        }

        // javax validation
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Movie movie = new Movie(id, movieDTO.getTitle(), movieDTO.isWonOscar(),
                    movieDTO.getYear(), movieDTO.getCoverImage(), this.actorService.findActors(movieDTO.getActorIds()),
                    this.filmStudioService.findById(movieDTO.getFilmStudioId()), this.customUserService.findUserByUsername(movieDTO.getUsername()));

            return new ResponseEntity<>(new MovieRepresentationalModelAssembler().toModel(this.movieService.update(movie)), CREATED);
        } catch (MovieServiceException e) {
            throw new RestControllerException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Partial update of a movie. null values indicate that the specific attribute should not be updated.
     */
    @PatchMapping(path = "{id}")
    public ResponseEntity<MovieRepresentationalModel> patch(@RequestBody MoviePostDTO movieDTO, @PathVariable("id") long id, Errors errors) {
        Optional<Movie> movieOptional = this.movieService.findById(id);
        if (movieOptional.isEmpty()) {
            throw new RestControllerException(HttpStatus.NOT_FOUND, "Movie not in db. Adapt id or post it to the server");
        }

        // custom validation here - otherwise framework throws exception before we can handle it
        this.validator.validate(movieDTO, errors);

        Movie movie = movieOptional.get();
        if (movieDTO.getTitle() != null) {
            movie.setTitle(movieDTO.getTitle());
        }

        movie.setWonOscar(movieDTO.isWonOscar());

        if (movieDTO.getYear() != 0) {
            if (errors.getFieldError("year") == null) {
                movie.setYear(movieDTO.getYear());
            } else {
                throw new RestControllerException(HttpStatus.BAD_REQUEST, errors.getFieldError("year").getDefaultMessage());
            }
        }

        if (movieDTO.getCoverImage() != null) {
            if (errors.getFieldError("coverImage") == null) {
                movie.setCoverImage(movieDTO.getCoverImage());
            } else {
                throw new RestControllerException(HttpStatus.BAD_REQUEST, errors.getFieldError("coverImage").getDefaultMessage());
            }
        }

        try {
            if (movieDTO.getActorIds() != null) {
                movie.setActors(this.actorService.findActors(movieDTO.getActorIds()));
            }

            if (movieDTO.getFilmStudioId() != null) {
                movie.setFilmStudio(this.filmStudioService.findById(movieDTO.getFilmStudioId()));
            }

            if (movieDTO.getUsername() != null) {
                movie.setCreatedBy(this.customUserService.findUserByUsername(movieDTO.getUsername()));
            }

            return new ResponseEntity<>(new MovieRepresentationalModelAssembler().toModel(this.movieService.update(movie)), OK);
        } catch (MovieServiceException e) {
            throw new RestControllerException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity deleteMovie(@PathVariable("id") long id) {
        if (this.movieService.findById(id).isEmpty()) {
            throw new RestControllerException(HttpStatus.NOT_FOUND, "Movie not in db. Adapt id or post it to the server");
        }

        this.movieService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({RestControllerException.class})
    public ResponseEntity<String> handleCustomException(RestControllerException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), ex.getStatus());
    }
}
