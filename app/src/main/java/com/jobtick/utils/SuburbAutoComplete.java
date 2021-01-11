package com.jobtick.utils;

import android.app.Activity;
import android.content.Intent;

import com.jobtick.R;
import com.mapbox.api.geocoding.v5.models.CarmenContext;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker;

import java.util.Objects;

public class SuburbAutoComplete {

    private final Intent intent;

    public SuburbAutoComplete(Activity activity) {
        intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(Mapbox.getAccessToken())
                .placeOptions(PlaceOptions.builder()
                        .backgroundColor(activity.getResources().getColor(R.color.backgroundLightGrey))
                        .limit(10)
                        .geocodingTypes("postcode", "locality", "district", "neighborhood", "address", "poi")
                        .country("AU")
                        .build(PlaceOptions.MODE_CARDS))
                .build(activity);
    }

    public Intent getIntent() {
        return intent;
    }


    public static String getLatitude(Intent data) {
        return Double.toString(getLatitudeDouble(data));
    }

    public static String getLongitude(Intent data) {
        return Double.toString(getLongitudeDouble(data));
    }

    public static double getLatitudeDouble(Intent data) {
        CarmenFeature carmenFeature = PlacePicker.getPlace(data);
        assert carmenFeature != null;
        return Objects.requireNonNull(carmenFeature.center()).latitude();
    }

    public static double getLongitudeDouble(Intent data) {
        CarmenFeature carmenFeature = PlacePicker.getPlace(data);
        assert carmenFeature != null;
        return Objects.requireNonNull(carmenFeature.center()).longitude();
    }

    public static String getSuburbName(Intent data) {
        CarmenFeature carmenFeature = PlacePicker.getPlace(data);
        assert carmenFeature != null;
        System.out.println("SuburbAutoComplete: =======> " + carmenFeature.toJson());

        StringBuilder suburb = new StringBuilder();
        int length = Objects.requireNonNull(carmenFeature.context()).size();
        //we build data like
        // 1- Suburb, city name, state name
        // 2- city name, sate name
        // 3- suburb name (village name), state name
        if (length >= 3) {
            CarmenContext state = Objects.requireNonNull(carmenFeature.context().get(length - 2));
            CarmenContext city = Objects.requireNonNull(carmenFeature.context().get(length - 3));
            suburb.append(city.text()).append(", ").append(state.shortCode().substring(3));
        } else if (length == 2) {
            CarmenContext state = Objects.requireNonNull(carmenFeature.context().get(length - 2));
            suburb.append(state.shortCode().substring(3));
        }else if(length < 2){
            throw new IllegalStateException("context of suburb has not at least two part!");
        }

        //if we don't have enough information in suburb, so we append extra data from text

        if (carmenFeature.text() != null && !carmenFeature.text().isEmpty()) {
            String detail = carmenFeature.text();
            if (!suburb.toString().toLowerCase().contains(detail.toLowerCase())) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(detail).append(", ").append(suburb.toString());
                suburb = stringBuilder;
            }
        }

        return suburb.toString();
    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (Exception e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
}
