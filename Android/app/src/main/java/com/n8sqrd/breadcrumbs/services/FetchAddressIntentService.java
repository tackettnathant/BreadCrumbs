package com.n8sqrd.breadcrumbs.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import com.n8sqrd.breadcrumbs.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Created by ntackett on 1/28/2018.
 */

public class FetchAddressIntentService extends IntentService {
    protected ResultReceiver receiver;
    public FetchAddressIntentService(){
        super("FetchAddressIntentService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Geocoder geo = new Geocoder(this, Locale.getDefault());
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        receiver=intent.getParcelableExtra(Constants.RECEIVER);
        if (location==null) return; //nothing to do

        List<Address> addresses = null;
        List<String> placeNames = null;
        try {
            addresses = geo.getFromLocation(location.getLatitude(),location.getLongitude(),Constants.MAX_ADDRESS_RESULTS);
        } catch (Exception e){
            Log.e("com.n8sqrd.breadcrumbs","Unable to retrive addresses",e);
        }

        if (addresses!=null && !addresses.isEmpty()){
            for (Address address : addresses){
                placeNames.add(address.getFeatureName());
            }
        }

    }

    protected void deliverResults(int code, List<String> results){
        if (receiver!=null) {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(Constants.RESULT_DATA_KEY, new ArrayList<String>(results));
            receiver.send(code,bundle);
        }
    }
}
