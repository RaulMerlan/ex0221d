package com.example.patricia.entityexam;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.ProgressBar;

import com.example.patricia.entityexam.domain.Entity;
import com.example.patricia.entityexam.domain.LocalEntity;
import com.example.patricia.entityexam.service.EntityService;
import com.example.patricia.entityexam.service.ServiceFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Patricia on 04.02.2017.
 */

public class Manager {
    private EntityService service;
    private Realm realm = Realm.getDefaultInstance();

    public Manager() {
        service = ServiceFactory.createRetrofitService(EntityService.class, EntityService.SERVICE_ENDPOINT);
    }

    public boolean networkConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void loadEvents(final ProgressBar progressBar, final MyCallback callback) {

        service.getEntities()
                .timeout(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Entity>>() {
                    @Override
                    public void onCompleted() {
                        Timber.v("Loading entities completed");
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error while loading the entities");
                        callback.clear();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmResults<Entity> result = realm.where(Entity.class).findAll();
                                List<Entity> entities = realm.copyFromRealm(result);
                                for (Entity good : entities) {
                                    callback.add(good);
                                }
                            }
                        });
                        callback.showError("Error loading the entities! Displaying local data.");
                    }

                    @Override
                    public void onNext(final List<Entity> entities) {
                        callback.clear();
                        for (Entity entity : entities)
                            callback.add(entity);
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.copyToRealmOrUpdate(entities);
                                Timber.v("Entities persisted locally");
                            }
                        });
                    }
                });

    }

    public void addEntity(Entity entity,final ProgressBar progressBar, final MyCallbackAdd callback){
        service.addEntity(entity)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Entity>() {
                    @Override
                    public void onCompleted() {
                        Timber.v("Add entity completed!");
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.showError("Error adding entity!");
                        Timber.e(e, "Error adding entity!");
                    }

                    @Override
                    public void onNext(final Entity entity) {
                        realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(entity);
                                    Timber.v("Entity added locally!");
                                }
                        });
                        Timber.v("Entity added!");

                        callback.add(entity);
                    }
                });

    }

    public void deleteEntity(final int id,final ProgressBar progressBar,final MyCallbackDetail callback) {
        service.deleteEntity(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Entity>() {
                    @Override
                    public void onCompleted() {
                        Timber.v("Entity deletion completed!");
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error while deleting entity!");
                        callback.showError(/*e.getMessage()*/"Error while deleting entity!");
                    }

                    @Override
                    public void onNext(Entity entity) {
                        Timber.v("Entity deleted.");

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmResults<Entity> result = realm.where(Entity.class).equalTo("id",id).findAll();
                                result.deleteAllFromRealm();
                            }
                        });

                        callback.delete(entity);
                        callback.back();
                    }
                });
    }

    public void updateEntity(Entity entity,final ProgressBar progressBar,final MyCallbackDetail callback){
        service.updateEntity(entity)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Entity>() {
                    @Override
                    public void onCompleted() {
                        Timber.v("Entity update completed");
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error while updating entity");
                        callback.showError("Error while updating entity!");
                    }

                    @Override
                    public void onNext(final Entity entity) {
                        Timber.v("Entity updated!");

                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.copyToRealmOrUpdate(entity);
                                Timber.v("Entity updated locally!");
                                LocalEntity lgood = new LocalEntity(entity.getId(),entity.getName(),entity.getType(),entity.getSeats());
                                RealmResults<LocalEntity> result = realm.where(LocalEntity.class).equalTo("id",lgood.getId()).findAll();
                                result.deleteAllFromRealm();
                            }
                        });



                        callback.update(entity);
                    }
                });

    }

    public void addPresenceLocally(final Entity entity,final ProgressBar progressBar){
        progressBar.setVisibility(View.GONE);

        realm.executeTransactionAsync(new Realm.Transaction() {
            LocalEntity localEntity = new LocalEntity(entity.getId(),entity.getName(),entity.getType(),entity.getSeats());
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(localEntity);
                Timber.v("Presence request added locally");
            }
        });
    }

    public List<Entity> getLocalEntities(){
        final List<Entity> entities = new ArrayList<>();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<LocalEntity> result = realm.where(LocalEntity.class).findAll();
                List<LocalEntity> localEntities = realm.copyFromRealm(result);

                for (LocalEntity localGood : localEntities) {
                    Entity newEntity= new Entity(localGood.getId(),localGood.getName(),localGood.getType(),localGood.getSeats());
                    entities.add(newEntity);
                }
            }
        });

        return entities;

    }


}
