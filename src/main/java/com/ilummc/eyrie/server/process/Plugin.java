package com.ilummc.eyrie.server.process;

import com.google.gson.Gson;
import com.ilummc.eyrie.server.Jsonable;

public class Plugin implements Jsonable {

    private String name;

    @Override
    public String toJson() {
        return new Gson().toJson(this);
    }
}
