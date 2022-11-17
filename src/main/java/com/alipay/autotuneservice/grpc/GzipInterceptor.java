/*
 * Ant Group
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package com.alipay.autotuneservice.grpc;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

/**
 * @author dutianze
 * @version GzipInterceptor.java, v 0.1 2021年12月23日 21:18 dutianze
 */
public class GzipInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        call.setCompression("gzip");
        return next.startCall(call, headers);
    }
}