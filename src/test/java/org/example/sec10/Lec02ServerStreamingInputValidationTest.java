package org.example.sec10;

import io.grpc.StatusRuntimeException;
import org.example.common.ResponseObserver;
import org.example.models.sec10.Money;
import org.example.models.sec10.ValidationCode;
import org.example.models.sec10.WithdrawRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.example.models.sec10.ValidationCode.*;
import static org.junit.jupiter.api.Assertions.*;

public class Lec02ServerStreamingInputValidationTest extends AbstractTest {

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

//        assertEquals(INVALID_ARGUMENT, ex.getStatus().getCode());
        assertEquals(INVALID_ACCOUNT, getValidationCode(ex));
    }


    @ParameterizedTest
    @MethodSource("testdata")
    public void blockingInputValidationWithParameterized(
            WithdrawRequest request, ValidationCode code){
        var ex = assertThrows(StatusRuntimeException.class,
                () -> {
                    this.bankBlockingStub
                            .withdraw(request)
                            .hasNext();
                });

        //assertEquals(code, ex.getStatus().getCode());
        assertEquals(code, getValidationCode(ex));
    }



    @ParameterizedTest
    @MethodSource("testdata")
    public void asyncInputValidation(WithdrawRequest request, ValidationCode code){
        var responseObserver = ResponseObserver.<Money>create();

        //WHEN
        this.bankStub.withdraw(request, responseObserver);
        responseObserver.await();

        //THEN
        assertTrue(responseObserver.getData().isEmpty());
        assertNotNull(responseObserver.getThrowable());
        assertEquals(code, getValidationCode(responseObserver.getThrowable()));
    }

    private Stream<Arguments> testdata(){
        return Stream.of(
                Arguments.of(WithdrawRequest.newBuilder().setAccountNumber(11).setAmount(10).build(), INVALID_ACCOUNT),
                Arguments.of(WithdrawRequest.newBuilder().setAccountNumber(1).setAmount(17).build(), INVALID_AMOUNT),
                Arguments.of(WithdrawRequest.newBuilder().setAccountNumber(1).setAmount(120).build(), INSUFFICIENT_BALANCE)
        );
    }

}
