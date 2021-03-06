package com.example.albert.pestormix_apk.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.albert.pestormix_apk.R;
import com.example.albert.pestormix_apk.adapters.ScreenSlidePagerAdapter;
import com.example.albert.pestormix_apk.application.PestormixMasterActivity;
import com.example.albert.pestormix_apk.backend.valveApi.ValveApi;
import com.example.albert.pestormix_apk.backend.valveApi.model.ValveBean;
import com.example.albert.pestormix_apk.models.Drink;
import com.example.albert.pestormix_apk.models.Valve;
import com.example.albert.pestormix_apk.repositories.DrinkRepository;
import com.example.albert.pestormix_apk.repositories.ValveRepository;
import com.example.albert.pestormixlibrary.Constants;
import com.example.albert.pestormix_apk.utils.Utils;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Albert on 01/02/2016.
 */
public class ConfigValvesActivity extends PestormixMasterActivity implements View.OnClickListener {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private List<View> valveTabs;
    private List<TextView> valveTabsName;

    private int lastSelected = -1;

    private boolean isTutorial;
    private List<Valve> valves;

    private boolean isSaved = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeOrientationIfIsPhone();
        setContentView(R.layout.activity_config_valves);
        isTutorial = getIntent().getBooleanExtra(Constants.EXTRA_IS_TUTORIAL, false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (!isTutorial)
            ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(getString(R.string.title_valves));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        configView();
    }

    private void configView() {
        valves = ValveRepository.getValves(getRealm());
        valveTabs = new ArrayList<>();
        valveTabsName = new ArrayList<>();

        mPager = (ViewPager) findViewById(R.id.pager);
        List<Drink> drinks = DrinkRepository.getDrinks(getRealm());
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager(), drinks);
        mPager.setAdapter(mPagerAdapter);

        valveTabs.add(findViewById(R.id.v1));
        valveTabs.add(findViewById(R.id.v2));
        valveTabs.add(findViewById(R.id.v3));
        valveTabs.add(findViewById(R.id.v4));

        valveTabsName.add((TextView) findViewById(R.id.v1_drink));
        valveTabsName.add((TextView) findViewById(R.id.v2_drink));
        valveTabsName.add((TextView) findViewById(R.id.v3_drink));
        valveTabsName.add((TextView) findViewById(R.id.v4_drink));

        for (int i = 0; i < valveTabs.size(); i++) {
            valveTabs.get(i).setTag(valves.get(i).getDrinkPosition());
            valveTabs.get(i).setOnClickListener(this);
            valveTabsName.get(i).setText(getTitle(valves.get(i).getDrinkPosition()));
        }

        onClick(valveTabs.get(0));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.v1:
                changeSelected(v, 0);
                break;
            case R.id.v2:
                changeSelected(v, 1);
                break;
            case R.id.v3:
                changeSelected(v, 2);
                break;
            case R.id.v4:
                changeSelected(v, 3);
                break;

        }
    }

    private void goNext() {
        Intent intent = new Intent(this, AllReadyActivity.class);
        startActivity(intent);
        finish();
    }

    private void changeSelected(View v, int position) {
        if (lastSelected != -1) {
            int lastItem = (int) valveTabs.get(lastSelected).getTag();
            int lastCurrentItem = mPager.getCurrentItem();
            valveTabs.get(lastSelected).setSelected(false);
            valveTabsName.get(lastSelected).setTextColor(Utils.getColorResource(this, R.color.white));
            if (lastItem != lastCurrentItem) {
                valveTabs.get(lastSelected).setTag(lastCurrentItem);
                valveTabsName.get(lastSelected).setText(getTitle(lastCurrentItem));
                setIsSaved(false);
            }
        }
        v.setSelected(true);
        int currentItem = (int) v.getTag();
        mPager.setCurrentItem(currentItem);
        valveTabsName.get(position).setText(getTitle(currentItem));
        valveTabsName.get(position).setTextColor(Utils.getColorResource(this, R.color.black));
        lastSelected = position;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_valves, menu);
        menu.setGroupVisible(R.id.tutorial_group, isTutorial);
        menu.setGroupVisible(R.id.config_valves_group, !isTutorial);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.continue_button:
                save();
                goNext();
                break;
            case R.id.save:
                save();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        onClick(valveTabs.get(lastSelected));
        Drink drink;
        for (int i = 0; i < valveTabs.size(); i++) {
            int position = (Integer) valveTabs.get(i).getTag();
            drink = getDrink(position);
            ValveRepository.updateValve(getRealm(), valves.get(i), drink, position);
        }
        setIsSaved(true);
        saveValveOnCloud();
        showToast(R.string.save_completed);
    }

    private void saveValveOnCloud() {
        String userId = getPestormixApplication().getUserId();
        if (!userId.equals(Constants.DEFAULT_USER_ID)) {
            List<Valve> valves = ValveRepository.getValves(getRealm());
            final List<ValveBean> valveBeen = new ArrayList<>();
            for (Valve valve : valves) {
                ValveBean valveBean = ValveRepository.toValveBean(valve);
                valveBeen.add(valveBean);
            }
            new AsyncTask<String, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(String... params) {
                    ValveApi.Builder builder2 = new ValveApi.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null);
                    ValveApi valveApi = builder2.build();
                    for (ValveBean valveBean : valveBeen) {
                        try {
                            valveApi.insertValve(params[0], valveBean).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                    return true;
                }

                @Override
                protected void onPostExecute(Boolean finish) {
                    String s = finish ? "Valves On Cloud" : "ERROR VALVES";
                    showToast(s);
                }
            }.execute(userId);
        }
    }

    private String getTitle(int position) {
        return (String) mPagerAdapter.getPageTitle(position);
    }

    private Drink getDrink(int position) {
        return ((ScreenSlidePagerAdapter) mPagerAdapter).getItemByPosition(position);
    }

    private void cancel() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.exit_without_saving)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setIsSaved(true);
                        onBackPressed();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        if (isSaved()) {
            super.onBackPressed();
            startActivityAnimation();
        } else {
            cancel();
        }
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setIsSaved(boolean isSaved) {
        this.isSaved = isSaved;
    }
}
