package com.jobtick.payment;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jobtick.utils.Constant;
import com.jobtick.utils.SessionManager;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static com.jobtick.utils.ConstantKey.PUBLISHABLE_KEY;

public abstract class AddCreditCardImpl implements AddCreditCard {

    private final Context context;
    private final SessionManager sessionManager;


    public AddCreditCardImpl(Context context, SessionManager sessionManager){
        this.context = context;
        this.sessionManager = sessionManager;
    }

    private Card createCard(String cardNumber, Integer expMonth, Integer expYear, String CVC, String fullName) {
        return new Card.Builder(cardNumber,
                expMonth, expYear, CVC)
                .name(fullName)
                .build();
    }


    @Override
    public void getToken(String cardNumber, Integer expMonth, Integer expYear, String CVC, String fullName) {
        Card card = createCard(cardNumber, expMonth, expYear, CVC, fullName);

        if(!validation(card))
            return;

        Stripe stripe = new Stripe(context,
                PUBLISHABLE_KEY);
        PaymentMethodCreateParams paymentMethod = PaymentMethodCreateParams.create(card.toPaymentMethodParamsCard());
        stripe.createPaymentMethod(paymentMethod, new ApiResultCallback<PaymentMethod>() {
            @Override
            public void onSuccess(PaymentMethod paymentMethod) {
                addPaymentTokenTOServer(paymentMethod.id);
                Timber.e(String.valueOf(paymentMethod.id));
            }

            @Override
            public void onError(@NotNull Exception e) {
                AddCreditCardImpl.this.onError(e);
                Timber.tag("Stripe Error").e(e.toString());
            }
        });
    }

    private boolean validation(Card card){
        if (!card.validateNumber()) {
            this.onValidationError(ValidationErrorType.CardNumber, "Invalid card number");
            return false;
        } else if (!card.validateExpiryDate()) {
            this.onValidationError(ValidationErrorType.ExpiryDate, "Invalid Expiry Date");
            return false;
        } else if (!card.validateCVC()) {
            this.onValidationError(ValidationErrorType.CVC, "Invalid Expiry Date");
            return false;
        } else if(!card.validateCard()) {
            this.onValidationError(ValidationErrorType.Unknown, "Invalid card information");
            return false;
        }
        return true;
    }


    private void addPaymentTokenTOServer(String pm_token) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_PAYMENTS_METHOD,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Timber.e(response);
                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.has("success") && !jsonObject.isNull("success")) {
                                if (jsonObject.getBoolean("success")) {
                                    AddCreditCardImpl.this.onSuccess();
                                } else {
                                    AddCreditCardImpl.this.onError(new Exception("Something went wrong."));
                                }
                            }


                        } catch (JSONException e) {
                            Timber.e(String.valueOf(e));
                            e.printStackTrace();
                            AddCreditCardImpl.this.onError(e);
                        }


                    }
                },
                error -> {
                    AddCreditCardImpl.this.onNetworkResponseError(error.networkResponse);
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();

                map1.put("authorization", sessionManager.getTokenType() + " " + sessionManager.getAccessToken());
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                // map1.put("X-Requested-With", "XMLHttpRequest");
                return map1;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<>();
                map1.put("pm_token", pm_token);
                Timber.e(String.valueOf(map1.size()));
                Timber.e(map1.toString());
                return map1;

            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    public abstract void onSuccess();
    public abstract void onError(Exception e);
    public abstract void onNetworkResponseError(NetworkResponse networkResponse);
    public abstract void onValidationError(ValidationErrorType validationErrorType, String message);

    public enum ValidationErrorType {
        CardNumber,
        ExpiryDate,
        CVC,
        Unknown
    }
}
