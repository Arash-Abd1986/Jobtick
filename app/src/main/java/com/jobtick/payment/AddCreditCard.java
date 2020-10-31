package com.jobtick.payment;

public interface AddCreditCard {

    public void getToken(String cardNumber, Integer expMonth, Integer expYear, String CVC, String fullName);
}
