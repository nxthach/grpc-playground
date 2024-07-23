package org.example.common;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ResponseObserver<T> implements StreamObserver<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseObserver.class);
    private final List<T> data = Collections.synchronizedList(new ArrayList<>());
    private final CountDownLatch latch;
    private Throwable throwable;

    private ResponseObserver(int count) {
        this.latch = new CountDownLatch(count);
    }

    public static <T> ResponseObserver<T> create() {
        return create(1);
    }

    public static <T> ResponseObserver<T> create(int countDown) {
        return new ResponseObserver<>(countDown);
    }

    @Override
    public void onNext(T t) {
        LOGGER.info("Received: {}", t);
        this.data.add(t);
    }

    @Override
    public void onError(Throwable throwable) {
        LOGGER.info("Received Error: {}", throwable.getMessage());
        this.throwable = throwable;
        this.latch.countDown();
    }

    @Override
    public void onCompleted() {
        LOGGER.info("Completed.");
        this.latch.countDown();
    }

    public void await() {
        try {
            //this.latch.await();
            this.latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public List<T> getData() {
        return data;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
