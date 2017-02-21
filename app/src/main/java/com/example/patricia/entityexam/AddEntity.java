package com.example.patricia.entityexam;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.patricia.entityexam.domain.Entity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddEntity extends AppCompatActivity implements MyCallbackAdd {

    @BindView(R.id.field1_add)
    EditText field1;
    @BindView(R.id.field2_add)
    EditText field2;
    @BindView(R.id.field3_add)
    EditText field3;
    @BindView(R.id.add_entity)
    Button addEntity;
    @BindView(R.id.progress_add)
    ProgressBar progress;
    @BindView(R.id.fab_back)
    FloatingActionButton fab_back;

    Manager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entity);
        ButterKnife.bind(this);

        manager = new Manager();

        final AddEntity t = this;

        progress.setVisibility(View.GONE);

        //back
        fab_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AddEntity.this,EntityListActivity.class);
                startActivityForResult(i,3);
            }
        });

        //add
        addEntity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Entity entity = new Entity(0,field1.getText().toString(),field2.getText().toString(),0);
                progress.setVisibility(View.VISIBLE);
                manager.addEntity(entity,progress,t);
                field1.setText("");
                field2.setText("");
            }
        });
    }

    @Override
    public void add(Entity entity) {
        EntityApp.getInstance().getAdapter().addData(entity);
    }

    @Override
    public void showError(String error){
        progress.setVisibility(View.GONE);
        Snackbar.make(addEntity, error, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}
