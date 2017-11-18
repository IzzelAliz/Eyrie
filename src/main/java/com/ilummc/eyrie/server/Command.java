package com.ilummc.eyrie.server;

import com.ilummc.eyrie.server.server.AccountManager;
import com.ilummc.eyrie.server.utils.SigarUtil;
import com.ilummc.eyrie.server.utils.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Command {

    private static List<Executor> list = new ArrayList<>();

    public static void addHandler(Executor executor) {
        list.add(executor);
    }

    public static void read() {
        addHandler(s -> {
            if (s.split("\\s")[0].equalsIgnoreCase("/account")) {
                if (s.split("\\s").length <= 2) {
                    System.err.println("用法： /account create <用户名> <密码>");
                    System.err.println("/account delete <用户名>");
                    return;
                }
                switch (s.split("\\s")[1]) {
                    case "create":
                        if (s.split("\\s").length == 4) {
                            AccountManager.createAccount(s.split("\\s")[2], s.split("\\s")[3]);
                            System.out.println("成功创建账户 " + s.split("\\s")[2]);
                        } else {
                            System.err.println("用法： /account create <用户名> <密码>");
                            System.err.println("/account delete <用户名>");
                        }
                        break;
                    case "delete":
                        if (s.split("\\s").length == 3) {
                            AccountManager.deleteAccount(s.split("\\s")[2]);
                            System.out.println("成功删除用户 " + s.split("\\s")[2]);
                        }
                        break;
                    default:
                        System.err.println("用法： /account create <用户名> <密码>");
                        System.err.println("/account delete <用户名>");
                }
            }
        });
        addHandler(s -> {
            if (s.equalsIgnoreCase("/sysinfo")) {
                System.out.println("Java 版本 " + System.getProperty("java.version") + " " + System.getProperty("java.vendor"));
                System.out.println("操作系统 " + System.getProperty("os.name") + " 版本 " + System.getProperty("os.version") + " 架构 " + System.getProperty("os.arch"));
                System.out.println("可用磁盘空间");
                Arrays.stream(File.listRoots()).forEach(f -> System.out.println("  " + f.getAbsolutePath() + "   " + Util.getSize(f.getFreeSpace()) + " / " + Util.getSize(f.getTotalSpace())));
                try {
                    if (System.getProperty("java.version").startsWith("9"))
                        System.out.println("部分系统信息不支持在 Java 9 查看。");
                    else {
                        System.out.println("处理器 " + SigarUtil.getSigar().getCpuInfoList()[0].getModel() + " @ " + SigarUtil.getSigar().getCpuInfoList()[0].getMhz() + " MHz " + Runtime.getRuntime().availableProcessors() + " 线程");
                        System.out.println("系统内存 " + Util.getSize(SigarUtil.getSigar().getMem().getActualUsed()) + " / " + Util.getSize(SigarUtil.getSigar().getMem().getTotal()));
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                System.out.println("Eyrie 内存使用 " + Util.getSize(Runtime.getRuntime().freeMemory()) + " / " + Util.getSize(Runtime.getRuntime().totalMemory()) + " / " + Util.getSize(Runtime.getRuntime().maxMemory()));
            }
        });
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                String cmd = reader.readLine();
                if (cmd.equalsIgnoreCase("/stop"))
                    break;
                list.forEach(e -> e.execute(cmd));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        EyrieServer.close();
    }

    @FunctionalInterface
    interface Executor {
        void execute(String cmd);
    }
}
