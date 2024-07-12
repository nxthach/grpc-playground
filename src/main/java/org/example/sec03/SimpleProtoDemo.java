package org.example.sec03;


import org.example.models.sec03.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleProtoDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleProtoDemo.class);

    public static void main(String[] args) {

        var person = getPerson();

        LOGGER.info("{}", person);
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

}
