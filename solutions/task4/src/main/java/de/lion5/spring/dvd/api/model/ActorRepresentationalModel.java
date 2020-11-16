package de.lion5.spring.dvd.api.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import de.lion5.spring.dvd.api.assembler.ShortMovieRepresentationalModelAssembler;
import de.lion5.spring.dvd.model.Actor;
import de.lion5.spring.dvd.model.Movie;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

public class ActorRepresentationalModel extends RepresentationModel<ActorRepresentationalModel> {

    @Getter
    private final String name;

    @Getter
    private final boolean wonOscar;

    @Getter
    private final LocalDate birthday;

    @Getter
    private final List<ShortMovieRepresentationalModel> movies;

    public ActorRepresentationalModel(Actor actor) {
        this.name = actor.getName();
        this.wonOscar = actor.isWonOscar();
        this.birthday = actor.getBirthday();

        this.movies = new ArrayList<>();
        if (actor.getMovies() != null) {
            for (Movie m : actor.getMovies()) {
                this.movies.add(new ShortMovieRepresentationalModelAssembler().toModel(m));
            }
        }
    }
}

