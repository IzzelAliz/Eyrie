package com.ilummc.eyrie.server.process;

import com.google.gson.Gson;
import com.ilummc.eyrie.server.Jsonable;
import com.ilummc.eyrie.server.utils.FixedList;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Server implements Jsonable {

    transient Process process;
    transient BufferedReader dataIn, errIn;
    private transient BufferedWriter dataOut;
    private transient FixedList<String> cachedLogs = new FixedList<>(50);

    transient int playerAmount;
    private transient boolean running = false;

    private String name;
    private String javaPath;
    private File folder;
    private File jar;
    private List<String> jvmargs = new ArrayList<>();

    private int bootPriority = 5;
    private boolean pluginServer = false, moddedServer = false, autoBoot = false;

    public void executeCommand(String command) {
        try {
            dataOut.write(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getBootPriority() {
        return bootPriority;
    }

    public boolean isAutoBoot() {
        return autoBoot;
    }

    public File getPluginFolder() {
        return new File(folder, "/plugins");
    }

    public File getFolder() {
        return folder;
    }

    public File getJar() {
        return jar;
    }

    public String getName() {
        return name;
    }

    public boolean isPluginServer() {
        return pluginServer;
    }

    public boolean isModdedServer() {
        return moddedServer;
    }

    public List<String> getJvmArgs() {
        return jvmargs;
    }

    public boolean isRunning() {
        return running;
    }

    public Collection<String> getLogs() {
        return cachedLogs;
    }

    public void start() {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command().add(javaPath);
        builder.command().add("-jar");
        builder.command().add(jar.getName());
        builder.command().addAll(jvmargs);
        builder.directory(folder);
        try {
            process = builder.start();
            dataIn = new BufferedReader(new InputStreamReader(process.getInputStream()));
            dataOut = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            errIn = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("服务端 " + name + " 启动失败。");
        }
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static Server fromJson(String json) {
        // TODO
        return new Server();
    }

    public static Server newServerFromFolder(File folder) {
        // TODO
        return new Server();
    }

}
