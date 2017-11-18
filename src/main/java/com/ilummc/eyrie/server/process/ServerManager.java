package com.ilummc.eyrie.server.process;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerManager {

    private static final Map<String, Server> map = new ConcurrentHashMap<>();

    public static Server getServer(String name) {
        return map.get(map);
    }


}
