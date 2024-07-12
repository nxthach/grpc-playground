package org.example.sec05.parser;

import com.google.protobuf.InvalidProtocolBufferException;
import org.example.models.sec05.v1.Television;
import org.example.sec05.V1VersionCompatibility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class V1Parser {

    private static final Logger LOGGER = LoggerFactory.getLogger(V1Parser.class);

    public static void parse(byte[] bytes) throws InvalidProtocolBufferException {
        var tv = Television.parseFrom(bytes);

        LOGGER.info("brand: {}", tv.getBrand());
        LOGGER.info("year: {}", tv.getYear());
    }
}
