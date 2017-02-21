package com.example.patricia.entityexam.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.patricia.entityexam.EntityDetailActivity;
import com.example.patricia.entityexam.EntityDetailFragment;
import com.example.patricia.entityexam.R;
import com.example.patricia.entityexam.domain.Entity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Patricia on 04.02.2017.
 */

public class MyAdapter  extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private final List<Entity> mValues;

    public MyAdapter() {
        mValues = new ArrayList<>();
    }

    public void addData(Entity entity) {
        mValues.add(entity);
        notifyDataSetChanged();
    }

    public void deleteData(Entity entity){
        int index = -1;
        for (int i=0;i<mValues.size();i++){
            if(mValues.get(i).getId() == entity.getId()){
                index = i;
                break;
            }
        }

        if(index != -1){
            mValues.remove(index);
        }

        notifyDataSetChanged();
    }

    public void updateData(Entity entity){
        for (int i=0;i<mValues.size();i++){
            if(mValues.get(i).getId() == entity.getId()){
                mValues.get(i).setName(entity.getName());
                mValues.get(i).setType(entity.getType());
                mValues.get(i).setSeats(entity.getSeats());
                break;
            }
        }

        notifyDataSetChanged();
    }

    public void clear() {
        mValues.clear();
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.entity_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Entity entity = mValues.get(position);
        holder.name.setText(entity.getName());
        holder.group.setText(entity.getType());
        holder.presences.setText(String.valueOf(entity.getSeats()));
        //String.valueOf(entity.getField())

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, EntityDetailActivity.class);
                intent.putExtra(EntityDetailFragment.ARG_ITEM_ID, String.valueOf(entity.getId()));
                intent.putExtra(EntityDetailFragment.ARG_ITEM_FIELD1,entity.getName());
                intent.putExtra(EntityDetailFragment.ARG_ITEM_FIELD2,entity.getType());
                intent.putExtra(EntityDetailFragment.ARG_ITEM_FIELD3,String.valueOf(entity.getSeats()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView name;

        @BindView(R.id.type)
        TextView group;

        @BindView(R.id.seats)
        TextView presences;

        View mView;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + name.getText() + "'";
        }
    }
}
