package de.lion5.spring.dvd.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraph(name = "FilmStudio.studio", // entity graph solution
        attributeNodes = {
                @NamedAttributeNode(value = "movies")
        })
public class FilmStudio extends RepresentationModel<FilmStudio> {
    @Id
    private Long id;
    private String name;
    private LocalDate since;
    @OneToMany(mappedBy = "filmStudio")
    @JsonIgnoreProperties({"actors", "filmStudio"})
    private List<Movie> movies;

    @Override
    public String toString() {
        return "";
    }
}
