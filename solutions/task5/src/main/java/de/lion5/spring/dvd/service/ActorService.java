package de.lion5.spring.dvd.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.lion5.spring.dvd.model.Actor;
import de.lion5.spring.dvd.model.Movie;
import de.lion5.spring.dvd.repository.ActorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ActorService {

    private ActorRepository actorRepository;

    @Autowired
    private MovieService movieService;

    @Autowired
    public ActorService(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Optional<Actor> findById(Long actorId) {
        return this.actorRepository.findById(actorId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Actor saveAndSetId(Actor actor) {
        actor.setId(this.actorRepository.count() + 1L);
        System.out.println(actor.getId());
        Actor a = this.actorRepository.save(actor);
        return this.actorRepository.save(a);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Actor> findActors(List<Long> actorIds) throws MovieServiceException {
        if (actorIds == null) {
            return null;
        }

        List<Actor> actors = new ArrayList<>();
        for (Long actorId : actorIds) {
            Optional<Actor> actor = this.actorRepository.findById(actorId);
            if (actor.isPresent()) {
                actors.add(actor.get());
            } else {
                throw new MovieServiceException("Actor not found with id: " + actorId);
            }
        }

        return actors;
    }

    public List<Actor> findAll() {
        return this.actorRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(long id) {
        log.info("Delete Actor");
        Optional<Actor> actorOptional = this.actorRepository.findById(id);
        if (actorOptional.isPresent()) {
            Actor actor = actorOptional.get();
            log.info("Remove the actor from all movies");
            for (Movie m : actor.getMovies()) {
                m.getActors().remove(actor);
                this.movieService.update(m);
            }
        }
        log.info("Delete actor finally");
        this.actorRepository.deleteById(id);
    }
}
