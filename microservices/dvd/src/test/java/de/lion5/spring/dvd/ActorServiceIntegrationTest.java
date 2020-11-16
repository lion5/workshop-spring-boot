package de.lion5.spring.dvd;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import de.lion5.spring.dvd.model.Actor;
import de.lion5.spring.dvd.model.Movie;
import de.lion5.spring.dvd.repository.ActorRepository;
import de.lion5.spring.dvd.repository.MovieRepository;
import de.lion5.spring.dvd.service.ActorService;
import de.lion5.spring.dvd.service.MovieService;
import de.lion5.spring.dvd.service.MovieServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ActorServiceIntegrationTest {

    @Autowired
    private ActorService actorService;
    @Autowired
    private MovieService movieService;

    private long actorsInDb;
    private Actor jonny, bruce, kate;
    private Movie pirate;

    @BeforeEach
    public void initDemoData(@Autowired MovieRepository movieRepository, @Autowired ActorRepository actorRepository) {
        actorsInDb = actorRepository.count();
        jonny = actorRepository.save(new Actor(actorsInDb + 1, "Jonny", true, LocalDate.of(2000, 1, 1), null));
        bruce = actorRepository.save(new Actor(actorsInDb + 2, "Bruce", false, LocalDate.of(2001, 1, 1), null));
        kate = actorRepository.save(new Actor(actorsInDb + 3, "Kate", true, LocalDate.of(1999, 1, 1), null));

        pirate = movieRepository.save(new Movie(movieRepository.count() + 1, "Pirates", true, 2000, "https://.png", List.of(bruce, jonny), null, null));
    }

    @Test
    public void findActorsByIds_successfull() throws MovieServiceException {

        List<Actor> actors = this.actorService.findActors(List.of(jonny.getId(), bruce.getId(), kate.getId()));

        assertEquals(3, actors.size());
        assertEquals(1, actors.stream().filter(a -> a.getId().equals(jonny.getId())).count());
    }

    @Test
    public void findActorsByIds_notInDb() throws MovieServiceException {
        assertThrows(MovieServiceException.class, () -> {
            this.actorService.findActors(List.of(jonny.getId(), bruce.getId(), kate.getId() + 1L));
        });
    }

    @Test
    public void findActorsByIds_nullableList() throws MovieServiceException {
        assertEquals(null, this.actorService.findActors(null));
    }

    /**
     * Test run in the same transaction context as the initDemoData operations to the db. Different transaction contexts
     * (due to requires new propagation strategy) might result in unseen changes by some methods (concurrency issues
     * which transaction context commits first etc.).
     */
    @Test
    public void deleteActor_successfull() {

        assertEquals(2, pirate.getActors().size());

        //refresh
        bruce = this.actorService.findById(bruce.getId()).get();
        this.actorService.delete(bruce.getId());

        // refresh content
        Movie pirateUpdated = this.movieService.findById(pirate.getId()).get();
        assertEquals(1, pirateUpdated.getActors().size());

        assertEquals(Optional.empty(), this.actorService.findById(bruce.getId()));
    }

    @Test
    public void deleteActor_noMovies() {

        assertEquals(2, pirate.getActors().size());

        //refresh
        kate = this.actorService.findById(kate.getId()).get();
        this.actorService.delete(kate.getId());

        // refresh content
        Movie pirateUpdated = this.movieService.findById(pirate.getId()).get();
        assertEquals(2, pirateUpdated.getActors().size());

        assertEquals(Optional.empty(), this.actorService.findById(kate.getId()));
    }
}
