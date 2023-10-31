package de.lion5.spring.dvd.api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MoviePostDTO {
    @NotNull(message = "Title must be set")
    @NotEmpty(message = "Title not there")
    private String title;
    private boolean wonOscar;
    @Min(value = 1920, message = "Movies before 1920 are not considered!")
    @Max(value = 2022, message = "Movies after 2022 are not planned now!")
    private int year;
    @NotNull
    @Pattern(regexp = "(https:\\/\\/).*\\.(?:jpg|gif|png)", message = "Must be a valid URL to a picture.")
    private String coverImage;
    // default fetch type: LAZY
    private List<Long> actorIds;
    private Long filmStudioId;
    private String username;
}