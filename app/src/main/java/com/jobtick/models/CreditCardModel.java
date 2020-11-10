package com.jobtick.models;

import java.util.List;

public class CreditCardModel {

    private boolean success;
    private String message;
    private List<Data> data;

    public class Data {

        public Card card;
        public Wallet wallet;
    }

    public class Card {

        public String brand;
        public String last4;
        public int exp_month;
        public int exp_year;
    }
    public class Wallet {

        public int balance;
    }
    
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
}

