package de.lion5.spring.dvd.demo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import de.lion5.spring.dvd.model.Actor;
import de.lion5.spring.dvd.model.FilmStudio;
import de.lion5.spring.dvd.model.Movie;
import de.lion5.spring.dvd.repository.ActorRepository;
import de.lion5.spring.dvd.repository.FilmStudioRepository;
import de.lion5.spring.dvd.repository.MovieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DemoData {

    private final MovieRepository movieRepo;
    private final ActorRepository actorRepo;
    private final FilmStudioRepository filmStudioRepo;

    @Autowired
    public DemoData(MovieRepository movieRepo, ActorRepository actorRepo, FilmStudioRepository filmStudioRepo) {
        this.movieRepo = movieRepo;
        this.actorRepo = actorRepo;
        this.filmStudioRepo = filmStudioRepo;
    }

    /**
     * We create some demo data here, when the database schema is created and the app is ready.
     *
     * @param event
     */
    @EventListener
    public void createDemoData(ApplicationReadyEvent event) {

        // not storing the film studio directly. It is stored transitively (CASCADE.MERGE) when the first movie is stored
        FilmStudio warner = new FilmStudio(1L, "Warner Bros. Pictures", LocalDate.of(1923, 4,4), null);

        Actor tomHanks = new Actor(1L, "Tom Hanks", true, LocalDate.of(1956,7,9), null);
        Actor diCaprio = new Actor(2L, "Leonardo Di Caprio", true, LocalDate.of(1974,11,11), null);

        // saving two actors
        this.actorRepo.saveAll(Arrays.asList(tomHanks, diCaprio));

        Movie inception = new Movie(1L, "Inception", false, 2010, "https://cdn.pixabay.com/photo/2017/05/15/17/43/calm-2315559_960_720.jpg", new ArrayList<>(Arrays.asList(diCaprio)), warner);
        Movie cloudAtlas = new Movie(2L, "Cloud Atlas", false, 2012,  "https://cdn.pixabay.com/photo/2020/03/02/16/19/vintage-4896141_960_720.jpg", new ArrayList<>(Arrays.asList(tomHanks)),warner);

        // Save movies
        this.movieRepo.saveAll(Arrays.asList(inception,cloudAtlas));

        // relation actor movie not stored, since movie is the owning side of the relation
        Actor halleBerry = new Actor(3L, "Halle Berry", true, LocalDate.of(1966,8,14),Arrays.asList(inception, cloudAtlas));
        // Since actor is the inverse side of the many to many relationship, we must add halle berry to the owning side and store she.
        // If nothing happens, you forgot to enable the CASCADE.MERGE on movies at actor class
        // or whatever setting you choose (in this case, it is risky, since you get a DataIntegrityViolationException when
        // you try to remove halle berry, since you do not delete the mapping in the table.
        // Use CASCADE.ALL in this case!!
        inception.getActors().add(halleBerry);
        cloudAtlas.getActors().add(halleBerry);
        this.actorRepo.save(halleBerry);

        // the following print statements are for playing around with entity graphs and spring data JPA
        // i.e. try lazy and eager fetch types and their impacts
        this.printActor(3L);
        this.printMovie(2L);
        this.printAllMovies();

        // remove halle
        // first remove it from the owning side of the many to many relation
        // then use the CASCADE.MERGE property to update the movies and delete halle from the mapping list
        for(Movie m : halleBerry.getMovies()){
            m.getActors().remove(halleBerry);
        }
        // this.actorRepo.save(halleBerry); // uncomment the delete and save halle here and check it
        this.actorRepo.delete(halleBerry);

    }

    private void printAllMovies() {
        for(Movie m : this.movieRepo.findAll()){
            this.printMovieToLog(m);
        }
    }

    private void printMovie(long l) {
        Optional<Movie> m = this.movieRepo.findById(l);
        if(m.isPresent()){
            Movie movie = m.get();
            this.printMovieToLog(movie);
        }
    }

    private void printMovieToLog(Movie movie) {
        log.info("Print movie: " + movie.getTitle());
        for(Actor a : movie.getActors()){
            log.info("\t famous actor: " + a.getName() );
        }
    }

    private void printActor(Long id) {
        Optional<Actor> a = this.actorRepo.findById(id);
        if(a.isPresent()){
            Actor actor = a.get();
            log.info("Print actor: " + actor.getName());
            for(Movie m : actor.getMovies()) {
                log.info("\t played in movie: " + m.getTitle() + " film studio: " + m.getFilmStudio().getName());
            }
        }
    }
}
