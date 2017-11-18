package com.ilummc.eyrie.server.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerLog {
    private static final List<Server> processes = new ArrayList<>();

    static {
        new Thread(() -> {
            while (true) {
                List<Server> copy;
                synchronized (processes) {
                    processes.stream().filter(Server::isRunning).forEach(server -> {
                        try {
                            while (server.process.getInputStream().available() > 0)
                                server.getLogs().add(server.dataIn.readLine());
                            while (server.process.getErrorStream().available() > 0)
                                server.getLogs().add(server.errIn.readLine());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    copy = new ArrayList<>(processes);
                }
                copy.stream().filter(s -> !s.isRunning()).forEach(processes::remove);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {
                    break;
                }
            }
        }, "LogRecordThread").start();
    }

    public static void add(Server server) {
        processes.add(server);
    }
}
