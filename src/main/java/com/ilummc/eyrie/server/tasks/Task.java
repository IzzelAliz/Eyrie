package com.ilummc.eyrie.server.tasks;

public interface Task {

    void start();

    void stop();

    double getProcess();

    boolean isComplete();
}
