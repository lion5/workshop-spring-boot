package de.lion5.spring.dvd.repository;

import java.util.Optional;

import de.lion5.spring.dvd.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MovieRepository extends PagingAndSortingRepository<Movie, Long> {

    @Override
    @EntityGraph(value="Movie.movies") // entity graph solution
    Optional<Movie> findById(Long aLong);

    @Override
    @EntityGraph(value="Movie.movies") // entity graph solution
    Iterable<Movie> findAll();

    @Override
    @EntityGraph(value="Movie.movies") // entity graph solution
    Page<Movie> findAll(Pageable pageable);
}
