package de.lion5.spring.dvd.service;

import java.util.List;
import java.util.Optional;

import de.lion5.spring.dvd.model.FilmStudio;
import de.lion5.spring.dvd.repository.FilmStudioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FilmStudioService {

    private FilmStudioRepository filmStudioRepo;

    @Autowired
    public FilmStudioService(FilmStudioRepository filmStudioRepo) {
        this.filmStudioRepo = filmStudioRepo;
    }

    public FilmStudio findById(Long filmStudioId) throws MovieServiceException {
        if (filmStudioId == null) {
            throw new MovieServiceException("non null values for film studio ids");
        }

        Optional<FilmStudio> studio = this.filmStudioRepo.findById(filmStudioId);

        if (studio.isEmpty()) {
            throw new MovieServiceException("id invalid");
        }

        return studio.get();
    }

    public List<FilmStudio> findAll() {
        return this.filmStudioRepo.findAll();
    }
}
