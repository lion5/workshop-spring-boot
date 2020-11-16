package de.lion5.spring.sample.di.model;

import org.springframework.beans.factory.annotation.Value;

public class Tricycle implements Vehicle {

    @Value("${vehicle.wheels}")
    private int wheels;

    @Override
    public String getWheelInfo() {
        return "This is a tricycle construction, where we build vehicles with " + this.wheels + " wheels";
    }
}
