package org.example.sec06.requesthandler;

import io.grpc.stub.StreamObserver;
import org.example.models.sec06.AccountBalance;
import org.example.models.sec06.DepositRequest;
import org.example.sec06.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DepositRequestHandler implements StreamObserver<DepositRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepositRequestHandler.class);
    private final StreamObserver<AccountBalance> responseObserver;
    private int accountNumber;

    public DepositRequestHandler(StreamObserver<AccountBalance> responseObserver) {
        this.responseObserver = responseObserver;
    }

    @Override
    public void onNext(DepositRequest value) {
        switch (value.getRequestCase()){
            case ACCOUNT_NUMBER -> this.accountNumber = value.getAccountNumber();
            case MONEY -> AccountRepository.addAmount(this.accountNumber, value.getMoney().getAmount());
        }
    }

    @Override
    public void onError(Throwable t) {
        LOGGER.info("Client have error: {}", t.getMessage());
    }

    @Override
    public void onCompleted() {
        responseObserver.onNext(AccountBalance.newBuilder()
                        .setAccountNumber(this.accountNumber)
                        .setBalance(AccountRepository.getBalance(this.accountNumber))
                .build());
        responseObserver.onCompleted();
    }
}
