package com.example.albert.pestormix_apk.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.albert.pestormix_apk.R;
import com.example.albert.pestormix_apk.application.PestormixApplication;
import com.example.albert.pestormix_apk.application.PestormixMasterActivity;
import com.example.albert.pestormix_apk.controllers.DataController;
import com.example.albert.pestormix_apk.gcm.RegistrationIntentService;
import com.example.albert.pestormixlibrary.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class SplashActivity extends PestormixMasterActivity implements Animation.AnimationListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "SplashActivity";


    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeOrientationIfIsPhone();
        setContentView(R.layout.activity_splash);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);
        animation.setAnimationListener(this);
        findViewById(R.id.title).startAnimation(animation);
        findViewById(R.id.loading).startAnimation(animation2);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean sentToken = ((PestormixApplication) getApplication())
                        .getBoolean(Constants.PREFERENCES_SENT_TOKEN_TO_SERVER_KEY, false);
                if (sentToken) {
                    initApp();
                } else {
                    showToast(R.string.network_first_time);
                    finish();
                }
            }
        };
    }

    private void initApp() {
        if (getPestormixApplication().getBoolean(Constants.PREFERENCES_TUTORIAL_KEY, true)) {
            if (getPestormixApplication().getBoolean(Constants.PREFERENCES_INIT_DATA_KEY, true)) {
                getPestormixApplication().putBoolean(Constants.PREFERENCES_INIT_DATA_KEY, false);
                DataController.init(getRealm());
            }
            openTutorial();
        } else {
            openMain();
        }
    }

    private void openTutorial() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void openMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(Constants.ACTION_REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (getPestormixApplication().getBoolean(Constants.PREFERENCES_SENT_TOKEN_TO_SERVER_KEY, false)) {
            initApp();
        } else {
            registerReceiver();
            if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(getApplicationContext(), RegistrationIntentService.class);
                startService(intent);
            }
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
