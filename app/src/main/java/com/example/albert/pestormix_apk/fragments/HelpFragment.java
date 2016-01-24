package com.example.albert.pestormix_apk.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.albert.pestormix_apk.R;
import com.example.albert.pestormix_apk.activities.DetailHelpActivity;
import com.example.albert.pestormix_apk.application.PestormixMasterFragment;
import com.example.albert.pestormix_apk.utils.Constants;

/**
 * Created by Albert on 24/01/2016.
 */
public class HelpFragment extends PestormixMasterFragment {
    private View mainView;

    public static HelpFragment getInstance() {
        HelpFragment fragment = new HelpFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainView = view;
        configView();
    }

    private void configView() {
        ListView list = (ListView) mainView.findViewById(R.id.questions_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DetailHelpFragment fragment = (DetailHelpFragment) getFragmentManager().findFragmentById(R.id.detail_content);
                if (fragment == null){
                    Intent intent = new Intent(getActivity(), DetailHelpActivity.class);
                    intent.putExtra(Constants.EXTRA_POSITION,position);
                    startActivity(intent);
                }
            }
        });
    }
}
