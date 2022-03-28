package dev.yoon.webclient.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@ToString
@AllArgsConstructor
public class CarDto {
    private Long id;
    private String uid;
    private String vin;
    private String makeAndModel;
    private String color;
    private String transmission;
    private String driveType;
    private String fuelType;
    private String carType;
    private List<String> carOptions;
    private List<String> specs;
    private Long doors;
    private Long mileage;
    private Long kilometrage;
    private String licensePlate;

    public CarDto() {
        this.carOptions = new ArrayList<>();
        this.specs = new ArrayList<>();
    }


}
