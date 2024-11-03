package de.lion5.spring.dvd.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class FilmStudio {
    private String id;
    private String name;
    private LocalDate since;
    private List<Movie> movies;
}
