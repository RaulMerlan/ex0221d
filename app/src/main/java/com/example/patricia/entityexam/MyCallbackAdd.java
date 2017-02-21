package com.example.patricia.entityexam;

import com.example.patricia.entityexam.domain.Entity;

/**
 * Created by Patricia on 05.02.2017.
 */

public interface MyCallbackAdd {

    void showError(String error);
    void add(Entity entity);
}
