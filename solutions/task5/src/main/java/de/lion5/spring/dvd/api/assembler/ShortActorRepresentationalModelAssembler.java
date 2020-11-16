package de.lion5.spring.dvd.api.assembler;

import de.lion5.spring.dvd.api.controller.ActorRestController;
import de.lion5.spring.dvd.api.model.ShortActorRepresentationalModel;
import de.lion5.spring.dvd.model.Actor;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.util.Assert;

public class ShortActorRepresentationalModelAssembler extends RepresentationModelAssemblerSupport<Actor, ShortActorRepresentationalModel> {

    public ShortActorRepresentationalModelAssembler() {
        super(ActorRestController.class, ShortActorRepresentationalModel.class);
    }

    @Override
    protected ShortActorRepresentationalModel instantiateModel(Actor entity) {
        return new ShortActorRepresentationalModel(entity);
    }

    @Override
    public ShortActorRepresentationalModel toModel(Actor entity) {
        Assert.notNull(entity, "Entity must not be null!");
        Assert.notNull(entity.getId(), "Id must not be null!");
        return this.createModelWithId(entity.getId(), entity);
    }
}
