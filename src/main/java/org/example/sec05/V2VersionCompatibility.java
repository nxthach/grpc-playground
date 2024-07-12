package org.example.sec05;

import com.google.protobuf.InvalidProtocolBufferException;
import org.example.models.sec05.v2.Television;
import org.example.models.sec05.v2.Type;
import org.example.sec05.parser.V1Parser;
import org.example.sec05.parser.V2Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class V2VersionCompatibility {

    private static final Logger LOGGER = LoggerFactory.getLogger(V2VersionCompatibility.class);

    public static void main(String[] args) throws InvalidProtocolBufferException {

        var tv = Television.newBuilder()
                .setBrand("Samsung")
                .setModel(2019)
                .setType(Type.UHD)
                .build();


        V1Parser.parse(tv.toByteArray());

        V2Parser.parse(tv.toByteArray());
    }

}
