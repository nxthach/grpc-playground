package org.example.sec06;

import io.grpc.stub.StreamObserver;
import org.example.common.ResponseObserver;
import org.example.models.sec06.TransferRequest;
import org.example.models.sec06.TransferResponse;
import org.example.models.sec06.TransferStatus;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.example.models.sec06.TransferStatus.COMPLETED;
import static org.example.models.sec06.TransferStatus.REJECTED;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Lec05BiDirectionalStreamTest extends AbstractTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(Lec05BiDirectionalStreamTest.class);

    @Test
    public void transfer(){
        LOGGER.info("Start test...");

        var responseObserver = ResponseObserver.<TransferResponse>create();

        //WHEN
        var requestObserver = this.transferStub.transfer(responseObserver);

        List.of(
                TransferRequest.newBuilder().setAmount(10).setFromAccount(6).setToAccount(6).build(),
                TransferRequest.newBuilder().setAmount(110).setFromAccount(6).setToAccount(7).build(),
                TransferRequest.newBuilder().setAmount(10).setFromAccount(6).setToAccount(7).build(),
                TransferRequest.newBuilder().setAmount(10).setFromAccount(7).setToAccount(6).build()
        ).forEach(requestObserver::onNext);

        requestObserver.onCompleted();

        //wait for complete
        responseObserver.await();

        //THEN
        assertEquals(4, responseObserver.getData().size());

        this.verify(responseObserver.getData().get(0), REJECTED, 100, 100);
        this.verify(responseObserver.getData().get(1), REJECTED, 100, 100);
        this.verify(responseObserver.getData().get(2), COMPLETED, 90, 110);
        this.verify(responseObserver.getData().get(3), COMPLETED, 100, 100);

        LOGGER.info("End test...");

    }

    void verify(TransferResponse response, TransferStatus status, int fromBalance, int toBalance){
        assertEquals(status, response.getStatus());
        assertEquals(fromBalance, response.getFromAccount().getBalance());
        assertEquals(toBalance, response.getToAccount().getBalance());

    }





}
