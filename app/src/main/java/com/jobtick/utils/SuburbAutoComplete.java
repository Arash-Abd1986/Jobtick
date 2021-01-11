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
        int i = 0;
        int length = Objects.requireNonNull(carmenFeature.context()).size();
        String preText = "";
        for (CarmenContext cont : Objects.requireNonNull(carmenFeature.context())
        ) {
            //we don't want to append post code to string
            if (i == 0) {
                if (isInteger(cont.text())) {
                    i++;
                    continue;
                }
                suburb.append(cont.text()).append(", ");
                preText = cont.text();
            }else if(i < length - 2){
                if(preText != null && !preText.equals(cont.text())){
                    suburb.append(cont.text()).append(", ");
                    preText = cont.text();
                }
            }

            //in state
            if (i == length - 2) {
                suburb.append(Objects.requireNonNull(cont.shortCode()).substring(3));
                break;
            }
            i++;
        }
        if(length < 4){

            if(carmenFeature.text() != null && !carmenFeature.text().isEmpty()){
                String detail = carmenFeature.text();
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
