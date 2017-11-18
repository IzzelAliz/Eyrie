package com.ilummc.eyrie.eyrieaccess;

import com.google.gson.Gson;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RequestHandler {

    private static final Map<String, ClientSession> sessionMap = new HashMap<>();
    private static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public static void handleProfileQuery(String uuid, OutputStream outputStream) {
        GameProfile profile = ProfileManager.getFromUuid(uuid);
        if (profile != null) {
            PrintStream stream = new PrintStream(outputStream);
            stream.println("HTTP/1.1 200 OK");
            stream.println("Content-Type: application/json; charset=utf-8");
            stream.println();
            stream.println(new Gson().toJson(profile));
            stream.flush();
        } else {
            PrintStream stream = new PrintStream(outputStream);
            stream.println("HTTP/1.1 204 No Content");
            stream.println("Content-Type: application/json; charset=utf-8");
            stream.println();
            stream.flush();
        }
    }

    public static void handleServerCheck(String inGameName, String serverId, String ipAddress, OutputStream outputStream) {
        if (sessionMap.containsKey(serverId) && ProfileManager.getFromAccessToken(sessionMap.get(serverId).accessToken).getName().equals(inGameName)
                && (ipAddress == null || sessionMap.get(serverId).inetAddress.getHostAddress().equals(ipAddress))) {
            PrintStream stream = new PrintStream(outputStream);
            stream.println("HTTP/1.1 200 OK");
            stream.println("Content-Type: application/json; charset=utf-8");
            stream.println();
            stream.println(new Gson().toJson(ProfileManager.getFromAccessToken(sessionMap.get(serverId).accessToken)));
            stream.flush();
        } else {
            PrintStream stream = new PrintStream(outputStream);
            stream.println("HTTP/1.1 204 No Content");
            stream.println("Content-Type: application/json; charset=utf-8");
            stream.println();
            stream.flush();
        }
    }

    public static void handleClientJoin(ClientJoin clientJoin, InetAddress address, OutputStream outputStream) {
        if (!sessionMap.containsKey(clientJoin.serverId) && TokenManager.user.containsKey(clientJoin.accessToken) &&
                User.fromGameProfile(ProfileManager.getFromUsername(TokenManager.user.get(clientJoin.accessToken)))
                        .id.equals(clientJoin.selectedProfile)) {
            sessionMap.put(clientJoin.serverId, clientJoin.wrap(address));
            scheduledExecutorService.schedule(() -> {
                synchronized (sessionMap) {
                    sessionMap.remove(clientJoin.serverId);
                }
            }, 20, TimeUnit.SECONDS);
            PrintStream stream = new PrintStream(outputStream);
            stream.println("HTTP/1.1 204 No Content");
            stream.println("Content-Type: application/json; charset=utf-8");
            stream.println();
            stream.flush();
        } else {
            PrintStream stream = new PrintStream(outputStream);
            stream.println("HTTP/1.1 403 Forbidden");
            stream.println("Content-Type: application/json; charset=utf-8");
            stream.println();
            stream.println(new Gson().toJson(new Error("ForbiddenOperationException", "令牌无效。")));
            stream.flush();
        }
    }

    public static void handleAuthenticate(Authenticate authenticate, OutputStream outputStream) {
        if (ProfileManager.matches(authenticate.username, authenticate.password)) {
            PrintStream stream = new PrintStream(outputStream);
            stream.println("HTTP/1.1 200 OK");
            stream.println("Content-Type: application/json; charset=utf-8");
            stream.println();
            stream.println(new Gson().toJson(TokenManager.auth(authenticate)));
            stream.flush();
        } else {
            PrintStream stream = new PrintStream(outputStream);
            stream.println("HTTP/1.1 403 Forbidden");
            stream.println("Content-Type: application/json; charset=utf-8");
            stream.println();
            stream.flush();
        }
    }

    public static void handleRefresh(Refresh refresh, OutputStream outputStream) {
        Object response = TokenManager.refreshToken(refresh);
        PrintStream stream = new PrintStream(outputStream);
        stream.println("HTTP/1.1 200 OK");
        stream.println("Content-Type: application/json; charset=utf-8");
        stream.println();
        if (response instanceof Boolean && !(boolean) response)
            stream.println("{}");
        else
            stream.println(new Gson().toJson(response));
        stream.flush();
    }

    public static void handleValidate(Validate validate, OutputStream outputStream) {
        if (TokenManager.checkToken(validate)) {
            PrintStream stream = new PrintStream(outputStream);
            stream.println("HTTP/1.1 204 No Content");
            stream.println("Content-Type: application/json; charset=utf-8");
            stream.println();
            stream.flush();
        } else {
            PrintStream stream = new PrintStream(outputStream);
            stream.println("HTTP/1.1 403 Forbidden");
            stream.println("Content-Type: application/json; charset=utf-8");
            stream.println();
            stream.println(new Gson().toJson(new Error("ForbiddenOperationException", "令牌无效。")));
            stream.flush();
        }
    }

    public static void handleInvalidate(Validate validate, OutputStream outputStream) {
        TokenManager.invalidate(validate);
        PrintStream stream = new PrintStream(outputStream);
        stream.println("HTTP/1.1 204 No Content");
        stream.println("Content-Type: application/json; charset=utf-8");
        stream.println();
        stream.flush();
    }

    public static void handleSignout(Signout signout, OutputStream outputStream) {
        if (TokenManager.signout(signout)) {
            PrintStream stream = new PrintStream(outputStream);
            stream.println("HTTP/1.1 204 No Content");
            stream.println("Content-Type: application/json; charset=utf-8");
            stream.println();
            stream.flush();
        } else {
            PrintStream stream = new PrintStream(outputStream);
            stream.println("HTTP/1.1 403 Forbidden");
            stream.println("Content-Type: application/json; charset=utf-8");
            stream.println();
            stream.flush();
        }
    }

    static class ClientJoin {
        String accessToken, selectedProfile, serverId;

        private ClientSession wrap(InetAddress address) {
            ClientSession session = new ClientSession();
            session.accessToken = accessToken;
            session.serverId = serverId;
            session.inetAddress = address;
            return session;
        }
    }

    static class ClientSession {
        String accessToken, serverId;
        InetAddress inetAddress;
    }

    static class Signout {
        String username, password;
    }

    static class Error {
        Error(String error, String errorMessage) {
            this.error = error;
            this.errorMessage = errorMessage;
        }

        String error, errorMessage;
    }

    static class Validate {
        String accessToken, clientToken;
    }

    static class RefreshResponse {
        String accessToken, clientToken;
        GameProfile selectedProfile;

        RefreshResponse(Refresh refresh, String accessToken) {
            this.accessToken = accessToken;
            this.clientToken = refresh.clientToken;
            this.selectedProfile = ProfileManager.getFromAccessToken(accessToken);
        }
    }

    static class RefreshResponseWithUser extends RefreshResponse {
        User user;

        RefreshResponseWithUser(Refresh refresh, String accessToken) {
            super(refresh, accessToken);
            this.user = User.fromGameProfile(this.selectedProfile);
        }
    }

    static class Refresh {
        String accessToken, clientToken = UUID.randomUUID().toString().replace("-", "");
        boolean requestUser = false;
        GameProfile selectedProfile;
    }

    static class AuthenticateResponse {
        String accessToken, clientToken;
        GameProfile[] availableProfiles = new GameProfile[1];
        GameProfile selectedProfiles;

        AuthenticateResponse(Authenticate authenticate, String accessToken) {
            this.accessToken = accessToken;
            this.clientToken = authenticate.clientToken;
            this.availableProfiles[0] = ProfileManager.getFromUsername(authenticate.username);
            this.selectedProfiles = availableProfiles[0];
        }
    }

    static class AuthenticateResponseWithUser extends AuthenticateResponse {

        AuthenticateResponseWithUser(Authenticate authenticate, String accessToken) {
            super(authenticate, accessToken);
            this.user = User.fromGameProfile(selectedProfiles);
        }

        User user;
    }

    static class User {
        String id;

        static User fromGameProfile(GameProfile profile) {
            User user = new User();
            user.id = profile.getId();
            return user;
        }
    }

    static class Authenticate {
        String username;
        String password;
        String clientToken = UUID.randomUUID().toString().replace("-", "");
        boolean requestUser = false;
        Agent agent;

        static class Agent {
            String name;
            int version;
        }
    }
}
