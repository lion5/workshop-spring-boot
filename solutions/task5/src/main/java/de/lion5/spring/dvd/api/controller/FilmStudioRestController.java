package de.lion5.spring.dvd.api.controller;

import de.lion5.spring.dvd.api.assembler.FilmStudioAssembler;
import de.lion5.spring.dvd.api.assembler.FilmStudioRepresentationalModelAssembler;
import de.lion5.spring.dvd.api.model.FilmStudioRepresentationalModel;
import de.lion5.spring.dvd.service.FilmStudioService;
import de.lion5.spring.dvd.service.MovieServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
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

    private FilmStudioService filmStudioService;

    @Autowired
    public FilmStudioRestController(FilmStudioService filmStudioService) {
        this.filmStudioService = filmStudioService;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<FilmStudioRepresentationalModel>> getStudios() {
        return new ResponseEntity<>(new FilmStudioAssembler().toCollectionModel(this.filmStudioService.findAll()), OK);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<FilmStudioRepresentationalModel> getStudioById(@PathVariable("id") long id) {
        try {
            return new ResponseEntity<>(new FilmStudioRepresentationalModelAssembler().toModel(this.filmStudioService.findById(id)), OK);
        } catch (MovieServiceException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
