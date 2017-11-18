package com.ilummc.eyrie.eyrieaccess;

import com.google.gson.Gson;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EyrieAccess {

    private static ExecutorService service = Executors.newFixedThreadPool(10);
    private static ServerSocket socket;

    public static void start(int port, InetAddress address) {
        new Thread(() -> {
            try {
                if (address != null)
                    socket = new ServerSocket(port, 50);
                else
                    socket = new ServerSocket(port, 50, address);
                System.out.println("Eyrie Access 开始在 " + port + " 端口上运行。");
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Eyrie Access 启动失败。可能端口 " + port + " 被占用？");
            }
            while (true) {
                try {
                    Socket client = socket.accept();
                    service.execute(() -> {
                        try {
                            BufferedReader path = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            String content = path.readLine();
                            String type = content.substring(0, content.lastIndexOf(" "));
                            while ((content = path.readLine()) != null) {
                                if (content.equals(""))
                                    break;
                                System.out.println(content);
                            }
                            byte[] jsonArray = new byte[1024];
                            int len = client.getInputStream().read(jsonArray);
                            String json = new String(jsonArray, 0, len, Charset.forName("utf-8"));
                            switch (type.contains("?") ? type.substring(type.indexOf("?")) : type) {
                                case "POST /authserver/authenticate":
                                    RequestHandler.handleAuthenticate(new Gson().fromJson(json, RequestHandler.Authenticate.class), client.getOutputStream());
                                    break;
                                case "POST /authserver/refresh":
                                    RequestHandler.handleRefresh(new Gson().fromJson(json, RequestHandler.Refresh.class), client.getOutputStream());
                                    break;
                                case "POST /authserver/validate":
                                    RequestHandler.handleValidate(new Gson().fromJson(json, RequestHandler.Validate.class), client.getOutputStream());
                                    break;
                                case "POST /authserver/invalidate":
                                    RequestHandler.handleInvalidate(new Gson().fromJson(json, RequestHandler.Validate.class), client.getOutputStream());
                                    break;
                                case "POST /authserver/signout":
                                    RequestHandler.handleSignout(new Gson().fromJson(json, RequestHandler.Signout.class), client.getOutputStream());
                                    break;
                                case "POST /sessionserver/session/minecraft/join":
                                    RequestHandler.handleClientJoin(new Gson().fromJson(json, RequestHandler.ClientJoin.class), client.getInetAddress(), client.getOutputStream());
                                    break;
                                case "GET /sessionserver/session/minecraft/hasJoined":
                                    String vars = type.substring(type.indexOf("?"));
                                    String[] var = vars.split("&");
                                    if (var.length == 2) {
                                        RequestHandler.handleServerCheck(var[0].substring(var[0].indexOf("=")), var[1].substring(var[1].indexOf("=")), null, client.getOutputStream());
                                    } else if (var.length == 3) {
                                        RequestHandler.handleServerCheck(var[0].substring(var[0].indexOf("=")), var[1].substring(var[1].indexOf("=")), var[2].substring(var[2].indexOf("=")), client.getOutputStream());
                                    } else {
                                        response404(client.getOutputStream());
                                    }
                                    break;
                                default:
                                    if (type.startsWith("GET /sessionserver/session/minecraft/profile/")) {
                                        String uuid = type.substring(type.indexOf("profile/") + "profile/".length());
                                        String trim = uuid.substring(0, 32);
                                        RequestHandler.handleProfileQuery(trim, client.getOutputStream());
                                    } else
                                        response404(client.getOutputStream());
                            }
                            client.close();
                        } catch (IOException e) {
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "EyrieAccess").start();
    }

    public static void response404(OutputStream outputStream) {
        PrintStream stream = new PrintStream(outputStream);
        stream.println("HTTP/1.1 404 Not Found");
        stream.println("Content-Type: application/json; charset=utf-8");
        stream.println();
        stream.println("找不到资源。");
        stream.flush();
    }
}
