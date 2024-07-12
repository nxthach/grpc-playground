package org.example.sec05.parser;

import com.google.protobuf.InvalidProtocolBufferException;
import org.example.models.sec05.v4.Television;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class V4Parser {

    private static final Logger LOGGER = LoggerFactory.getLogger(V4Parser.class);

    public static void parse(byte[] bytes) throws InvalidProtocolBufferException {
        var tv = Television.parseFrom(bytes);

        LOGGER.info("brand: {}", tv.getBrand());
        LOGGER.info("type: {}", tv.getType());
        LOGGER.info("price: {}", tv.getPrice());
    }
}
