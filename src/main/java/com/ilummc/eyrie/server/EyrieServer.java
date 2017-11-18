package com.ilummc.eyrie.server;

import com.ilummc.eyrie.server.config.Config;
import com.ilummc.eyrie.server.server.AccountManager;
import com.ilummc.eyrie.server.server.Server;
import com.ilummc.eyrie.server.utils.SigarUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

public class EyrieServer {

    private static Logger logger = LogManager.getLogger(EyrieServer.class);

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        setupLogger();
        Config.load();
        if (!System.getenv().containsKey("debug") && Config.getInstance().enableStats)
            new Statistics();
        AccountManager.load();
        new Thread(Server::start, "EyrieServer").start();
        SigarUtil.init();
        System.out.println("启动完毕，用时 " + (System.currentTimeMillis() - time) + "毫秒。");
        Command.read();
    }

    public static void close() {
        long time = System.currentTimeMillis();
        Server.stop();
        AccountManager.save();
        Config.save();
        System.out.println("Eyrie 已关闭，用时 " + (System.currentTimeMillis() - time) + " 毫秒。");
        System.exit(0);
    }

    public static InputStream getResource(String name) {
        return EyrieServer.class.getClassLoader().getResourceAsStream(name);
    }

    public static File getJar() {
        URL url = EyrieServer.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            String dir = URLDecoder.decode(url.toString(), "utf-8");
            dir = dir.substring(6);
            return new File(dir);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getBaseDir() {
        return getJar().getParentFile();
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void setupLogger() {
        PrintStream out = new PrintStream(AnsiConsole.out) {
            public void println(boolean x) {
                logger.info(new Ansi().a(Boolean.valueOf(x)).fgDefault());
            }

            public void println(char x) {
                logger.info(new Ansi().a(Character.valueOf(x)).fgDefault());
            }

            public void println(char[] x) {
                logger.info(new Ansi().a(x == null ? null : new String(x)).fgDefault());
            }

            public void println(double x) {
                logger.info(new Ansi().a(Double.valueOf(x)).fgDefault());
            }

            public void println(float x) {
                logger.info(new Ansi().a(Float.valueOf(x)).fgDefault());
            }

            public void println(int x) {
                logger.info(new Ansi().a(Integer.valueOf(x)).fgDefault());
            }

            public void println(long x) {
                logger.info(new Ansi().a(x).fgDefault());
            }

            public void println(Object x) {
                logger.info(new Ansi().a(x).fgDefault());
            }

            public void println(String x) {
                logger.info(new Ansi().a(x).fgDefault());
            }
        };
        PrintStream err = new PrintStream(AnsiConsole.err) {
            public void println(boolean x) {
                logger.error(new Ansi().fgBrightRed().a(Boolean.valueOf(x)).fgDefault());
            }

            public void println(char x) {
                logger.error(new Ansi().fgBrightRed().a(Character.valueOf(x)).fgDefault());
            }

            public void println(char[] x) {
                logger.error(new Ansi().fgBrightRed().a(x == null ? null : new String(x)).fgDefault());
            }

            public void println(double x) {
                logger.error(new Ansi().fgBrightRed().a(Double.valueOf(x)).fgDefault());
            }

            public void println(float x) {
                logger.error(new Ansi().fgBrightRed().a(Float.valueOf(x)).fgDefault());
            }

            public void println(int x) {
                logger.error(new Ansi().fgBrightRed().a(Integer.valueOf(x)).fgDefault());
            }

            public void println(long x) {
                logger.error(new Ansi().fgBrightRed().a(x).fgDefault());
            }

            public void println(Object x) {
                logger.error(new Ansi().fgBrightRed().a(x).fgDefault());
            }

            public void println(String x) {
                logger.error(new Ansi().fgBrightRed().a(x).fgDefault());
            }
        };
        System.setOut(out);
        System.setErr(err);
    }

}
