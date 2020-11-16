package de.lion5.spring.dvd.api.controller;

import java.util.Optional;

import de.lion5.spring.dvd.api.RestControllerException;
import de.lion5.spring.dvd.api.assembler.FilmStudioAssembler;
import de.lion5.spring.dvd.api.assembler.FilmStudioRepresentationalModelAssembler;
import de.lion5.spring.dvd.api.model.FilmStudioRepresentationalModel;
import de.lion5.spring.dvd.model.FilmStudio;
import de.lion5.spring.dvd.repository.FilmStudioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping(path = "/v1/studios",
        produces = "application/json")
@CrossOrigin("*")
public class FilmStudioRestController {

    private FilmStudioRepository filmStudioRepo;

    @Autowired
    public FilmStudioRestController(FilmStudioRepository filmStudioRepo) {
        this.filmStudioRepo = filmStudioRepo;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<FilmStudio>> getStudios() {
        return new ResponseEntity<>(new FilmStudioAssembler().toCollectionModel(this.filmStudioRepo.findAll()), OK);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<FilmStudioRepresentationalModel> getStudioById(@PathVariable("id") long id) {
        Optional<FilmStudio> studioOptional = this.filmStudioRepo.findById(id);
        if (studioOptional.isEmpty()) {
            throw new RestControllerException(HttpStatus.NOT_FOUND, "");
        }

        return new ResponseEntity<>(new FilmStudioRepresentationalModelAssembler().toModel(studioOptional.get()), OK);
    }
}
