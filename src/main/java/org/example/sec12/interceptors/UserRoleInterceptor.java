package org.example.sec12.interceptors;

import io.grpc.*;
import org.example.sec12.Constants;
import org.example.sec12.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;

/*
    We have only getAccountBalance feature
    user-token-1, user-token-2 => prime users, return the balance as it is
    user-token-3, user-token-4 => standard users, deduct $1 and then return the balance
    any other token            => not valid...!
 */
public class UserRoleInterceptor implements ServerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleInterceptor.class);



    private static final Set<String> PRIME_SET = Set.of("user-token-1", "user-token-2");
    private static final Set<String> STANDARD_SET = Set.of("user-token-3", "user-token-4");

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        var token = extractToken(headers.get(Constants.USER_TOKEN_KEY));
        LOGGER.info("{}", token);

        var ctx = toContext(token);

        if(Objects.nonNull(ctx)){
            return Contexts.interceptCall(ctx, call, headers, next);
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

    private Context toContext(String token) {
        var isTokenValid = Objects.nonNull(token)
                && (PRIME_SET.contains(token) || STANDARD_SET.contains(token));

        if (isTokenValid) {
            var role = PRIME_SET.contains(token) ? UserRole.PRIME : UserRole.STANDARD;
            return Context.current().withValue(Constants.USER_ROLE_KEY, role);
        }
        return null;
    }

    private <ReqT, RespT> ServerCall.Listener<ReqT> close(
            ServerCall<ReqT,RespT> call,
            Metadata headers,
            Status status) {

        call.close(status, headers);

        return new ServerCall.Listener<ReqT>() {};
    }

}
