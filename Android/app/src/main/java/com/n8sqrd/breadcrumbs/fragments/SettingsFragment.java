package com.n8sqrd.breadcrumbs.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.content.SharedPreferences;

/**
 * Created by ntackett on 11/19/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        return false;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }
}
