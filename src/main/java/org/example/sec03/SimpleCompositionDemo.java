package org.example.sec03;


import org.example.models.sec03.Address;
import org.example.models.sec03.Person;
import org.example.models.sec03.School;
import org.example.models.sec03.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SimpleCompositionDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCompositionDemo.class);
    private static final Path PATH = Path.of("person.out");

    public static void main(String[] args) throws IOException {

        var address = Address.newBuilder()
                .setStreet("16 Ky Con")
                .setCity("HCMC")
                .setState("VN")
                .build();

        var student = Student.newBuilder()
                .setName("Tony")
                .setAddress(address);

        var school = School.newBuilder()
                .setId(1)
                .setAddress(address.toBuilder().setStreet("20 Ky Con"))
                .build();

        LOGGER.info("Student: {}", student);
        LOGGER.info("School: {}", school);


    }

}
