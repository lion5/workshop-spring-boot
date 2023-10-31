package de.lion5.spring.dvd.model;

import java.util.List;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@NamedEntityGraph(name = "Movie.movies", // entity graph solution
        attributeNodes = @NamedAttributeNode(value="actors")) // entity graph solution// entity graph solution
public class Movie {
    @Id
    private Long id;
    @NotNull(message = "Title must be set")
    @NotEmpty(message = "Title not there")
    private String title;
    private boolean wonOscar;
    @Min(value = 1920, message = "Movies before 1920 are not considered!")
    @Max(value = 2023, message = "Movies after 2022 are not planned now!")
    private int releaseYear;
    @NotNull
    @Pattern(regexp = "(https:\\/\\/).*\\.(?:jpg|gif|png)", message = "Must be a valid URL to a picture.")
    private String coverImage;
    // default fetch type: LAZY
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="movie_actor", joinColumns=@JoinColumn(name="movie_id"), inverseJoinColumns = @JoinColumn(name="actor_id"))
    private List<Actor> actors;
    // Due to merge, the film studio will be stored when it is not present in the database
    @ManyToOne(cascade = CascadeType.MERGE) // default fetch type: EAGER
    private FilmStudio filmStudio;
}
