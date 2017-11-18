package com.ilummc.eyrie.server.server;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.ilummc.eyrie.server.EyrieServer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountManager {

    private static final Map<String, String> map = new ConcurrentHashMap<>();

    public static void deleteAccount(String user) {
        map.remove(user);
    }

    public static void createAccount(String user, String password) {
        map.put(user, BCrypt.hashpw(password, BCrypt.gensalt(16)));
    }

    public static boolean checkPassword(String user, String password) {
        return map.containsKey(user) && BCrypt.checkpw(password, map.get(user));
    }

    @SuppressWarnings({"unchecked"})
    public static void load() {
        map.clear();
        if (!new File(EyrieServer.getBaseDir(), "accounts.json").exists()) {
            try {
                new File(EyrieServer.getBaseDir(), "accounts.json").createNewFile();
                Files.write("{}".getBytes(), new File(EyrieServer.getBaseDir(), "accounts.json"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            map.putAll(new Gson().fromJson(Files.toString(new File(EyrieServer.getBaseDir(), "accounts.json"), Charset.forName("utf-8")), Map.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("用户信息加载完毕。");
    }

    public static void save() {
        try {
            Files.write(new Gson().toJson(map).getBytes(Charset.forName("utf-8")), new File(EyrieServer.getBaseDir(), "accounts.json"));
            System.out.println("用户信息已经保存。");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
