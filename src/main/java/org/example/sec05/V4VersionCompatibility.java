package org.example.sec05;

import com.google.protobuf.InvalidProtocolBufferException;
import org.example.models.sec05.v4.Television;
import org.example.models.sec05.v4.Type;
import org.example.sec05.parser.V1Parser;
import org.example.sec05.parser.V2Parser;
import org.example.sec05.parser.V3Parser;
import org.example.sec05.parser.V4Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class V4VersionCompatibility {

    private static final Logger LOGGER = LoggerFactory.getLogger(V4VersionCompatibility.class);

    public static void main(String[] args) throws InvalidProtocolBufferException {

        var tv = Television.newBuilder()
                .setBrand("Samsung")
                .setType(Type.UHD)
                .setPrice(100)
                .build();


        //client job
        V1Parser.parse(tv.toByteArray());
        V2Parser.parse(tv.toByteArray());
        V3Parser.parse(tv.toByteArray());
        V4Parser.parse(tv.toByteArray());
    }

}
