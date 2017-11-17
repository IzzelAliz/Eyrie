package com.ilummc.eyrie.server.tasks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskManager {
    private static final Map<Integer, Task> tasks = new ConcurrentHashMap<>();
    private static transient int id = 0;

    public static int runTask(Task task) {
        tasks.put(id, task);
        return id++;
    }

    public static void cancelTask(int id) {
        tasks.get(id).stop();
        tasks.remove(id);
    }
}
