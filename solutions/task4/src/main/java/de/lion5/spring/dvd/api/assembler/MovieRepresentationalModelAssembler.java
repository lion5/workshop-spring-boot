package de.lion5.spring.dvd.api.assembler;

import de.lion5.spring.dvd.api.controller.MovieRestController;
import de.lion5.spring.dvd.api.model.MovieRepresentationalModel;
import de.lion5.spring.dvd.model.Movie;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.util.Assert;

public class MovieRepresentationalModelAssembler extends RepresentationModelAssemblerSupport<Movie, MovieRepresentationalModel> {

    public MovieRepresentationalModelAssembler() {
        // use MovieRestController as the base path for link creation
        super(MovieRestController.class, MovieRepresentationalModel.class);
    }

    @Override
    protected MovieRepresentationalModel instantiateModel(Movie entity) {
        return new MovieRepresentationalModel(entity);
    }

    @Override
    public MovieRepresentationalModel toModel(Movie entity) {
        Assert.notNull(entity, "Entity must not be null!");
        Assert.notNull(entity.getId(), "Id must not be null!");
        return this.createModelWithId(entity.getId(), entity);
    }
}
