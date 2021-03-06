package com.example.albert.pestormix_apk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.albert.pestormix_apk.R;
import com.example.albert.pestormix_apk.application.PestormixMasterActivity;
import com.example.albert.pestormixlibrary.Constants;

public class WelcomeActivity extends PestormixMasterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeOrientationIfIsPhone();
        setContentView(R.layout.activity_welcome);
        findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getPestormixApplication(), ConfigValvesActivity.class);
                intent.putExtra(Constants.EXTRA_IS_TUTORIAL, true);
                startActivity(intent);
                finish();
            }
        });
    }

}
