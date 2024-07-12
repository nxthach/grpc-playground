package org.example.sec09;

import io.grpc.StatusRuntimeException;
import org.example.common.ResponseObserver;
import org.example.models.sec09.AccountBalance;
import org.example.models.sec09.BalanceCheckRequest;
import org.junit.jupiter.api.Test;

import static io.grpc.Status.Code.INVALID_ARGUMENT;
import static org.junit.jupiter.api.Assertions.*;

public class Lec01UnaryInputValidationTest extends AbstractTest{

    @Test
    public void blockingInputValidation(){
        var ex = assertThrows(StatusRuntimeException.class,
                () -> {
                    this.bankBlockingStub.getAccountBalance(
                            BalanceCheckRequest.newBuilder()
                                    .setAccountNumber(11)
                                    .build());
                });

        assertEquals(INVALID_ARGUMENT, ex.getStatus().getCode());
    }

    @Test
    public void asyncInputValidation(){
        var request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(11)
                .build();
        var responseObserver = ResponseObserver.<AccountBalance>create();

        this.bankStub.getAccountBalance(request, responseObserver);

        responseObserver.await();

        //THEN
        assertTrue(responseObserver.getData().isEmpty());
        assertNotNull(responseObserver.getThrowable());
        assertEquals(INVALID_ARGUMENT,
                ((StatusRuntimeException) responseObserver.getThrowable())
                        .getStatus().getCode());
    }
}
