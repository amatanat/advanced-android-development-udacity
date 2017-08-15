package com.example.android.android_me.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.android_me.R;
import com.example.android.android_me.data.AndroidImageAssets;

/**
 * Created by amatanat on 15.08.17.
 */

public class BodyPartFragment extends Fragment {
    public BodyPartFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout file that contains only imageview
        View rootView = inflater.inflate(R.layout.fragment_body_part, container, false);
        // find imageview in layout file
        ImageView imageView = (ImageView) rootView.findViewById(R.id.body_part_iv);
        // set image resource of imageview
        imageView.setImageResource(AndroidImageAssets.getHeads().get(0));
        return rootView;
    }
}
