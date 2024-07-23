package org.example.sec11;

import io.grpc.Deadline;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.common.ResponseObserver;
import org.example.models.sec11.AccountBalance;
import org.example.models.sec11.BalanceCheckRequest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class Lec01UnaryDeadlineTest extends AbstractTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(Lec01UnaryDeadlineTest.class);

    @Test
    public void blockingDeadline() {

        var ex = assertThrows(StatusRuntimeException.class,
                () -> {
                    this.bankBlockingStub
                            .withDeadline(Deadline.after(2, TimeUnit.SECONDS))
                            .getAccountBalance(BalanceCheckRequest.newBuilder()
                                    .setAccountNumber(1)
                                    .build());
                });

        assertEquals(Status.Code.DEADLINE_EXCEEDED, ex.getStatus().getCode());

    }

    @Test
    public void asyncDeadline() {

        var request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(1)
                .build();

        var response = ResponseObserver.<AccountBalance>create();

        this.bankStub.withDeadline(Deadline.after(2, TimeUnit.SECONDS))
                .getAccountBalance(request, response);

        response.await();

        assertTrue(response.getData().isEmpty());
        assertNotNull(response.getThrowable());
//        assertEquals(Status.Code.DEADLINE_EXCEEDED,
//                ((StatusRuntimeException) response.getThrowable()).getStatus().getCode());
        assertEquals(Status.Code.DEADLINE_EXCEEDED,
                Status.fromThrowable(response.getThrowable()).getCode());

    }

}
