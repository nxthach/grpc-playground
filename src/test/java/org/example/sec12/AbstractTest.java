package org.example.sec12;

import org.example.common.AbstractChannelTest;
import org.example.common.GrpcServer;
import org.example.models.sec12.BankServiceGrpc;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractTest extends AbstractChannelTest {

    private final GrpcServer server = GrpcServer.create(new BankService());
    protected BankServiceGrpc.BankServiceBlockingStub bankBlockingStub;
    protected BankServiceGrpc.BankServiceStub bankStub;

    @BeforeAll
    public void setUp(){
        this.server.start();
        this.bankBlockingStub = BankServiceGrpc.newBlockingStub(channel);
        this.bankStub = BankServiceGrpc.newStub(channel);
    }

    @AfterAll
    public void tearDown(){
        this.server.stop();
    }

}