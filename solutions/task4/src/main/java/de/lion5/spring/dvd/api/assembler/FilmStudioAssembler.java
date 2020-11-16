package de.lion5.spring.dvd.api.assembler;

import de.lion5.spring.dvd.api.controller.FilmStudioRestController;
import de.lion5.spring.dvd.model.FilmStudio;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class FilmStudioAssembler extends RepresentationModelAssemblerSupport<FilmStudio, FilmStudio> {

    public FilmStudioAssembler() {
        super(FilmStudioRestController.class, FilmStudio.class);
    }

    @Override
    protected FilmStudio instantiateModel(FilmStudio entity) {
        return entity;
    }

    @Override
    public FilmStudio toModel(FilmStudio entity) {
        return this.createModelWithId(entity.getId(), entity);
    }
}
