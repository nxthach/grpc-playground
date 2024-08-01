package org.example.sec12;

import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;
import org.example.common.GrpcServer;
import org.example.models.sec12.AccountBalance;
import org.example.models.sec12.BalanceCheckRequest;
import org.example.sec12.interceptors.ApiKeyValidationInterceptor;
import org.example.sec12.interceptors.GzipRequestInterceptor;
import org.example.sec12.interceptors.GzipResponseInterceptor;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Lec05ClientApiKeyInterceptorTest extends AbstractInterceptorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(Lec05ClientApiKeyInterceptorTest.class);

    @Override
    protected GrpcServer createServer(){
        return GrpcServer.create(8000, serverBuilder -> {
            serverBuilder.addService(new BankService())
                    .intercept(new ApiKeyValidationInterceptor());
        });
    }

    @Test
    public void clientApiKeyDemo() {
        var response = this.bankBlockingStub.getAccountBalance(
                BalanceCheckRequest.newBuilder()
                        .setAccountNumber(1)
                        .build()
        );

        LOGGER.info("{}", response);
    }

    @Override
    protected List<ClientInterceptor> getClientInterceptors() {
        return List.of(MetadataUtils.newAttachHeadersInterceptor(getApiKey()));
    }

    private Metadata getApiKey() {
        var metadata = new Metadata();
        metadata.put(Constants.API_KEY, "bank-client-secret");

        return metadata;
    }
}
