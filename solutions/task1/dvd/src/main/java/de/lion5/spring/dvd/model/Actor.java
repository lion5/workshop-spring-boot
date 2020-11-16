package de.lion5.spring.dvd.model;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Actor {
    private String id;
    private String name;
    private boolean wonOscar;
    private LocalDate birthday;
}
