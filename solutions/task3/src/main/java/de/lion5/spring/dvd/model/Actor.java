package de.lion5.spring.dvd.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@NamedEntityGraph(name = "Actor.movies", // entity graph solution
    attributeNodes = @NamedAttributeNode("movies")) // entity graph solution
public class Actor {
    @Id
    private Long id;
    private String name;
    private boolean wonOscar;
    private LocalDate birthday;
    // default fetch type: LAZY
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "actors", cascade={MERGE,PERSIST})
    private List<Movie> movies;
}
