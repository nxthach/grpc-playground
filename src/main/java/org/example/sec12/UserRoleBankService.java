package org.example.sec12;

import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.example.models.sec12.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class UserRoleBankService extends BankServiceGrpc.BankServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleBankService.class);

    @Override
    public void getAccountBalance(
            BalanceCheckRequest request,
            StreamObserver<AccountBalance> responseObserver) {

        var accountNumber = request.getAccountNumber();
        var balance = AccountRepository.getBalance(accountNumber);

        //update here
        if(UserRole.STANDARD.equals(Constants.USER_ROLE_KEY.get())){
            var fee = balance > 0? 1 : 0;
            AccountRepository.deductAmount(accountNumber, fee);//???
            balance = balance - fee;

        }

        var accountBalance = AccountBalance.newBuilder()
                .setAccountNumber(accountNumber)
                .setBalance(balance)
                .build();

        responseObserver.onNext(accountBalance);
        responseObserver.onCompleted();

    }


}
