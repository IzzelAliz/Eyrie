package com.ilummc.eyrie.eyrieaccess;

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

    }

    public static void createAccount(String id, String name, String password) {
        profiles.put(id, new GameProfile(id, name, password));
    }

    public static boolean matches(String name, String password) {
        return profiles.keySet().contains(name) && profiles.get(name).matches(password);
    }


}
