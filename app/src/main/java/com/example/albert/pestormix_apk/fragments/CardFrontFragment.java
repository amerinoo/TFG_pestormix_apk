package com.example.albert.pestormix_apk.fragments;

/**
 * Created by Albert on 05/12/2015.
 */


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.albert.pestormix_apk.R;
import com.example.albert.pestormix_apk.listeners.OnInformationClickListener;

public class CardFrontFragment extends Fragment {
    OnInformationClickListener mListener;
    private int image;


    public static CardFrontFragment newInstance(int image) {
        CardFrontFragment cardFrontFragment = new CardFrontFragment();
        cardFrontFragment.image = image;
        return cardFrontFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card_front, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((ImageView) view.findViewById(R.id.image)).setImageResource(image);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onInformationClick();
            }
        });
    }

    public void setListener(OnInformationClickListener mListener) {
        this.mListener = mListener;

    }
}
