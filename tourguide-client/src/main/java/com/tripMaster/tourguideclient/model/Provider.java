package com.tripMaster.tourguideclient.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Class that models {@link Provider}
 * providers that offer trip deals
 *
 * @author Christine Duarte
 */
@AllArgsConstructor
@Getter
@Setter
public class Provider {
    private final UUID tripId;
    private final String name;
    private final double price;


    @Override
    public String toString() {
        return "Provider{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", tripId=" + tripId +
                '}';
    }
}
