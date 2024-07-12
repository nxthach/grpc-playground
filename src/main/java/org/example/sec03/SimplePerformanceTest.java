package org.example.sec03;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import org.example.models.sec03.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SimplePerformanceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimplePerformanceTest.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {

        var protoPerson = getPerson();
        LOGGER.info("{}", protoPerson);

        var jsonPerson = new JsonPerson("Nguyen",
                34,
                "nxthach@gmail.com",
                true,
                100,
                123456,
                -100
                );


        for (int i = 0; i < 5; i++) {
            runTest("proto", () -> proto(protoPerson));
            runTest("json", () -> json(jsonPerson));

            System.out.println("-------------------------------");
        }

    }

    private static Person getPerson() {
        return Person.newBuilder()
                .setLastName("Nguyen")
                .setAge(34)
                .setEmail("nxthach@gmail.com")
                .setEmployed(true)
                .setSalary(100)
                .setBankAccountNumber(123456)
                .setBalance(-100)
                .build();
    }

    public static void proto(Person person) {
        try {
            var bytes = person.toByteArray();
            Person.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    public static void json(JsonPerson person){

        try {
            byte[] bytes = mapper.writeValueAsBytes(person);
            mapper.readValue(bytes, JsonPerson.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public static void runTest(String testName, Runnable runnable){

        var start = System.currentTimeMillis();

        for (int i = 0; i < 5_000_000; i++) {
            runnable.run();
        }

        var end = System.currentTimeMillis();

        LOGGER.info("Time taken for {} - {} ms", testName, (end - start));

    }
}
