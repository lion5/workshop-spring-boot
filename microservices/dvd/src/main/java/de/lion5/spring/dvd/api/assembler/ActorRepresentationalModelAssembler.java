package de.lion5.spring.dvd.api.assembler;

import de.lion5.spring.dvd.api.controller.ActorRestController;
import de.lion5.spring.dvd.api.model.ActorRepresentationalModel;
import de.lion5.spring.dvd.model.Actor;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.util.Assert;

public class ActorRepresentationalModelAssembler extends RepresentationModelAssemblerSupport<Actor, ActorRepresentationalModel> {

    public ActorRepresentationalModelAssembler() {
        super(ActorRestController.class, ActorRepresentationalModel.class);
    }

    @Override
    protected ActorRepresentationalModel instantiateModel(Actor entity) {
        return new ActorRepresentationalModel(entity);
    }

    @Override
    public ActorRepresentationalModel toModel(Actor entity) {
        Assert.notNull(entity, "Entity must not be null!");
        Assert.notNull(entity.getId(), "Id must not be null!");
        return this.createModelWithId(entity.getId(), entity);
    }
}
