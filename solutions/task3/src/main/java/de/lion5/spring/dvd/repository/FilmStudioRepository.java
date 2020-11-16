package de.lion5.spring.dvd.repository;

import de.lion5.spring.dvd.model.FilmStudio;
import org.springframework.data.repository.CrudRepository;

public interface FilmStudioRepository extends CrudRepository<FilmStudio, Long> {
}
