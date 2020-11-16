package de.lion5.spring.dvd.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class FilmStudio {
    @Id
    private Long id;
    private String name;
    private LocalDate since;
    @OneToMany(mappedBy = "filmStudio")
    private List<Movie> movies;
}
