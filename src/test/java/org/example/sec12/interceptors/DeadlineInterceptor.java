package org.example.sec12.interceptors;

import io.grpc.*;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DeadlineInterceptor implements ClientInterceptor {
    private Duration duration;
    public DeadlineInterceptor(Duration duration) {
        this.duration = duration;
    }


    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        if (Objects.isNull(callOptions.getDeadline())) {
            callOptions = callOptions.withDeadline(Deadline.after(duration.toMillis(), TimeUnit.MILLISECONDS));
        }

        return next.newCall(method, callOptions);
    }
}
