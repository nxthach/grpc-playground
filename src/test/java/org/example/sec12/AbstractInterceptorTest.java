package org.example.sec12;

import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.common.GrpcServer;
import org.example.models.sec12.BankServiceGrpc;
import org.example.sec12.interceptors.GzipResponseInterceptor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractInterceptorTest {

    private GrpcServer server;
    protected ManagedChannel channel;
    protected BankServiceGrpc.BankServiceBlockingStub bankBlockingStub;
    protected BankServiceGrpc.BankServiceStub bankStub;

    protected abstract List<ClientInterceptor> getClientInterceptors();

    protected GrpcServer createServer(){
        return GrpcServer.create(8000, serverBuilder -> {
            serverBuilder.addService(new BankService())
                    .intercept(new GzipResponseInterceptor());
        });
    }

    @BeforeAll
    public void setUp() {
        this.server = createServer();
        this.server.start();

        //channel simulate for client
        this.channel = ManagedChannelBuilder.forAddress("localhost", 8000)
                .usePlaintext()
                .intercept(getClientInterceptors())
                .build();
        this.bankBlockingStub = BankServiceGrpc.newBlockingStub(channel);
        this.bankStub = BankServiceGrpc.newStub(channel);
    }

    @AfterAll
    public void tearDown(){
        this.server.stop();
        this.channel.shutdownNow();
    }

}
