package com.ilummc.eyrie.server.server;

import com.ilummc.eyrie.server.EyrieServer;
import com.ilummc.eyrie.server.config.Config;

import java.io.IOException;
import java.net.ServerSocket;

public class Host {

    private static ServerSocket socket;
    private static Thread current;

    public static void start() {
        current = Thread.currentThread();
        try {
            socket = new ServerSocket(Config.getInstance().serverPort);
            EyrieServer.getLogger().info("Eyrie 开始在 " + Config.getInstance().serverPort + " 端口上运行。");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("服务端启动失败，端口 " + Config.getInstance().serverPort + " 可能被占用？");
        }
        while (true) {
            try {
                socket.accept();
            } catch (IOException e) {
            }
        }
    }

    public static void stop() {
        try {
            socket.close();
            System.out.println("Eyrie 服务端已停止运行。");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
