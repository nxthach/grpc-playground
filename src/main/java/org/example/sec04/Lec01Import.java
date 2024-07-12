package org.example.sec04;

import org.example.models.common.Address;
import org.example.models.common.BodyStyle;
import org.example.models.common.Car;
import org.example.models.sec04.Person;
import org.example.sec03.SimpleCompositionDemo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lec01Import {

    private static final Logger LOGGER = LoggerFactory.getLogger(Lec01Import.class);

    public static void main(String[] args) {
        var person = Person.newBuilder()
                .setName("Tony")
                .setAge(34)
                .setCar(Car.newBuilder()
                        .setBodyStyle(BodyStyle.COUPE)
                        .build())
                .setAddress(Address.newBuilder()
                        .setCity("HCMC")
                        .build())
                .build();

        LOGGER.info("{}", person);
    }

}
