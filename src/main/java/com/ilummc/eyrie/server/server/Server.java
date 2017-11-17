package com.ilummc.eyrie.server.server;

import com.ilummc.eyrie.server.EyrieServer;
import com.ilummc.eyrie.server.config.Config;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

    private static ServerSocket socket;

    public static void start() {
        try {
            socket = new ServerSocket(Config.getInstance().serverPort);
            EyrieServer.getLogger().info("服务端开始在 " + Config.getInstance().serverPort + " 端口上运行。");
            while (true) {
                socket.accept();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("服务端启动失败，端口 " + Config.getInstance().serverPort + " 可能被占用？");
        }
    }

    public static void stop() {

    }
}
