package org.example.sec12;

import io.grpc.ClientInterceptor;
import io.grpc.Deadline;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.common.ResponseObserver;
import org.example.models.sec12.BalanceCheckRequest;
import org.example.models.sec12.Money;
import org.example.models.sec12.WithdrawRequest;
import org.example.sec12.interceptors.DeadlineInterceptor;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Lec03DeadlineInterceptorTest extends AbstractInterceptorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(Lec03DeadlineInterceptorTest.class);

    @Test
    public void defaultDeadlineViaInterceptor(){
        var ex = assertThrows(StatusRuntimeException.class,
                () -> {
                    this.bankBlockingStub.getAccountBalance(
                            BalanceCheckRequest.newBuilder()
                                    .setAccountNumber(1)
                                    .build()
                    );
                });

        assertEquals(Status.Code.DEADLINE_EXCEEDED, ex.getStatus().getCode());

    }

    @Test
    public void overrideInterceptor(){
        var request = WithdrawRequest.newBuilder()
                .setAccountNumber(1)
                .setAmount(30)
                .build();

        var response = ResponseObserver.<Money>create();

        this.bankStub
                .withDeadline(Deadline.after(6, TimeUnit.SECONDS))
                .withdraw(request, response);

        response.await();

    }

    @Override
    protected List<ClientInterceptor> getClientInterceptors() {
        return List.of(new DeadlineInterceptor(Duration.ofSeconds(2)));
    }
}
