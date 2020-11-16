package de.lion5.spring.dvd.api.assembler;

import de.lion5.spring.dvd.api.controller.FilmStudioRestController;
import de.lion5.spring.dvd.api.model.FilmStudioRepresentationalModel;
import de.lion5.spring.dvd.model.FilmStudio;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.util.Assert;

public class FilmStudioRepresentationalModelAssembler extends RepresentationModelAssemblerSupport<FilmStudio, FilmStudioRepresentationalModel> {

    public FilmStudioRepresentationalModelAssembler() {
        super(FilmStudioRestController.class, FilmStudioRepresentationalModel.class);
    }

    @Override
    protected FilmStudioRepresentationalModel instantiateModel(FilmStudio entity) {
        return new FilmStudioRepresentationalModel(entity);
    }

    @Override
    public FilmStudioRepresentationalModel toModel(FilmStudio entity) {
        Assert.notNull(entity, "Entity must not be null!");
        Assert.notNull(entity.getId(), "Id must not be null!");
        return this.createModelWithId(entity.getId(), entity);
    }
}
