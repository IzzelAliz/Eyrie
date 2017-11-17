package com.ilummc.eyrie.server.config;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.ilummc.eyrie.server.EyrieServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.UUID;

public class Config {

    private static Config instance;

    public String uuid = UUID.randomUUID().toString();
    public boolean enableStats = true;

    public static Config getInstance() {
        return instance;
    }

    public static void load() {
        File json = new File(EyrieServer.getBaseDir(), "config.json");
        if (!json.exists())
            try {
                json.createNewFile();
                instance = new Gson().fromJson(new InputStreamReader(EyrieServer.getResource("config.json")), Config.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        else
            try {
                instance = new Gson().fromJson(Files.toString(json, Charset.forName("utf-8")), Config.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void save() {
        File json = new File(EyrieServer.getBaseDir(), "config.json");
        try {
            Files.write(new Gson().toJson(instance).getBytes(Charset.forName("utf-8")), json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
