package de.lion5.spring.dvd.unit.service;

import java.time.LocalDate;

import de.lion5.spring.dvd.model.FilmStudio;
import de.lion5.spring.dvd.repository.FilmStudioRepository;
import de.lion5.spring.dvd.service.FilmStudioService;
import de.lion5.spring.dvd.service.MovieServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FilmStudioServiceUnitTest {

    @Autowired
    private FilmStudioService studioService;

    @Test
    public void findById_nullCheck() {
        assertThrows(MovieServiceException.class, () -> this.studioService.findById(null));
    }

    @Test
    public void findById_notFound() {
        assertThrows(MovieServiceException.class, () -> {
            this.studioService.findById(0L);
        });
    }

    @Test
    @Transactional
    public void findById_saveAndRead(@Autowired FilmStudioRepository studioRepository) throws MovieServiceException {
        FilmStudio studio = studioRepository.saveAndFlush(new FilmStudio(99L, "Warner", LocalDate.now(), null));

        assertEquals(studio, this.studioService.findById(studio.getId()));
    }
}
