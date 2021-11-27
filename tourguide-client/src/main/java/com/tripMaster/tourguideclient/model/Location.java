package com.tripMaster.tourguideclient.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * Class that models {@link Location}
 *
 * @author Christine Duarte
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location {
        private double latitude;
        private double longitude;

        @Override
        public String toString() {
                return "Location{" +
                        "latitude=" + latitude +
                        ", longitude=" + longitude +
                        '}';
        }
}
