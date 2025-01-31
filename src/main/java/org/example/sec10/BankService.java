package org.example.sec10;

import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.example.models.sec10.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static org.example.sec10.validator.RequestValidator.*;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(BankService.class);

    @Override
    public void getAccountBalance(
            BalanceCheckRequest request,
            StreamObserver<AccountBalance> responseObserver) {

        validateAccountNumber(request.getAccountNumber())
                .ifPresentOrElse(
                        responseObserver::onError,
                        () -> sendAccountBalance(request, responseObserver)
                );

    }

    private void sendAccountBalance(
            BalanceCheckRequest request,
            StreamObserver<AccountBalance> responseObserver) {
        var accountNumber = request.getAccountNumber();
        var accountBalance = AccountBalance.newBuilder()
                .setAccountNumber(accountNumber)
                .setBalance(AccountRepository.getBalance(accountNumber))
                .build();

        responseObserver.onNext(accountBalance);
        responseObserver.onCompleted();
    }

    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {

        validateAccountNumber(request.getAccountNumber())
                .or(() -> isAmountDivisibleBy10(request.getAmount()))
                .or(() -> hasSufficientBalance(
                                request.getAmount(),
                                AccountRepository.getBalance(request.getAccountNumber())))
                .ifPresentOrElse(
                        e -> responseObserver.onError(e),
                        () -> sendMoney(request, responseObserver)
                );
        ;

    }

    private void sendMoney(WithdrawRequest request, StreamObserver<Money> responseObserver) {

        var accountNumber = request.getAccountNumber();
        var requestedAmount = request.getAmount();
        var balance = AccountRepository.getBalance(accountNumber);

        if (requestedAmount > balance) {
            responseObserver.onCompleted();
            return;
        }

        for (int i = 0; i < (requestedAmount / 10); i++) {
            var money = Money.newBuilder().setAmount(10).build();

            responseObserver.onNext(money);
            LOGGER.info("Response Money : {}", money);

            //
            AccountRepository.deductAmount(accountNumber, 10);

            //
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        }

        responseObserver.onCompleted();

    }


}
