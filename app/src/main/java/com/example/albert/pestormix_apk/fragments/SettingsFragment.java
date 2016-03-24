package com.example.albert.pestormix_apk.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;

import com.example.albert.pestormix_apk.R;
import com.example.albert.pestormix_apk.activities.ConfigValvesActivity;
import com.example.albert.pestormix_apk.activities.MainActivity;
import com.example.albert.pestormix_apk.application.PestormixMasterActivity;
import com.example.albert.pestormix_apk.utils.Constants;

/**
 * Created by Albert on 24/01/2016.
 */
public class SettingsFragment extends PreferenceFragment {

    public static SettingsFragment getInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configView();
    }

    private void configView() {
        findPreference(getString(R.string.PREFERENCE_VALVES)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                goConfigValves();
                return true;
            }
        });
        final Preference loginPreference = findPreference(getString(R.string.PREFERENCE_LOGIN));
        addLoginTitle(loginPreference);
        loginPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (isUserLogged()) {
                    showToast(R.string.sign_out_google);
                    signOutGoogle();
                    preference.setTitle(R.string.sign_in_google);
                } else {
                    showToast(R.string.sign_in_google);
                    signInGoogle();
                    preference.setTitle(R.string.sign_out_google);
                }
                loginPreference.setEnabled(false);
                reenableLogin(loginPreference);
                return true;
            }
        });
        findPreference(getString(R.string.PREFERENCE_MUSE)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
                boolean visible = checkBoxPreference.isChecked();
                museVisible(visible);
                return false;
            }
        });
    }

    private void reenableLogin(final Preference loginPreference) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loginPreference.setEnabled(true);
            }
        }, 1000);
    }

    private void addLoginTitle(Preference preference) {
        if (isUserLogged()) {
            preference.setTitle(R.string.sign_out_google);
        } else {
            preference.setTitle(R.string.sign_in_google);
        }
    }

    private boolean isUserLogged() {
        return ((MainActivity) getActivity()).getPestormixApplication().getBoolean(Constants.PREFERENCES_USER_LOGGED, false);
    }

    private void signInGoogle() {
        ((MainActivity) getActivity()).signIn();
    }

    private void signOutGoogle() {
        ((MainActivity) getActivity()).signOut();
    }


    private void goConfigValves() {
        Intent intent = new Intent(getActivity(), ConfigValvesActivity.class);
        startActivity(intent);
        startActivityAnimation();
    }

    public void startActivityAnimation() {
        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.nothing);
    }

    private void museVisible(boolean visible) {
        ((MainActivity) getActivity()).museVisible(visible);
    }

    public void showToast(int resId) {
        ((PestormixMasterActivity) getActivity()).showToast(resId);
    }
}
