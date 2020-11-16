package de.lion5.spring.dvd.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

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
    @JsonBackReference
    private List<Movie> movies;
}
