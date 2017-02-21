package com.example.patricia.entityexam;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.patricia.entityexam.domain.Entity;

public class EntityDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM_FIELD1 = "item_field1";
    public static final String ARG_ITEM_FIELD2 = "item_field2";
    public static final String ARG_ITEM_FIELD3 = "item_field3";

    private Entity mItem;


    public EntityDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            mItem = new Entity(Integer.parseInt(getArguments().getString(ARG_ITEM_ID)), getArguments().getString(ARG_ITEM_FIELD1),getArguments().getString(ARG_ITEM_FIELD2),Integer.parseInt(getArguments().getString(ARG_ITEM_FIELD3)));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle("Add presence");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.entity_detail, container, false);

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.field1_detail)).setText(mItem.getName());
            ((TextView) rootView.findViewById(R.id.field2_detail)).setText(mItem.getType());
            ((EditText) rootView.findViewById(R.id.field3_detail)).setText(String.valueOf(mItem.getSeats()));
        }

        return rootView;
    }
}
