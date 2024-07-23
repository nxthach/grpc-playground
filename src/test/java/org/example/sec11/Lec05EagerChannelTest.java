package org.example.sec11;

import org.example.common.AbstractChannelTest;
import org.example.common.GrpcServer;
import org.example.models.sec11.BalanceCheckRequest;
import org.example.models.sec11.BankServiceGrpc;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lec05EagerChannelTest extends AbstractChannelTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(Lec05EagerChannelTest.class);

    @Test
    public void eagerChannelDemo() {
        LOGGER.info("{}", channel.getState(true));

    }

}
