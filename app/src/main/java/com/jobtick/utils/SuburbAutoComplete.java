package com.jobtick.utils;

import android.app.Activity;
import android.content.Intent;

import com.jobtick.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

public class SuburbAutoComplete {

    private final Intent intent;

    public SuburbAutoComplete(Activity activity){
        intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(Mapbox.getAccessToken())
                .placeOptions(PlaceOptions.builder()
                        .backgroundColor(activity.getResources().getColor(R.color.backgroundLightGrey))
                        .limit(10)
                        .country("AU")
                        .build(PlaceOptions.MODE_CARDS))
                .build(activity);
    }

    public Intent getIntent() {
        return intent;
    }
}
