package com.ilummc.eyrie.eyrieaccess;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.ilummc.eyrie.server.EyrieServer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class ProfileManager {

    private static Map</*Username*/String, GameProfile> profiles = new HashMap<>();

    public static GameProfile getFromUuid(String uuid) {
        final GameProfile[] profile = new GameProfile[1];
        profiles.values().forEach(profile1 -> {
            if (profile1.getId().equals(uuid)) profile[0] = profile1;
        });
        return profile[0];
    }

    public static GameProfile getFromAccessToken(String accessToken) {
        return profiles.get(TokenManager.user.get(accessToken));
    }

    public static GameProfile getFromUsername(String username) {
        return profiles.get(username);
    }

    public static void save() {
        File store = new File(EyrieServer.getBaseDir(), "EyrieAccess.json");
        try {
            Files.write(new Gson().toJson(profiles).getBytes(Charset.forName("utf-8")), store);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("保存 Eyrie Access 用户文件失败。");
        }
        System.out.println("成功保存 Eyrie Access 用户文件。");
    }

    public static void createAccount(String id, String name, String password) {
        profiles.put(id, new GameProfile(id, name, password));
    }

    public static boolean matches(String name, String password) {
        return profiles.keySet().contains(name) && profiles.get(name).matches(password);
    }

    @SuppressWarnings({"unchecked"})
    public static void load() {
        File store = new File(EyrieServer.getBaseDir(), "EyrieAccess.json");
        if (!store.exists()) {
            try {
                store.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("创建 Eyrie Access 用户文件出错。");
            }
            profiles = new Gson().fromJson("{}", HashMap.class);
        } else {
            try {
                profiles = new Gson().fromJson(Files.toString(store, Charset.forName("utf-8")), HashMap.class);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("读取 Eyrie Access 用户文件出错。");
            }
        }
        System.out.println("成功加载 Eyrie Access 用户文件。");
    }

}
