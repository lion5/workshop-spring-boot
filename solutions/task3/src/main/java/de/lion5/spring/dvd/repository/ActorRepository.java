package de.lion5.spring.dvd.repository;

import java.util.Optional;

import de.lion5.spring.dvd.model.Actor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

public interface ActorRepository extends CrudRepository<Actor, Long> {

    @Override
    @EntityGraph(value="Actor.movies") // entity graph solution
    Optional<Actor> findById(Long aLong);
}
