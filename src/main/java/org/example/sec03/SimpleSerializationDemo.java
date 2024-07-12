package org.example.sec03;


import org.example.models.sec03.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SimpleSerializationDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSerializationDemo.class);
    private static final Path PATH = Path.of("person.out");

    public static void main(String[] args) throws IOException {

        var person = getPerson();
        LOGGER.info("{}", person);

        serialize(person);
        LOGGER.info("{}", deserialize());
        LOGGER.info("bytes length : {}", person.toByteArray().length);

    }

    private static Person getPerson() {
        return Person.newBuilder()
                .setLastName("Nguyen")
                .setAge(34)
                .setEmail("nxthach@gmail.com")
                .setEmployed(true)
                .setBankAccountNumber(123456)
                .setBalance(-100)
                .build();
    }

    public static void serialize(Person person) throws IOException {
        try(var stream = Files.newOutputStream(PATH)){
            person.writeTo(stream);
        }
    }

    public static Person deserialize() throws IOException {
        try(var stream = Files.newInputStream(PATH)){
            return Person.parseFrom(stream);
        }
    }
}
