package org.example.sec09;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.common.ResponseObserver;
import org.example.models.sec09.AccountBalance;
import org.example.models.sec09.BalanceCheckRequest;
import org.example.models.sec09.Money;
import org.example.models.sec09.WithdrawRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.grpc.Status.Code.FAILED_PRECONDITION;
import static io.grpc.Status.Code.INVALID_ARGUMENT;
import static org.junit.jupiter.api.Assertions.*;

public class Lec02ServerStreamingInputValidationTest extends AbstractTest{

    @Test
    public void blockingInputValidation(){
        var ex = assertThrows(StatusRuntimeException.class,
                () -> {
                    this.bankBlockingStub
                            .withdraw(
                                    WithdrawRequest.newBuilder()
                                            .setAccountNumber(11)
                                            .setAmount(10)
                                            .build())
                            .hasNext();
                });

        assertEquals(INVALID_ARGUMENT, ex.getStatus().getCode());
    }

    @ParameterizedTest
    @MethodSource("testdata")
    public void blockingInputValidationWithParameterized(
            WithdrawRequest request, Status.Code code){
        var ex = assertThrows(StatusRuntimeException.class,
                () -> {
                    this.bankBlockingStub
                            .withdraw(request)
                            .hasNext();
                });

        assertEquals(code, ex.getStatus().getCode());
    }



    @ParameterizedTest
    @MethodSource("testdata")
    public void asyncInputValidation(WithdrawRequest request, Status.Code code){
        var responseObserver = ResponseObserver.<Money>create();

        //WHEN
        this.bankStub.withdraw(request, responseObserver);
        responseObserver.await();

        //THEN
        assertTrue(responseObserver.getData().isEmpty());
        assertNotNull(responseObserver.getThrowable());
        assertEquals(code,
                ((StatusRuntimeException) responseObserver.getThrowable()).getStatus().getCode());
    }

    private Stream<Arguments> testdata(){
        return Stream.of(
                Arguments.of(WithdrawRequest.newBuilder().setAccountNumber(11).setAmount(10).build(), INVALID_ARGUMENT),
                Arguments.of(WithdrawRequest.newBuilder().setAccountNumber(1).setAmount(17).build(), INVALID_ARGUMENT),
                Arguments.of(WithdrawRequest.newBuilder().setAccountNumber(1).setAmount(120).build(), FAILED_PRECONDITION)
        );
    }

}
