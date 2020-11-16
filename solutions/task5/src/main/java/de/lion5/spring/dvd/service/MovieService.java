package de.lion5.spring.dvd.service;

import java.util.Optional;

import de.lion5.spring.dvd.model.Movie;
import de.lion5.spring.dvd.repository.MovieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class MovieService {

    private MovieRepository movieRepo;

    @Autowired
    public MovieService(MovieRepository movieRepo) {
        this.movieRepo = movieRepo;
    }

    public Iterable<Movie> findAll() {
        return this.movieRepo.findAll();
    }

    public Page<Movie> findAll(PageRequest of) {
        return this.movieRepo.findAll(of);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Movie update(Movie movie) {
        log.info("Update movie");
        return this.movieRepo.save(movie);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Movie saveAndSetId(Movie movie) {
        log.info("Start save");

        // First action to the db starts the transaction
        movie.setId(this.movieRepo.count() + 1L);
        Movie storedMovie = this.movieRepo.save(movie);
        log.info(storedMovie.toString());
        // forcing a TransactionSystemException, handled in the REST controller class via exception handler
        // storedMovie.setCoverImage("");
        // the requires new has no effect here (no further transaction will be opened - same class - same proxy)
        log.info(this.findById(1L).get().toString());
        // Return will end the transaction
        return this.movieRepo.save(storedMovie);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Optional<Movie> findById(Long id) {
        return this.movieRepo.findById(id);
    }

    public void deleteById(long id) {
        this.movieRepo.deleteById(id);
    }
}
