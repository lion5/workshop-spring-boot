package de.lion5.spring.dvd.api.model;

import de.lion5.spring.dvd.model.FilmStudio;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

public class ShortFilmStudioRepresentationalModel extends RepresentationModel<ShortFilmStudioRepresentationalModel> {

    @Getter
    private final String name;

    public ShortFilmStudioRepresentationalModel(FilmStudio studio) {
        this.name = studio.getName();
    }
}

