package org.example.sec10.validator;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import org.example.models.sec10.ErrorMessage;
import org.example.models.sec10.ValidationCode;

import java.util.Optional;

import static org.example.models.sec10.ValidationCode.*;

public class RequestValidator {

    public static Optional<StatusRuntimeException> validateAccountNumber(int accountNumber) {
        if (accountNumber > 0 && accountNumber < 11) {
            return Optional.empty();
        }
        var metadata = buildMetadata(INVALID_ACCOUNT);
        return Optional.of(Status.INVALID_ARGUMENT
                .withDescription("account number should be between 1 and 10")
                .asRuntimeException(metadata));
    }

    public static Optional<StatusRuntimeException> isAmountDivisibleBy10(int amount) {
        if (amount > 0 && amount % 10 == 0) {
            return Optional.empty();
        }
        var metadata = buildMetadata(INVALID_AMOUNT);
        return Optional.of(Status.INVALID_ARGUMENT
                .withDescription("requested amount should be 10 multiples")
                .asRuntimeException(metadata));
    }

    public static Optional<StatusRuntimeException> hasSufficientBalance(int amount, int balance) {
        if (amount <= balance) {
            return Optional.empty();
        }
        var metadata = buildMetadata(INSUFFICIENT_BALANCE);
        return Optional.of(Status.FAILED_PRECONDITION
                .withDescription("insufficient balance")
                .asRuntimeException(metadata));
    }

    private static Metadata buildMetadata(ValidationCode code){
        var metadata = new Metadata();
        var errorMessage = ErrorMessage.newBuilder()
                .setValidationCode(code)
                .build();

        var key = ProtoUtils.keyForProto(errorMessage);
        //var key = ProtoUtils.keyForProto(ErrorMessage.getDefaultInstance());

        metadata.put(key, errorMessage);

        return metadata;
    }

}
