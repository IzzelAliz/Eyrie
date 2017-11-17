package com.ilummc.eyrie.server;

import com.ilummc.eyrie.server.config.Config;
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
        setupLogger();
        Config.load();
        if (!System.getenv().containsKey("debug"))
            new Statistics();
        Config.save();
    }

    public static InputStream getResource(String name) {
        return EyrieServer.class.getClassLoader().getResourceAsStream(name);
    }

    public static File getBaseDir() {
        URL url = EyrieServer.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            String dir = URLDecoder.decode(url.toString(), "utf-8");
            dir = dir.substring(6);
            return new File(dir).getParentFile();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
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
