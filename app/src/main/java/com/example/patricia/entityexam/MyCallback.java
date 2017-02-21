package com.example.patricia.entityexam;

import com.example.patricia.entityexam.domain.Entity;

/**
 * Created by Patricia on 05.02.2017.
 */

public interface MyCallback {
    void add(Entity entity);
    void clear();
    void showError(String e);
}
