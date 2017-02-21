package com.example.patricia.entityexam;

import com.example.patricia.entityexam.domain.Entity;

/**
 * Created by Patricia on 05.02.2017.
 */

public interface MyCallbackDetail {

    void showError(String error);
    void delete(Entity entity);
    void update(Entity entity);
    void back();
}
