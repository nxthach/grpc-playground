package org.example.sec11;

import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.Deadline;
import org.example.common.AbstractChannelTest;
import org.example.common.GrpcServer;
import org.example.models.sec11.BalanceCheckRequest;
import org.example.models.sec11.BankServiceGrpc;
import org.example.models.sec11.WithdrawRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Lec04LazyChannelTest extends AbstractChannelTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(Lec04LazyChannelTest.class);

    private final GrpcServer server = GrpcServer.create(new DeadlineBankService());
    protected BankServiceGrpc.BankServiceBlockingStub bankBlockingStub;

    @BeforeAll
    public void setUp() {
        server.start();
        this.bankBlockingStub = BankServiceGrpc.newBlockingStub(channel);
    }

    @Test
    public void lazyChannelDemo() {

        var request  = BalanceCheckRequest.newBuilder()
                .setAccountNumber(1)
                .build();

        var response = this.bankBlockingStub.getAccountBalance(request);
        LOGGER.info("{}", response);

    }

    @AfterAll
    public void tearDown() {
        this.server.stop();
    }


}
