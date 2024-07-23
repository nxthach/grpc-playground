package org.example.sec10;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import org.example.common.AbstractChannelTest;
import org.example.common.GrpcServer;
import org.example.models.sec10.BankServiceGrpc;
import org.example.models.sec10.ErrorMessage;
import org.example.models.sec10.ValidationCode;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.Optional;

public abstract class AbstractTest extends AbstractChannelTest {

    private static final Metadata.Key<ErrorMessage> ERROR_MSG_KEY = ProtoUtils.keyForProto(ErrorMessage.getDefaultInstance());

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

    protected ValidationCode getValidationCode(Throwable throwable){
        return Optional.ofNullable(Status.trailersFromThrowable(throwable))
                .map(e -> e.get(ERROR_MSG_KEY))
                .map(ErrorMessage::getValidationCode)
                .orElseThrow();
    }
}