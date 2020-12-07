package com.jobtick.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.jobtick.widget.ExtendedEntryText;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker;
import com.jobtick.R;
import com.jobtick.models.UserAccountModel;
import com.jobtick.utils.Constant;
import com.jobtick.utils.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CompleteRegistrationActivity extends ActivityBase implements View.OnClickListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.first_name)
    ExtendedEntryText edtFirstName;
    @BindView(R.id.last_name)
    ExtendedEntryText edtLastName;

    @BindView(R.id.lyt_btn_complete_registration)
    MaterialButton lytBtnCompleteRegistration;

    @BindView(R.id.cb_poster)
    RadioButton cbPoster;
    @BindView(R.id.cb_worker)
    RadioButton cbWorker;

    @BindView(R.id.suburb)
    ExtendedEntryText suburb;


    private int PLACE_SELECTION_REQUEST_CODE = 1;

    private String str_latitude = null;
    private String str_longitude = null;



    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_registration);
        ButterKnife.bind(this);
        context = CompleteRegistrationActivity.this;
        initToolbar();
        suburb.setExtendedViewOnClickListener(() -> {
            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(Mapbox.getAccessToken())
                    .placeOptions(PlaceOptions.builder()
                            // .backgroundColor(Helper.getAttrColor(taskCreateActivity, R.attr.colorBackground))
                            .backgroundColor(getResources().getColor(R.color.background))
                            .limit(10)
                            .country("AU")
                            /*.addInjectedFeature(home)
                            .addInjectedFeature(work)*/
                            .build(PlaceOptions.MODE_FULLSCREEN))
                    .build(CompleteRegistrationActivity.this);
            startActivityForResult(intent, PLACE_SELECTION_REQUEST_CODE);
        });




        cbWorker.setOnClickListener(this);
        cbPoster.setOnClickListener(this);
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Create Profile");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void profileUpdate(String fname, String lname, String suburb) {

        final int[] count = {0};
        Helper.closeKeyboard(this);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_USER_PROFILE_INFO,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        hideProgressDialog();
                        try {

                            JSONObject jsonObject = new JSONObject(response);



                            JSONObject jsonObject_user = jsonObject.getJSONObject("data");


                            UserAccountModel userAccountModel = new UserAccountModel().getJsonToModel(jsonObject_user);
                            sessionManager.setUserAccount(userAccountModel);

                            sessionManager.setLogin(true);

                            //  showToast("Login SuccessFully!!!", AuthActivity.this);

                          /*  Intent intent = new Intent(CompleteRegistrationActivity.this, DashboardActivity.class);
                            openActivity(intent);*/


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            String jsonError = new String(networkResponse.data);
                            // Print Error!
                            try {
                                JSONObject jsonObject = new JSONObject(jsonError);

                                JSONObject jsonObject_error = jsonObject.getJSONObject("error");

                                String message = jsonObject_error.getString("message");

                                showToast(message, context);


                                if (jsonObject_error.has("errors")) {

                                    JSONObject jsonObject_errors = jsonObject_error.getJSONObject("errors");

                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showToast("Something Went Wrong", context);
                        }
                        Log.e("error", error.toString());
                        hideProgressDialog();
                    }
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("Content-Type", "application/x-www-form-urlencoded");
                map1.put("X-Requested-With", "XMLHttpRequest");
                map1.put("Authorization", "Bearer " + sessionManager.getAccessToken());

                return map1;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("fname", fname);
                map1.put("lname", lname);
                if (cbPoster.isChecked()) {
                    map1.put("role[" + count[0] + "]", "poster");
                    count[0] = count[0] + 1;
                }
                if (cbWorker.isChecked()) {
                    map1.put("role[" + count[0] + "]", "worker");
                }
                map1.put("location", suburb);
                map1.put("latitude", str_latitude);
                map1.put("longitude", str_longitude);
                return map1;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void openActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean validation() {
        if(!cbWorker.isChecked() && !cbPoster.isChecked()){
            cbWorker.setBackgroundResource(R.drawable.radio_button_background_on_error);
            cbPoster.setBackgroundResource(R.drawable.radio_button_background_on_error);
            return false;
        }
        if (TextUtils.isEmpty(edtFirstName.getText().toString())) {
            edtFirstName.setError("Please enter first name");
            return false;
        } else if (TextUtils.isEmpty(edtLastName.getText().toString())) {
            edtLastName.setError("Please enter last name");
            return false;
        } else if (TextUtils.isEmpty(suburb.getText().toString())) {
            suburb.setError("Please enter suburb");
            return false;
        }

        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_SELECTION_REQUEST_CODE && resultCode == RESULT_OK) {

            // Retrieve the information from the selected location's CarmenFeature
            Helper.closeKeyboard(this);
            CarmenFeature carmenFeature = PlacePicker.getPlace(data);
            Helper.Logger("THIS", "CarmenFeature = " + carmenFeature.toJson());

            //TODO: No Need To get fine location, suburb is enough
            //remove these comments latter
//            GeocodeObject geocodeObject = Helper.getGeoCodeObject(this, carmenFeature.center().latitude()
//                    , carmenFeature.center().longitude());


            suburb.setText(carmenFeature.placeName());

            // editArea.setText(geocodeObject.getKnownName());
            str_latitude = String.valueOf(carmenFeature.center().latitude());
            str_longitude = String.valueOf(carmenFeature.center().longitude());
        }
    }

    @OnClick({R.id.lyt_btn_complete_registration})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lyt_btn_complete_registration:

                if (validation()) {
                    String str_fname = edtFirstName.getText().toString().trim();
                    String str_lname = edtLastName.getText().toString().trim();
                    String str_suburb = suburb.getText().toString().trim();

                    showProgressDialog();
                    profileUpdate(str_fname, str_lname, str_suburb);

                    Intent intent = new Intent(CompleteRegistrationActivity.this, OnboardActivity.class);
                    if (cbWorker.isChecked()) {
                        intent.putExtra("as", "worker");
                    } else if (cbPoster.isChecked()) {
                        intent.putExtra("as", "poster");
                    }
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        view.setBackgroundResource(R.drawable.radio_button_background);
    }
}
