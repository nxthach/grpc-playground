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
import org.example.sec12.interceptors.GzipRequestInterceptor;
import org.example.sec12.interceptors.GzipResponseInterceptor;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Lec04GzipInterceptorTest extends AbstractInterceptorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(Lec04GzipInterceptorTest.class);

    @Test
    public void gzipDemo() {
        this.bankBlockingStub.getAccountBalance(
                BalanceCheckRequest.newBuilder()
                        .setAccountNumber(1)
                        .build()
        );
    }


    @Override
    protected List<ClientInterceptor> getClientInterceptors() {
        return List.of(new GzipRequestInterceptor());
    }
}
