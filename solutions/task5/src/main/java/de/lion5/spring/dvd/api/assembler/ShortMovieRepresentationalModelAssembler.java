package de.lion5.spring.dvd.api.assembler;

import de.lion5.spring.dvd.api.controller.MovieRestController;
import de.lion5.spring.dvd.api.model.ShortMovieRepresentationalModel;
import de.lion5.spring.dvd.model.Movie;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.util.Assert;

public class ShortMovieRepresentationalModelAssembler extends RepresentationModelAssemblerSupport<Movie, ShortMovieRepresentationalModel> {
    public ShortMovieRepresentationalModelAssembler() {
        super(MovieRestController.class, ShortMovieRepresentationalModel.class);
    }

    @Override
    protected ShortMovieRepresentationalModel instantiateModel(Movie entity) {
        return new ShortMovieRepresentationalModel(entity);
    }

    @Override
    public ShortMovieRepresentationalModel toModel(Movie entity) {
        Assert.notNull(entity, "Entity must not be null!");
        Assert.notNull(entity.getId(), "Id must not be null!");
        return this.createModelWithId(entity.getId(), entity);
    }
}
