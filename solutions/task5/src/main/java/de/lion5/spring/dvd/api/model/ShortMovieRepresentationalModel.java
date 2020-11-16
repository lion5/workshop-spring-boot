package de.lion5.spring.dvd.api.model;

import de.lion5.spring.dvd.model.Movie;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

public class ShortMovieRepresentationalModel extends RepresentationModel<ShortMovieRepresentationalModel> {

    @Getter
    private final String title;
    @Getter
    private final int year;

    public ShortMovieRepresentationalModel(Movie entity) {
        this.title = entity.getTitle();
        this.year = entity.getYear();
    }
}

