package de.lion5.spring.dvd.api.controller;

import java.util.Optional;

import de.lion5.spring.dvd.api.assembler.ActorRepresentationalModelAssembler;
import de.lion5.spring.dvd.api.model.ActorRepresentationalModel;
import de.lion5.spring.dvd.model.Actor;
import de.lion5.spring.dvd.repository.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "v1/actors")
public class ActorRestController {

    private ActorRepository actorRepo;

    @Autowired
    public ActorRestController(ActorRepository actorRepo) {
        this.actorRepo = actorRepo;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<ActorRepresentationalModel>> getActors() {
        return new ResponseEntity<>(new ActorRepresentationalModelAssembler().toCollectionModel(this.actorRepo.findAll()), HttpStatus.OK);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<ActorRepresentationalModel> getActorById(@PathVariable("id") long id) {

        Optional<Actor> actorOptional = this.actorRepo.findById(id);

        if (actorOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(new ActorRepresentationalModelAssembler().toModel(actorOptional.get()), HttpStatus.OK);
    }
}
