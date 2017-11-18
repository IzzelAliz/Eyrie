package com.ilummc.eyrie.eyrieaccess;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TokenManager {

    private static Map</*accessToken*/String, /*clientToken*/String> token = new HashMap<>();
    static Map</*accessToken*/String, /*username*/String> user = new HashMap<>();

    public static boolean signout(RequestHandler.Signout signout) {
        if (ProfileManager.matches(signout.username, signout.password)) {
            boolean[] success = {false};
            new HashMap<>(user).keySet().forEach(accessToken -> {
                if (user.get(accessToken).equals(signout.username)) {
                    user.remove(accessToken);
                    token.remove(accessToken);
                    success[0] = true;
                 }
            });
            return success[0];
        }
        return false;
    }

    public static void invalidate(RequestHandler.Validate validate) {
        token.remove(validate.accessToken);
    }

    public static RequestHandler.AuthenticateResponse auth(RequestHandler.Authenticate authenticate) {
        String accessToken = UUID.randomUUID().toString().replace("-", "");
        token.put(accessToken, authenticate.clientToken);
        user.put(accessToken, authenticate.username);
        if (authenticate.requestUser)
            return new RequestHandler.AuthenticateResponseWithUser(authenticate, accessToken);
        return new RequestHandler.AuthenticateResponse(authenticate, accessToken);
    }

    public static boolean checkToken(RequestHandler.Validate validate) {
        return token.containsKey(validate.accessToken) && (validate.clientToken == null || token.get(validate.accessToken).equals(validate.clientToken));
    }

    public static Object refreshToken(RequestHandler.Refresh refresh) {
        if (token.containsKey(refresh.accessToken) && (refresh.clientToken == null || token.get(refresh.accessToken).equals(refresh.clientToken))) {
            token.remove(refresh.accessToken);
            if (refresh.clientToken == null) refresh.clientToken = UUID.randomUUID().toString().replace("-", "");
            String newAccessToken = UUID.randomUUID().toString().replace("-", "");
            token.put(newAccessToken, refresh.clientToken);
            user.replace(user.get(refresh.accessToken), newAccessToken);
            if (refresh.requestUser)
                return new RequestHandler.RefreshResponseWithUser(refresh, newAccessToken);
            return new RequestHandler.RefreshResponse(refresh, newAccessToken);
        }
        return false;
    }
}
