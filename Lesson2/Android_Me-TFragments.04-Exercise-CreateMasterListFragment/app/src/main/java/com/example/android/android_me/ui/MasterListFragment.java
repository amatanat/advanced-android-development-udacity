package com.example.android.android_me.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.android_me.R;
import com.example.android.android_me.data.AndroidImageAssets;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amatanat on 15.08.17.
 */

public class MasterListFragment extends Fragment {

    private OnImageClicked onImageClicked;

    public MasterListFragment(){}

    public interface OnImageClicked{
        void OnImageSelected(int position);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_master_list, container,false);

        GridView gridView = (GridView) view.findViewById(R.id.grid_view);

        MasterListAdapter masterListAdapter = new MasterListAdapter(this.getContext(), AndroidImageAssets.getAll());
        gridView.setAdapter(masterListAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onImageClicked.OnImageSelected(position);
            }
        });

        return view;
    }

    // check if activity has implemented required method of interface
    // if not, show exception
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            onImageClicked = (OnImageClicked) context;
        }catch (Exception ex){
            Log.e("MasterListFragment","MasterListFragment interface exception");
        }
    }
}
