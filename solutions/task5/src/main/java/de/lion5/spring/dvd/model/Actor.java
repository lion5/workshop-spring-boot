package de.lion5.spring.dvd.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import static jakarta.persistence.CascadeType.*;


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
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "actors", cascade = {MERGE, PERSIST, DETACH})
    @JsonBackReference
    private List<Movie> movies;
}
