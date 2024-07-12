package org.example.sec03;


import org.example.models.sec03.Credentials;
import org.example.models.sec03.Email;
import org.example.models.sec03.Phone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleOneOfDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleOneOfDemo.class);

    public static void main(String[] args) {

        var email = Email.newBuilder()
                .setAddress("example@gmail.com")
                .setPassword("123456")
                .build();

        var phone = Phone.newBuilder()
                .setNumber(123)
                .setCode(777)
                .build();

        login(Credentials.newBuilder().setEmail(email).build());
        login(Credentials.newBuilder().setPhone(phone).build());
        login(Credentials.newBuilder().setEmail(email).setPhone(phone).build());

        LOGGER.info("{}", email.getAddress());
    }

    private static void login(Credentials credentials){
        switch (credentials.getLoginTypeCase()){
            case EMAIL -> LOGGER.info("email -> {}", credentials.getEmail());
            case PHONE -> LOGGER.info("phone -> {}", credentials.getPhone());
        }
    }



}
