package org.example.sec12.interceptors;

import io.grpc.*;

public class GzipRequestInterceptor implements ClientInterceptor {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        callOptions = callOptions.withCompression("gzip");

        return next.newCall(method, callOptions);
    }
}
