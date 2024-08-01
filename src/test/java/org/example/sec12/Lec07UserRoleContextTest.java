package org.example.sec12;

import io.grpc.CallCredentials;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import org.example.common.GrpcServer;
import org.example.common.ResponseObserver;
import org.example.models.sec12.BalanceCheckRequest;
import org.example.models.sec12.Money;
import org.example.models.sec12.WithdrawRequest;
import org.example.sec12.interceptors.UserRoleInterceptor;
import org.example.sec12.interceptors.UserTokenInterceptor;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class Lec07UserRoleContextTest extends AbstractInterceptorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(Lec07UserRoleContextTest.class);

    @Override
    protected GrpcServer createServer(){
        return GrpcServer.create(8000, serverBuilder -> {
            serverBuilder.addService(new UserRoleBankService())
                    .intercept(new UserRoleInterceptor());
        });
    }

    @RepeatedTest(5)
    public void unaryUserCredentialsDemo() {

        for (int i = 1; i <= 4; i++) {
            var response = this.bankBlockingStub
                    .withCallCredentials(new UserSessionToken("user-token-" + i))
                    .getAccountBalance(
                            BalanceCheckRequest.newBuilder()
                                    .setAccountNumber(i)
                                    .build()
                    );

            LOGGER.info("{}", response);
        }
    }

    @Test
    public void streamingUserCredentialsDemo(){

        for (int i = 1; i <= 5; i++) {
            var request = WithdrawRequest.newBuilder()
                    .setAccountNumber(i)
                    .setAmount(30)
                    .build();

            var response = ResponseObserver.<Money>create();

            this.bankStub
                    .withCallCredentials(new UserSessionToken("user-token-" + i))
                    .withdraw(request, response);

            response.await();
        }



    }

    @Override
    protected List<ClientInterceptor> getClientInterceptors() {
        return Collections.emptyList();
    }


    private static class UserSessionToken extends CallCredentials {

        private static final String TOKEN_FORMAT = "%s %s";
        private final String jwt;

        public UserSessionToken(String jwt) {
            this.jwt = jwt;
        }

        @Override
        public void applyRequestMetadata(RequestInfo requestInfo, Executor appExecutor, MetadataApplier applier) {
            appExecutor.execute(() -> {
                var headers = new Metadata();
                headers.put(Constants.USER_TOKEN_KEY, TOKEN_FORMAT.formatted(Constants.BEARER, jwt));

                applier.apply(headers);
            });

        }
    }
}
