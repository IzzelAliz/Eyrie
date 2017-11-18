package com.ilummc.eyrie.server.tasks;

public interface Task {

    void start();

    void stop(Callback callback);

    void forceStop();

    double getProcess();

    boolean isComplete();

    interface Callback {
        void call();
    }
}
