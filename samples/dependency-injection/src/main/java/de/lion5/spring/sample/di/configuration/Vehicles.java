package de.lion5.spring.sample.di.configuration;

import de.lion5.spring.sample.di.model.Bicycle;
import de.lion5.spring.sample.di.model.Car;
import de.lion5.spring.sample.di.model.Tricycle;
import de.lion5.spring.sample.di.model.Vehicle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class Vehicles {

    @Bean
    @Profile("default")
    public Vehicle getBicycle(){
        return new Bicycle();
    }

    @Bean
    @Profile("dev")
    public Vehicle getTricycle(){
        return new Tricycle();
    }

    @Bean
    @Profile("prod")
    public Vehicle getCar(){
        return new Car();
    }
}
