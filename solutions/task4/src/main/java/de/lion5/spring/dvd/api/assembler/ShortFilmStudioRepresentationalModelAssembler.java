package de.lion5.spring.dvd.api.assembler;

import de.lion5.spring.dvd.api.controller.FilmStudioRestController;
import de.lion5.spring.dvd.api.model.ShortFilmStudioRepresentationalModel;
import de.lion5.spring.dvd.model.FilmStudio;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.util.Assert;

public class ShortFilmStudioRepresentationalModelAssembler extends RepresentationModelAssemblerSupport<FilmStudio, ShortFilmStudioRepresentationalModel> {

    public ShortFilmStudioRepresentationalModelAssembler() {
        super(FilmStudioRestController.class, ShortFilmStudioRepresentationalModel.class);
    }

    @Override
    protected ShortFilmStudioRepresentationalModel instantiateModel(FilmStudio entity) {
        return new ShortFilmStudioRepresentationalModel(entity);
    }

    @Override
    public ShortFilmStudioRepresentationalModel toModel(FilmStudio entity) {
        Assert.notNull(entity, "Entity must not be null!");
        Assert.notNull(entity.getId(), "Id must not be null!");
        return this.createModelWithId(entity.getId(), entity);
    }
}
