package de.lion5.spring.dvd.api.controller;

import java.util.Optional;

import de.lion5.spring.dvd.api.assembler.ActorRepresentationalModelAssembler;
import de.lion5.spring.dvd.api.model.ActorRepresentationalModel;
import de.lion5.spring.dvd.model.Actor;
import de.lion5.spring.dvd.service.ActorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "v1/actors")
public class ActorRestController {

    private ActorService actorService;

    @Autowired
    public ActorRestController(ActorService actorService) {
        this.actorService = actorService;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<ActorRepresentationalModel>> getActors() {
        return new ResponseEntity<>(new ActorRepresentationalModelAssembler().toCollectionModel(this.actorService.findAll()), HttpStatus.OK);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<ActorRepresentationalModel> getActorById(@PathVariable("id") long id) {
        log.info("Get actor with id " + id);
        Optional<Actor> actorOptional = this.actorService.findById(id);

        if (actorOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(new ActorRepresentationalModelAssembler().toModel(actorOptional.get()), HttpStatus.OK);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity deleteActor(@PathVariable("id") long id) {
        if (this.actorService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        this.actorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
