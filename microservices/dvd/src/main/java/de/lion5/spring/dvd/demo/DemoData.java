package de.lion5.spring.dvd.demo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

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
     */
    @EventListener
    public void createDemoData(ApplicationReadyEvent event) {

        // not storing the film studio directly. It is stored transitively (CASCADE.MERGE) when the first movie is stored
        FilmStudio warner = new FilmStudio(1L, "Warner Bros. Pictures", LocalDate.of(1923, 4, 4), null);

        Actor tomHanks = new Actor(1L, "Tom Hanks", true, LocalDate.of(1956, 7, 9), null);
        Actor diCaprio = new Actor(2L, "Leonardo Di Caprio", true, LocalDate.of(1974, 11, 11), null);

        // saving two actors
        this.actorRepo.saveAll(Arrays.asList(tomHanks, diCaprio));

        Movie inception = new Movie(1L, "Inception", false, 2010, "https://cdn.pixabay.com/photo/2017/05/15/17/43/calm-2315559_960_720.jpg", new ArrayList<>(Arrays.asList(diCaprio)), warner, null);
        Movie cloudAtlas = new Movie(2L, "Cloud Atlas", false, 2012, "https://cdn.pixabay.com/photo/2020/03/02/16/19/vintage-4896141_960_720.jpg", new ArrayList<>(Arrays.asList(tomHanks)), warner, null);

        // Save movies
        this.movieRepo.saveAll(Arrays.asList(inception, cloudAtlas));

        // relation actor movie not stored, since movie is the owning side of the relation
        Actor halleBerry = new Actor(3L, "Halle Berry", true, LocalDate.of(1966, 8, 14), Arrays.asList(inception, cloudAtlas));
        inception.getActors().add(halleBerry);
        cloudAtlas.getActors().add(halleBerry);
        this.actorRepo.save(halleBerry);
    }
}
