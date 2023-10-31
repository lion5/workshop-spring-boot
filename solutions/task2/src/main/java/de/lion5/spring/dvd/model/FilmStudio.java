package de.lion5.spring.dvd.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
