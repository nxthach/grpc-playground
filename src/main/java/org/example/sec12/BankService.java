package org.example.sec12;

import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import org.example.models.sec12.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(BankService.class);

    @Override
    public void getAccountBalance(
            BalanceCheckRequest request,
            StreamObserver<AccountBalance> responseObserver) {

        var accountNumber = request.getAccountNumber();

        var accountBalance = AccountBalance.newBuilder()
                .setAccountNumber(accountNumber)
                .setBalance(AccountRepository.getBalance(accountNumber))
                .build();

        //simulate long process
        //Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);

        responseObserver.onNext(accountBalance);
        responseObserver.onCompleted();

    }

    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {

        var accountNumber = request.getAccountNumber();
        var requestedAmount = request.getAmount();
        var balance = AccountRepository.getBalance(accountNumber);

        if(requestedAmount > balance){
            responseObserver.onError(Status.FAILED_PRECONDITION.asRuntimeException());
            return;
        }

        for (int i = 0; i < (requestedAmount/10) && !Context.current().isCancelled(); i++) {
            var money = Money.newBuilder().setAmount(10).build();

            responseObserver.onNext(money);
            LOGGER.info("Response Money : {}", money);

            //
            AccountRepository.deductAmount(accountNumber, 10);

            //
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        }

        LOGGER.info("Streaming is completed");

        responseObserver.onCompleted();

    }

}
