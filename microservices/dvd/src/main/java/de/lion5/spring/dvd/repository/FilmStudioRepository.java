package de.lion5.spring.dvd.repository;

import java.util.Optional;

import de.lion5.spring.dvd.model.FilmStudio;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmStudioRepository extends JpaRepository<FilmStudio, Long> {

    @Override
    @EntityGraph(value = "FilmStudio.studio")
    Optional<FilmStudio> findById(Long aLong);
}
