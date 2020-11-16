package de.lion5.spring.dvd.repository;

import java.util.Optional;

import de.lion5.spring.dvd.model.Movie;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

public interface MovieRepository extends CrudRepository<Movie, Long> {

    @Override
    @EntityGraph(value="Movie.movies") // entity graph solution
    Optional<Movie> findById(Long aLong);

    @Override
    @EntityGraph(value="Movie.movies") // entity graph solution
    Iterable<Movie> findAll();
}
