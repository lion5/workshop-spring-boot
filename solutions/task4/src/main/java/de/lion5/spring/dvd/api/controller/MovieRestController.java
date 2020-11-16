package de.lion5.spring.dvd.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import de.lion5.spring.dvd.api.RestControllerException;
import de.lion5.spring.dvd.api.assembler.MovieRepresentationalModelAssembler;
import de.lion5.spring.dvd.api.dto.MoviePostDTO;
import de.lion5.spring.dvd.api.model.MovieRepresentationalModel;
import de.lion5.spring.dvd.model.Actor;
import de.lion5.spring.dvd.model.FilmStudio;
import de.lion5.spring.dvd.model.Movie;
import de.lion5.spring.dvd.properties.MovieProperties;
import de.lion5.spring.dvd.repository.ActorRepository;
import de.lion5.spring.dvd.repository.FilmStudioRepository;
import de.lion5.spring.dvd.repository.MovieRepository;
import de.lion5.spring.dvd.users.User;
import de.lion5.spring.dvd.users.UserRepository;
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
    private MovieRepository movieRepo;
    private UserRepository userRepo;
    private ActorRepository actorRepo;
    private FilmStudioRepository filmStudioRepo;

    private Validator validator;

    @Autowired
    public MovieRestController(MovieProperties props, MovieRepository movieRepo, UserRepository userRepo, ActorRepository actorRepo, FilmStudioRepository filmStudioRepo, Validator validator) {
        this.props = props;
        this.movieRepo = movieRepo;
        this.userRepo = userRepo;
        this.actorRepo = actorRepo;
        this.filmStudioRepo = filmStudioRepo;
        this.validator = validator;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<MovieRepresentationalModel>> getMovies(@RequestParam(defaultValue = "0") int page) {
        if (page < 0) {
            return ResponseEntity.badRequest().build();
        }
        log.info("Find movies . . .");
        Iterable<Movie> movies = this.movieRepo.findAll(PageRequest.of(page, this.props.getPageSize())).getContent();

        CollectionModel<MovieRepresentationalModel> collection = CollectionModel.of(new MovieRepresentationalModelAssembler().toCollectionModel(movies));
        collection.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MovieRestController.class).getMovies(page)).withRel("movies"));

        return new ResponseEntity<>(collection, OK);
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<MovieRepresentationalModel> findMovie(@PathVariable("id") Long id) {
        Optional<Movie> m = this.movieRepo.findById(id);
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

        Movie movie = new Movie((this.movieRepo.count() + 1L), movieDTO.getTitle(), movieDTO.isWonOscar(),
                movieDTO.getYear(), movieDTO.getCoverImage(), this.getActorList(movieDTO.getActorIds()),
                this.getFilmStudio(movieDTO.getFilmStudioId()), this.getUser(movieDTO.getUsername()));

        return new ResponseEntity<>(new MovieRepresentationalModelAssembler().toModel(this.movieRepo.save(movie)), CREATED);
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<MovieRepresentationalModel> updateMovie(@RequestBody @Valid MoviePostDTO movieDTO, @PathVariable("id") long id, Errors errors) {

        if (this.movieRepo.findById(id).isEmpty()) {
            throw new RestControllerException(HttpStatus.NOT_FOUND, "Movie not in db. Adapt id or post it to the server");
        }

        // javax validation
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        Movie movie = new Movie(id, movieDTO.getTitle(), movieDTO.isWonOscar(),
                movieDTO.getYear(), movieDTO.getCoverImage(), this.getActorList(movieDTO.getActorIds()),
                this.getFilmStudio(movieDTO.getFilmStudioId()), this.getUser(movieDTO.getUsername()));
        return new ResponseEntity<>(new MovieRepresentationalModelAssembler().toModel(this.movieRepo.save(movie)), CREATED);
    }

    /**
     * Partial update of a movie. null values indicate that the specific attribute should not be updated.
     */
    @PatchMapping(path = "{id}")
    public ResponseEntity<MovieRepresentationalModel> patch(@RequestBody MoviePostDTO movieDTO, @PathVariable("id") long id, Errors errors) {
        Optional<Movie> movieOptional = this.movieRepo.findById(id);
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

        if (movieDTO.getActorIds() != null) {
            movie.setActors(this.getActorList(movieDTO.getActorIds()));
        }

        if (movieDTO.getFilmStudioId() != null) {
            movie.setFilmStudio(this.getFilmStudio(movieDTO.getFilmStudioId()));
        }

        if (movieDTO.getUsername() != null) {
            movie.setCreatedBy(this.getUser(movieDTO.getUsername()));
        }

        return new ResponseEntity<>(new MovieRepresentationalModelAssembler().toModel(this.movieRepo.save(movie)), OK);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity deleteMovie(@PathVariable("id") long id) {
        if (this.movieRepo.findById(id).isEmpty()) {
            throw new RestControllerException(HttpStatus.NOT_FOUND, "Movie not in db. Adapt id or post it to the server");
        }

        this.movieRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private User getUser(String userName) {
        if (userName == null) {
            return null;
        }

        User user = this.userRepo.findUserByUsername(userName);
        if (user == null) {
            throw new RestControllerException(HttpStatus.NOT_FOUND, "User not found with this username");
        }
        if (!user.getRole().equals("ROLE_ADMIN")) {
            throw new RestControllerException(HttpStatus.FORBIDDEN, "This user is not allowed to create movies");
        }
        return user;
    }

    private List<Actor> getActorList(List<Long> actorIds) {
        if (actorIds == null) {
            return null;
        }

        List<Actor> actors = new ArrayList<>();
        for (Long actorId : actorIds) {
            Optional<Actor> actor = this.actorRepo.findById(actorId);
            if (actor.isPresent()) {
                actors.add(actor.get());
            } else {
                throw new RestControllerException(HttpStatus.NOT_FOUND, "Actor not found with id: " + actorId);
            }
        }

        return actors;
    }

    private FilmStudio getFilmStudio(Long filmStudioId) {

        if (filmStudioId == null) {
            return null;
        }

        Optional<FilmStudio> studio = this.filmStudioRepo.findById(filmStudioId);

        if (studio.isEmpty()) {
            throw new RestControllerException(HttpStatus.NOT_FOUND, "Film studio not found with given id");
        }

        return studio.get();
    }

    @ExceptionHandler({RestControllerException.class})
    public ResponseEntity<String> handleCustomException(RestControllerException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), ex.getStatus());
    }
}
