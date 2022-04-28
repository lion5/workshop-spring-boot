package de.lion5.spring.dvd.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    private String id;
    private String title;
    private boolean wonOscar;
    private int year;
    private String coverImage;
    private List<Actor> actors;
}
