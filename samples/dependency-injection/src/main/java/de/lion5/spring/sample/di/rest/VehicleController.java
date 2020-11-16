package de.lion5.spring.sample.di.rest;

import de.lion5.spring.sample.di.model.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "vehicle")
public class VehicleController {

    private Vehicle vehicle;

    @Autowired
    public VehicleController(Vehicle vehicle){
        this.vehicle = vehicle;
    }

    @GetMapping
    public String getInfo(){
        return vehicle.getWheelInfo();
    }
}
