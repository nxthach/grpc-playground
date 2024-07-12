package org.example.common;

import io.grpc.*;
import org.example.sec01.SimpleProtoDemo;
import org.example.sec06.BankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GrpcServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServer.class);

    private final Server server;

    private GrpcServer(Server server) {
        this.server = server;
    }

    public static GrpcServer create(BindableService... services){
        return create(8000, services);
    }

    public static GrpcServer create(int port, BindableService... services){
        var builder = ServerBuilder.forPort(port);
        Arrays.stream(services).forEach(builder::addService);

        return new GrpcServer(builder.build());

    }

    public GrpcServer start(){

        var serviceNames = server.getServices()
                .stream()
                .map(ServerServiceDefinition::getServiceDescriptor)
                .map(ServiceDescriptor::getName)
                .toList();

        try {
            server.start();
            LOGGER.info("Server stared. Listing on port {}. Services: {}", server.getPort(), serviceNames);

            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void await() {
        try {
            server.awaitTermination();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop(){
        server.shutdownNow();
        LOGGER.info("Server stopped");
    }

    public static void main(String[] args) throws Exception {
        var server = ServerBuilder.forPort(8000)
                .addService(new BankService())
                .build();

        server.start();
        server.awaitTermination();

    }
}
