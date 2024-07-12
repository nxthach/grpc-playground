package org.example.sec05.parser;

import com.google.protobuf.InvalidProtocolBufferException;
import org.example.models.sec05.v2.Television;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class V2Parser {

    private static final Logger LOGGER = LoggerFactory.getLogger(V2Parser.class);

    public static void parse(byte[] bytes) throws InvalidProtocolBufferException {
        var tv = Television.parseFrom(bytes);

        LOGGER.info("brand: {}", tv.getBrand());
        LOGGER.info("model: {}", tv.getModel());
        LOGGER.info("type: {}", tv.getType());
    }
}
