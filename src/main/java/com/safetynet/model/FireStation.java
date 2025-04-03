package com.safetynet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Firestation {

    @NotBlank
    private String address;

    @Min(1)
    private int station;

    public Firestation() {
    }

    public Firestation(String address, int station) {
        this.address = address;
        this.station = station;
    }
}
