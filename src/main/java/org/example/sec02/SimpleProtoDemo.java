package org.example.sec02;


import org.example.models.sec02.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleProtoDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleProtoDemo.class);

    public static void main(String[] args) {

        var person1 = getPerson();

        var person2 = getPerson();

        LOGGER.info("equals -> {} ", person1.equals(person2));
        LOGGER.info(" == -> {} ", (person1 == person2));

        //
        var person3 = person1.toBuilder()
                .setName("Mike")
                .build();

        LOGGER.info("equals -> {} ", person1.equals(person3));
        LOGGER.info(" == -> {} ", (person1 == person3));

        //
        var person4 = person1.toBuilder()
                .clearName()
                .build();

        LOGGER.info("person4 -> {} ", person4);
    }

    private static Person getPerson() {
        return Person.newBuilder()
                .setName("Tony")
                .setAge(34)
                .build();
    }

}
