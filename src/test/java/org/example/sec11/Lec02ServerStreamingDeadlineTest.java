package org.example.sec11;

import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.Deadline;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.common.ResponseObserver;
import org.example.models.sec11.Money;
import org.example.models.sec11.WithdrawRequest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class Lec02ServerStreamingDeadlineTest extends AbstractTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(Lec02ServerStreamingDeadlineTest.class);

    @Test
    public void blockingDeadline() {

        var ex = assertThrows(StatusRuntimeException.class,
                () -> {
                    var request = WithdrawRequest.newBuilder()
                            .setAccountNumber(1)
                            .setAmount(50)
                            .build();

                    var result = this.bankBlockingStub
                            .withDeadline(Deadline.after(2, TimeUnit.SECONDS))
                            .withdraw(request);

                    while (result.hasNext()) {
                        LOGGER.info("{}", result.next());
                    }
                });

        assertEquals(Status.Code.DEADLINE_EXCEEDED, ex.getStatus().getCode());
    }


    @Test
    public void asyncDeadline() {
        var request = WithdrawRequest.newBuilder()
                .setAccountNumber(1)
                .setAmount(50)
                .build();

        var responseObserver = ResponseObserver.<Money>create();

        //WHEN
        this.bankStub
                .withDeadline(Deadline.after(2, TimeUnit.SECONDS))
                .withdraw(request, responseObserver);

        responseObserver.await();

        //THEN
        assertEquals(2, responseObserver.getData().size());
        assertEquals(Status.Code.DEADLINE_EXCEEDED, Status.fromThrowable(responseObserver.getThrowable()).getCode());
    }
}
