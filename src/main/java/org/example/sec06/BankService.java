package org.example.sec06;

import com.google.common.util.concurrent.Uninterruptibles;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.example.models.sec06.*;
import org.example.sec06.requesthandler.DepositRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(BankService.class);

    @Override
    public void getAccountBalance(
            BalanceCheckRequest request,
            StreamObserver<AccountBalance> responseObserver) {

        LOGGER.info("Request received : {}", request.getAccountNumber());

        var accountNumber = request.getAccountNumber();

        var accountBalance = AccountBalance.newBuilder()
                .setAccountNumber(accountNumber)
                .setBalance(AccountRepository.getBalance(accountNumber))
                .build();

        responseObserver.onNext(accountBalance);
        responseObserver.onCompleted();

    }

    @Override
    public void getAllAccount(Empty request, StreamObserver<ListAccountBalance> responseObserver) {

        var accountBalances = AccountRepository.getAllAccounts()
                .entrySet()
                .stream()
                .map(e -> AccountBalance.newBuilder()
                        .setAccountNumber(e.getKey())
                        .setBalance(e.getValue())
                        .build()).toList();

        responseObserver.onNext(
                ListAccountBalance.newBuilder()
                        .addAllAccounts(accountBalances)
                        .build());

        responseObserver.onCompleted();

    }

    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {

        var accountNumber = request.getAccountNumber();
        var requestedAmount = request.getAmount();
        var balance = AccountRepository.getBalance(accountNumber);

        if(requestedAmount > balance){
            responseObserver.onCompleted();
            return;
        }

        for (int i = 0; i < (requestedAmount/10); i++) {
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

    @Override
    public StreamObserver<DepositRequest> deposit(StreamObserver<AccountBalance> responseObserver) {
        return new DepositRequestHandler(responseObserver);
    }


}
