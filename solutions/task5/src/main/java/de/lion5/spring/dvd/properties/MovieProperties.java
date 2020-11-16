package de.lion5.spring.dvd.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConfigurationProperties(prefix = "movies")
@Data
public class MovieProperties {

    // 3 is the default value, if no property is specified in the application context
    private int pageSize = 3;
}
