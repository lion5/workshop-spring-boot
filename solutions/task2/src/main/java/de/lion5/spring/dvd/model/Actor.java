package de.lion5.spring.dvd.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@NamedEntityGraph(name = "Actor.movies", // entity graph solution
    attributeNodes = {
        @NamedAttributeNode(value = "movies", subgraph = "subgraph.filmStudio")
    },
    subgraphs = {
        @NamedSubgraph(name = "subgraph.filmStudio",
            attributeNodes = {
                @NamedAttributeNode(value = "filmStudio")
            })}
) // entity graph solution
public class Actor {

  @Id
  private Long id;
  private String name;
  private boolean wonOscar;
  private LocalDate birthday;
  // default fetch type: LAZY
  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "actors", cascade = {MERGE, PERSIST})
  private List<Movie> movies;
}
