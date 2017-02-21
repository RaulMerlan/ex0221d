package com.example.patricia.entityexam;

import android.content.Intent;
import android.os.Bundle;
import android.sax.EndElementListener;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.patricia.entityexam.domain.Entity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EntityDetailActivity extends AppCompatActivity implements MyCallbackDetail {

    Manager manager;

    @BindView(R.id.progress_detail)
    ProgressBar progress;

    @BindView(R.id.fab_delete)
    FloatingActionButton delete_button;

    @BindView(R.id.fab_update)
    FloatingActionButton update_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        progress.setVisibility(View.GONE);

        manager = new Manager();

        final EntityDetailActivity t = this;

        //delete
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setVisibility(View.VISIBLE);
                manager.deleteEntity(Integer.parseInt(getIntent().getStringExtra(EntityDetailFragment.ARG_ITEM_ID)),progress,t);
            }
        });

        boolean ok = false;

        if(manager.networkConnectivity(this)){
            ok = true;
        }else{
            ok = false;
        }

        final boolean netConnectivity = ok;

        //update
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setVisibility(View.VISIBLE);
                TextView field1 = (TextView) findViewById(R.id.field1_detail);
                TextView field2 = (TextView) findViewById(R.id.field2_detail);
                EditText field3 = (EditText) findViewById(R.id.field3_detail);

                Entity entity = new Entity(Integer.parseInt(getIntent().getStringExtra(EntityDetailFragment.ARG_ITEM_ID)),field1.getText().toString(),field2.getText().toString(),Integer.parseInt(field3.getText().toString()));
                if(netConnectivity){
                    manager.updateEntity(entity,progress,t);
                }else{
                    manager.addPresenceLocally(entity,progress);
                }

            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(EntityDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(EntityDetailFragment.ARG_ITEM_ID));
            arguments.putString(EntityDetailFragment.ARG_ITEM_FIELD1,
                    getIntent().getStringExtra(EntityDetailFragment.ARG_ITEM_FIELD1));
            arguments.putString(EntityDetailFragment.ARG_ITEM_FIELD2,
                    getIntent().getStringExtra(EntityDetailFragment.ARG_ITEM_FIELD2));
            arguments.putString(EntityDetailFragment.ARG_ITEM_FIELD3,
                    getIntent().getStringExtra(EntityDetailFragment.ARG_ITEM_FIELD3));
            EntityDetailFragment fragment = new EntityDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.entity_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, EntityListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void delete(Entity entity){EntityApp.getInstance().getAdapter().deleteData(entity);}

    @Override
    public void update(Entity entity){EntityApp.getInstance().getAdapter().updateData(entity);}

    @Override
    public void back(){
        Intent myIntent = new Intent(EntityDetailActivity.this, EntityListActivity.class);
        startActivityForResult(myIntent,0);
    }

    @Override
    public void showError(String error){
        progress.setVisibility(View.GONE);
        Snackbar.make(delete_button, error, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}
