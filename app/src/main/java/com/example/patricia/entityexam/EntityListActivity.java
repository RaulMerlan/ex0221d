package com.example.patricia.entityexam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.patricia.entityexam.adapter.MyAdapter;
import com.example.patricia.entityexam.domain.Entity;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.TimeInterval;
import timber.log.Timber;


public class EntityListActivity extends AppCompatActivity implements MyCallback, MyCallbackDetail {

    private MyAdapter adapter;
    private Manager manager;
    private View recyclerView;

    @BindView(R.id.progress)
    ProgressBar progressBar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    WebSocketClient mWebSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_list);
        ButterKnife.bind(this);

        manager = new Manager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        //Add entity

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent myIntent = new Intent(EntityListActivity.this, AddEntity.class);
                startActivity(myIntent);

            }
        });

        connectWebSocket();

        recyclerView = findViewById(R.id.entity_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        loadEvents();

        if (manager.networkConnectivity(this)) {



            Observable.interval(30, TimeUnit.SECONDS)
                    .timeInterval()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<TimeInterval<Long>>() {
                        @Override
                        public void onCompleted() {
                            Timber.v("Refresh data complete");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.e(e, "Error refreshing data");
                            unsubscribe();
                        }

                        @Override
                        public void onNext(TimeInterval<Long> longTimeInterval) {
                            Timber.v("Refreshing data");
                            if (!loadEvents()) {
                                unsubscribe();
                            }
                        }
                    });
        }else{
            if(mWebSocketClient != null && mWebSocketClient.getConnection().isOpen()){
                mWebSocketClient.close();
            }
        }

    }


    private boolean loadEvents() {
        boolean conectivity = manager.networkConnectivity(getApplicationContext());
        List<Entity> entities = manager.getLocalEntities();

        if (conectivity) {
            fab.setVisibility(View.VISIBLE);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setSubtitle("");
            if(entities.size() > 0){
                for (Entity entity: entities) {
                    manager.updateEntity(entity,progressBar,this);
                }
            }
        } else {
            fab.setVisibility(View.GONE);

            CharSequence text = "There are "+ entities.size() +" pending presence requests.";
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setSubtitle(text);
            System.out.println(text);
            //showError2("No internet connection! Offline mode.");
        }

        manager.loadEvents(progressBar, this);
        return conectivity;
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://172.25.14.138:3100/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                Log.i("Websocket message",s);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadEvents();
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };

        mWebSocketClient.connect();

    }

    @Override
    public void delete(Entity entity){EntityApp.getInstance().getAdapter().deleteData(entity);}

    @Override
    public void update(Entity entity){EntityApp.getInstance().getAdapter().updateData(entity);}

    @Override
    public void back(){}

    @Override
    public void add(Entity entity) {
        adapter.addData(entity);
    }

    @Override
    public void clear() {
        adapter.clear();
    }

    @Override
    public void showError(String error) {
        progressBar.setVisibility(View.GONE);
        Snackbar.make(recyclerView, error, Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadEvents();
                    }
                }).show();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = EntityApp.getInstance().getAdapter();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadEvents();
    }


}
