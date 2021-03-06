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

    public String serverUniqueId = UUID.randomUUID().toString();
    public boolean enableStats = true;
    public int serverPort = 11117, restApiPort = 11118, eyrieAccessPort = 11119;
    public String eyrieAccessHost = null;

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
        System.out.println("配置文件加载完毕。");
    }

    public static void save() {
        File json = new File(EyrieServer.getBaseDir(), "config.json");
        try {
            Files.write(new Gson().toJson(instance).getBytes(Charset.forName("utf-8")), json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("配置文件保存完毕。");
    }

}
