package com.jobtick.android.payment;

public interface AddCreditCard {

    void getToken(String cardNumber, Integer expMonth, Integer expYear, String CVC, String fullName);
}
