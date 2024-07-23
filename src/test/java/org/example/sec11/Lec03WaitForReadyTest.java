package org.example.sec11;

import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.Deadline;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.common.AbstractChannelTest;
import org.example.common.GrpcServer;
import org.example.common.ResponseObserver;
import org.example.models.sec11.AccountBalance;
import org.example.models.sec11.BalanceCheckRequest;
import org.example.models.sec11.BankServiceGrpc;
import org.example.models.sec11.WithdrawRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class Lec03WaitForReadyTest extends AbstractChannelTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(Lec03WaitForReadyTest.class);

    private final GrpcServer server = GrpcServer.create(new DeadlineBankService());
    protected BankServiceGrpc.BankServiceBlockingStub bankBlockingStub;

    @BeforeAll
    public void setUp() {
        //simulate wait for 5 seconds for server up
        Thread.ofVirtual().start(
                () -> {
                    Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
                    server.start();
                }
        );

        this.bankBlockingStub = BankServiceGrpc.newBlockingStub(channel);
    }

    @Test
    public void blockingDeadline() {

        LOGGER.info("Sending request");

        var request = WithdrawRequest.newBuilder()
                .setAccountNumber(1)
                .setAmount(50)
                .build();

        var result = this.bankBlockingStub
                .withWaitForReady()
                .withDeadline(Deadline.after(15, TimeUnit.SECONDS))
                .withdraw(request);

        while (result.hasNext()) {
            LOGGER.info("{}", result.next());
        }

    }

    @AfterAll
    public void tearDown() {
        this.server.stop();
    }


}
