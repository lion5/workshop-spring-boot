package de.lion5.spring.dvd.unit.service;

import java.util.Optional;

import de.lion5.spring.dvd.model.Movie;
import de.lion5.spring.dvd.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MovieServiceUnitTest {

    @Autowired
    private MovieService movieService;

    @Test
    @Transactional
    public void testCorrectIdHandlingForNewInsertedMovies() {
        // for test purposes ok - id handling of the db
        int elementsInDb = 0;
        for (Movie m : this.movieService.findAll()) {
            elementsInDb++;
        }
        Movie m = movieService.saveAndSetId(new Movie("My Movie", false, 2003, "https://.png", null, null, null));

        assertEquals(elementsInDb + 1, m.getId());
        /*
         * The following assertion will result in an error 'java.util.NoSuchElementException: No value present'.
         * Our transactional test method creates a new managed transaction, which is rolled back after the execution
         * of the transaction (normally when exiting the method as you know).
         * But there might be cases, where you start another managed transaction. The result is, that the framework
         * roles back the started transaction and then executes the next one. In cases where the second transaction
         * needs the result from the first, the test will fail, since the data is already rolled back.
         * In our case, this error is artificially created, since findById declares as propagation strategy to start
         * a new transaction (which makes no sense - other then wasting time and resources and showing the error).
         */
        //assertEquals("My Movie", this.movieService.findById(m.getId()).get().getTitle());
    }

    @Test
    @Transactional
    public void testUpdateOfMovie() {
        Movie m = movieService.saveAndSetId(new Movie("My Movie", false, 2003, "https://.png", null, null, null));
        m.setTitle("Another title");
        m.setYear(2004);

        //update
        Movie updated = movieService.update(m);

        assertEquals(m.getId(), updated.getId());
        assertEquals("Another title", updated.getTitle());
        assertEquals(2004, updated.getYear());
    }

    @Test
    @Transactional
    public void testDelete() {
        Movie m = movieService.saveAndSetId(new Movie("My Movie", false, 2003, "https://.png", null, null, null));
        this.movieService.deleteById(m.getId());
        assertEquals(Optional.empty(), this.movieService.findById(m.getId()));
    }
}
