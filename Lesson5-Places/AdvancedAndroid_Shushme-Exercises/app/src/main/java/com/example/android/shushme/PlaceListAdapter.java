package com.example.android.shushme;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.gms.location.places.PlaceBuffer;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder> {

    private Context mContext;
    private PlaceBuffer mPlaceBuffer;

    /**
     * Constructor using the context and the place buffer
     *
     * @param context the calling context/activity
     */
    public PlaceListAdapter(Context context, PlaceBuffer placeBuffer) {
        this.mContext = context;
        this.mPlaceBuffer = placeBuffer;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item
     *
     * @param parent   The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new PlaceViewHolder that holds a View with the item_place_card layout
     */
    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_place_card, parent, false);
        return new PlaceViewHolder(view);
    }

    /**
     * Binds the data from a particular position in the place buffer to the corresponding view holder
     *
     * @param holder   The PlaceViewHolder instance corresponding to the required position
     * @param position The current position that needs to be loaded with data
     */
    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {

        // get place name and address from place buffer and set text of textviews accordingly
        String placeName = mPlaceBuffer.get(position).getName().toString();
        String placeAddress = mPlaceBuffer.get(position).getAddress().toString();
        holder.nameTextView.setText(placeName);
        holder.addressTextView.setText(placeAddress);

    }

    /**
     * Returns the number of items in the place buffer
     *
     * @return Number of items in the place buffer, or 0 if null
     */
    @Override
    public int getItemCount() {
        if (mPlaceBuffer == null) return 0;
        return mPlaceBuffer.getCount();
    }

    // update place buffer with new places
    public void swapPlaces (PlaceBuffer placeBuffer){
        mPlaceBuffer = placeBuffer;
        if (mPlaceBuffer != null)
            // force the recyclerview to refresh
                this.notifyDataSetChanged();

    }

    /**
     * PlaceViewHolder class for the recycler view item
     */
    class PlaceViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView addressTextView;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
            addressTextView = (TextView) itemView.findViewById(R.id.address_text_view);
        }

    }
}
