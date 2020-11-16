package de.lion5.spring.dvd.api.model;

import de.lion5.spring.dvd.model.Actor;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

public class ShortActorRepresentationalModel extends RepresentationModel<ShortActorRepresentationalModel> {

    @Getter
    private final String name;

    public ShortActorRepresentationalModel(Actor actor) {
        this.name = actor.getName();
    }
}
