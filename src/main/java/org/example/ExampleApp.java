package org.example;

import org.example.common.GrpcServer;
import org.example.sec06.BankService;
import org.example.sec06.TransferService;
import org.example.sec07.FlowControlService;
import org.example.sec08.GuessNumberService;
import org.example.sec12.interceptors.ApiKeyValidationInterceptor;

public class ExampleApp {
    public static void main(String[] args) {

//        GrpcServer.create(
//                        new BankService(),
//                        new TransferService(),
//                        new FlowControlService(),
//                        new GuessNumberService(),
//                        new org.example.sec09.BankService(),
//                        new org.example.sec10.BankService())
//                .start()
//                .await();


        GrpcServer.create(8000, serverBuilder -> {
            serverBuilder.addService(new org.example.sec12.BankService())
                    .intercept(new ApiKeyValidationInterceptor());
        })
                .start()
                .await();
    }

    private static class BankInstance1 {
        public static void main(String[] args) {
            GrpcServer.create(6565, new BankService())
                    .start()
                    .await();
        }
    }

    private static class BankInstance2 {
        public static void main(String[] args) {
            GrpcServer.create(7575, new BankService())
                    .start()
                    .await();
        }
    }
}