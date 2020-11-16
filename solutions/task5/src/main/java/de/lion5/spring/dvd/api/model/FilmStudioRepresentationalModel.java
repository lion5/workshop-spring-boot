package de.lion5.spring.dvd.api.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import de.lion5.spring.dvd.api.assembler.ShortMovieRepresentationalModelAssembler;
import de.lion5.spring.dvd.model.FilmStudio;
import de.lion5.spring.dvd.model.Movie;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Relation(value = "studio", collectionRelation = "studios")
public class FilmStudioRepresentationalModel extends RepresentationModel<FilmStudioRepresentationalModel> {

    @Getter
    private final String name;

    @Getter
    private final LocalDate since;

    @Getter
    private final List<ShortMovieRepresentationalModel> movies;

    public FilmStudioRepresentationalModel(FilmStudio filmStudio) {
        this.name = filmStudio.getName();
        this.since = filmStudio.getSince();
        this.movies = new ArrayList<>();
        if (filmStudio.getMovies() != null) {
            for (Movie m : filmStudio.getMovies()) {
                this.movies.add(new ShortMovieRepresentationalModelAssembler().toModel(m));
            }
        }
    }
}

