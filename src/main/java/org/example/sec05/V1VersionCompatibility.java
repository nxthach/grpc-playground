package org.example.sec05;

import com.google.protobuf.InvalidProtocolBufferException;
import org.example.models.common.Address;
import org.example.models.common.BodyStyle;
import org.example.models.common.Car;
import org.example.models.sec04.Person;
import org.example.models.sec05.v1.Television;
import org.example.sec05.parser.V1Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class V1VersionCompatibility {

    private static final Logger LOGGER = LoggerFactory.getLogger(V1VersionCompatibility.class);

    public static void main(String[] args) throws InvalidProtocolBufferException {

        var tv = Television.newBuilder()
                .setBrand("Samsung")
                .setYear(2019)
                .build();


        V1Parser.parse(tv.toByteArray());
    }

}
