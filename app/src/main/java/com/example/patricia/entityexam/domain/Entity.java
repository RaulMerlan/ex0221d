package com.example.patricia.entityexam.domain;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Patricia on 04.02.2017.
 */

public class Entity extends RealmObject{
    @PrimaryKey
    private int id;
    private String name;
    private int seats;
    private String type;

    public Entity(){}

    public Entity(int id, String name, String type, int seats) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.seats = seats;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }
}
