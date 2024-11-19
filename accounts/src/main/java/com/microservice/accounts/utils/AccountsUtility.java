package com.microservice.accounts.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AccountsUtility {

    public long getAccountNumber() {
        long randomAccountNumber = 10000000000L + new Random().nextInt(900000000);
        return randomAccountNumber;
    }
}
