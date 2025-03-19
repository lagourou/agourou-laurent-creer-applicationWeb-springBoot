package com.safetynet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Firestation {
    private String address;
    private int station;

    public Firestation() {
    }

    public Firestation(String address, int station) {
        this.address = address;
        this.station = station;
    }
}
