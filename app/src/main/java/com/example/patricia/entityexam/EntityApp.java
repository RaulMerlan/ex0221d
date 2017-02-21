package com.example.patricia.entityexam;

import android.app.Application;

import com.example.patricia.entityexam.adapter.MyAdapter;
import com.example.patricia.entityexam.domain.Entity;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

/**
 * Created by Patricia on 05.02.2017.
 */

public class EntityApp extends Application{

    private static EntityApp instance;

    MyAdapter adapter;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.deleteRealm(config);
        Realm.setDefaultConfiguration(config);

        instance = (EntityApp) getApplicationContext();

        adapter = new MyAdapter();
    }

    public static EntityApp getInstance() {
        return instance;
    }

    public MyAdapter getAdapter(){
        return  adapter;
    }
}
