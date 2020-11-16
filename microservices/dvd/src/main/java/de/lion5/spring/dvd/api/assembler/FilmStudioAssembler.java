package de.lion5.spring.dvd.api.assembler;

import de.lion5.spring.dvd.api.controller.FilmStudioRestController;
import de.lion5.spring.dvd.api.model.FilmStudioRepresentationalModel;
import de.lion5.spring.dvd.model.FilmStudio;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class FilmStudioAssembler extends RepresentationModelAssemblerSupport<FilmStudio, FilmStudioRepresentationalModel> {

    public FilmStudioAssembler() {
        super(FilmStudioRestController.class, FilmStudioRepresentationalModel.class);
    }

    @Override
    protected FilmStudioRepresentationalModel instantiateModel(FilmStudio entity) {
        return new FilmStudioRepresentationalModel(entity);
    }

    @Override
    public FilmStudioRepresentationalModel toModel(FilmStudio entity) {
        return this.createModelWithId(entity.getId(), entity);
    }
}
