package de.lion5.spring.sample.di.model;

import org.springframework.beans.factory.annotation.Value;

public class Bicycle implements Vehicle {

    @Value("${vehicle.wheels}")
    private int wheels;

    @Override
    public String getWheelInfo() {
        return "This is a bicycle construction, where we build vehicles with " + this.wheels + " wheels";
    }
}
