package de.lion5.spring.dvd.api.model;

import java.util.ArrayList;
import java.util.List;

import de.lion5.spring.dvd.api.assembler.ShortActorRepresentationalModelAssembler;
import de.lion5.spring.dvd.api.assembler.ShortFilmStudioRepresentationalModelAssembler;
import de.lion5.spring.dvd.model.Actor;
import de.lion5.spring.dvd.model.Movie;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Relation(value = "movie", collectionRelation = "movies")
public class MovieRepresentationalModel extends RepresentationModel<MovieRepresentationalModel> {

    @Getter
    private final String title;

    @Getter
    private final boolean wonOscar;

    @Getter
    private final int year;

    @Getter
    private final String coverImage;

    @Getter
    private final List<ShortActorRepresentationalModel> actors;

    @Getter
    private final ShortFilmStudioRepresentationalModel filmStudio;

    @Getter
    private final String username;

    public MovieRepresentationalModel(Movie movie) {
        this.title = movie.getTitle();
        this.wonOscar = movie.isWonOscar();
        this.year = movie.getYear();
        this.coverImage = movie.getCoverImage();
        this.actors = new ArrayList<>();
        if (movie.getActors() != null) {
            ShortActorRepresentationalModelAssembler assembler = new ShortActorRepresentationalModelAssembler();
            for (Actor a : movie.getActors()) {
                this.actors.add(assembler.toModel(a));
            }
        }
        if (movie.getFilmStudio() != null) {
            this.filmStudio = new ShortFilmStudioRepresentationalModelAssembler().toModel(movie.getFilmStudio());
        } else {
            this.filmStudio = null;
        }
        if (movie.getCreatedBy() != null) {
            this.username = movie.getCreatedBy().getUsername();
        } else {
            this.username = null;
        }
    }
}

