package org.example.sec06.requesthandler;

import io.grpc.stub.StreamObserver;
import org.example.models.sec06.*;
import org.example.sec06.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransferRequestHandler implements StreamObserver<TransferRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferRequestHandler.class);
    private final StreamObserver<TransferResponse> responseObserver;

    public TransferRequestHandler(StreamObserver<TransferResponse> responseObserver) {
        this.responseObserver = responseObserver;
    }

    @Override
    public void onNext(TransferRequest request) {

        LOGGER.info("Request from client: {}", request);

        //do transfer
        var status = this.doTransfer(request);

        //set value to response
        responseObserver.onNext(TransferResponse.newBuilder()
                .setFromAccount(toAccountBalance(request.getFromAccount()))
                .setToAccount(toAccountBalance(request.getToAccount()))
                .setStatus(status)
                .build());

    }

    @Override
    public void onError(Throwable t) {
        LOGGER.info("Client have error: {}", t.getMessage());
    }

    @Override
    public void onCompleted() {
        LOGGER.info("Transfer request stream completed!");
        responseObserver.onCompleted();
    }

    private TransferStatus doTransfer(TransferRequest request) {
        var amount = request.getAmount();
        var fromAccount = request.getFromAccount();
        var toAccount = request.getToAccount();

        var status = TransferStatus.REJECTED;

        if (fromAccount != toAccount &&
                AccountRepository.getBalance(fromAccount) >= amount) {

            AccountRepository.deductAmount(fromAccount, amount);
            AccountRepository.addAmount(toAccount, amount);

            status = TransferStatus.COMPLETED;
        }

        return status;
    }

    private AccountBalance toAccountBalance(int account) {

        return AccountBalance.newBuilder()
                .setAccountNumber(account)
                .setBalance(AccountRepository.getBalance(account))
                .build();

    }
}
