package de.lion5.spring.dvd.unit.service;

import java.util.List;

import de.lion5.spring.dvd.model.Actor;
import de.lion5.spring.dvd.service.ActorService;
import de.lion5.spring.dvd.service.MovieServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ActorServiceUnitTest {

    @Autowired
    private ActorService actorService;

    @Test
    @Transactional
    public void testCorrectIdHandlingForNewInsertedActors() {
        int elementsInDb = 0;
        for (Actor a : this.actorService.findAll()) {
            elementsInDb++;
        }

        Actor actor = this.actorService.saveAndSetId(new Actor());

        assertEquals(elementsInDb + 1, actor.getId());
    }

    @Test
    @Transactional
    public void findActorsByIds_notFound() {
        assertThrows(MovieServiceException.class, () -> {
            this.actorService.findActors(List.of(0L));
        });
    }
}
