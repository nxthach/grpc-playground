package org.example.sec12.interceptors;

import io.grpc.*;
import org.example.sec12.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;

public class UserTokenInterceptor implements ServerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTokenInterceptor.class);

    /*
    user-token-1, user-token-2 => prime users, all calls are allowed
    user-token-3, user-token-4 => standard users, server streaming calls are NOT allowed. other calls are allowed.
    any other token            => not valid...!
 */

    private static final Set<String> PRIME_SET = Set.of("user-token-1", "user-token-2");
    private static final Set<String> STANDARD_SET = Set.of("user-token-3", "user-token-4");

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        var token = extractToken(headers.get(Constants.USER_TOKEN_KEY));

        LOGGER.info("{}", token);

        if(!isValid(token)){
            return close(call,
                    headers,
                    Status.UNAUTHENTICATED.withDescription("token is either null or invalid"));
        }

        //when token valid then check that sever is sends one message
        var isSeverSendsOneMessage = call.getMethodDescriptor().getType().serverSendsOneMessage();
        if(isSeverSendsOneMessage || PRIME_SET.contains(token)){
            return next.startCall(call, headers);
        }

        //User is not allow
        return close(call,
                headers,
                Status.PERMISSION_DENIED.withDescription("user is not allow to do this operation"));


    }

    private String extractToken(String value) {
        return Objects.nonNull(value) && value.startsWith(Constants.BEARER)
                ? value.substring(Constants.BEARER.length()).trim()
                : null;
    }

    private boolean isValid(String token){
        return Objects.nonNull(token)
                && (PRIME_SET.contains(token) || STANDARD_SET.contains(token));
    }

    private <ReqT, RespT> ServerCall.Listener<ReqT> close(
            ServerCall<ReqT,RespT> call,
            Metadata headers,
            Status status) {

        call.close(status, headers);

        return new ServerCall.Listener<ReqT>() {};

    }

}
