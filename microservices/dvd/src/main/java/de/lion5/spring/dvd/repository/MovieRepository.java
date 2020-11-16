package de.lion5.spring.dvd.repository;

import java.util.List;
import java.util.Optional;

import de.lion5.spring.dvd.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Override
    @EntityGraph(value = "Movie.movies")
// entity graph solution
    Optional<Movie> findById(Long aLong);

    @Override
    @EntityGraph(value = "Movie.movies")
// entity graph solution
    List<Movie> findAll();

    @Override
    @EntityGraph(value = "Movie.movies")
// entity graph solution
    Page<Movie> findAll(Pageable pageable);
}
