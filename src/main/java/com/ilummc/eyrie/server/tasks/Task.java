package com.ilummc.eyrie.server.tasks;

import com.ilummc.eyrie.server.Jsonable;

public interface Task extends Jsonable {

    void start();

    void stop(Callback callback);

    void forceStop();

    double getProcess();

    boolean isComplete();

    String toJson();

    interface Callback {
        void call();
    }
}
