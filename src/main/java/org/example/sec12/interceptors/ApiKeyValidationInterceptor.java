package org.example.sec12.interceptors;

import io.grpc.*;
import org.example.sec12.Constants;

import java.util.Objects;

public class ApiKeyValidationInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        if(isValid(headers.get(Constants.API_KEY))){
            return next.startCall(call, headers);
        }

        call.close(
                Status.UNAUTHENTICATED.withDescription("must provide valid api key"),
                headers
        );

        return new ServerCall.Listener<ReqT>() {};

    }

    private boolean isValid(String apiKey){
        return "bank-client-secret".equals(apiKey);
    }

}
