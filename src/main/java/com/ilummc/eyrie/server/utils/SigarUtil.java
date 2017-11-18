package com.ilummc.eyrie.server.utils;

import com.ilummc.eyrie.server.EyrieServer;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import org.hyperic.sigar.Sigar;

import java.io.File;
import java.io.IOException;

public class SigarUtil {
    private static Sigar sigar;

    public static Sigar getSigar() {
        return sigar;
    }

    @SuppressWarnings({"unchecked"})
    public static void init() {
        if (!new File(EyrieServer.getBaseDir(), "/natives").exists()) {
            File folder = new File(EyrieServer.getBaseDir(), "/natives");
            folder.mkdir();
            try {
                ZipFile file = new ZipFile(EyrieServer.getJar());
                file.getFileHeaders().stream().forEach(h -> {
                    FileHeader header = (FileHeader) h;
                    if (header.getFileName().startsWith("natives/"))
                        try {
                            file.extractFile(header, EyrieServer.getBaseDir().getAbsolutePath());
                        } catch (ZipException e) {
                            e.printStackTrace();
                        }
                });
            } catch (ZipException e) {
                e.printStackTrace();
            }
        }
        String path = System.getProperty("java.library.path");
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win"))
                path += ";" + new File(EyrieServer.getBaseDir(), "/natives").getCanonicalPath();
            else
                path += ":" + new File(EyrieServer.getBaseDir(), "/natives").getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.setProperty("java.library.path", path);
        sigar = new Sigar();
    }
}
