package com.jobtick.android.payment;

public interface VerifyPhoneNumber {

    void sendOTP(String phoneNumber);
    void verify(String otp);
    void resendOTP(String phoneNumber);
}
