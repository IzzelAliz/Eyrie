package com.ilummc.eyrie.server.utils;

import java.text.DecimalFormat;

public class Util {

    public static String getSize(long size) {
        if (size >= 1099511627776L)
            return new DecimalFormat("#.00").format((double) size / 1099511627776.0D) + " TiB";
        if (size >= 1073741824L)
            return new DecimalFormat("#.00").format((double) size / 1073741824.0D) + " GiB";
        if (size >= 1048576L)
            return new DecimalFormat("#.00").format((double) size / 1048576.0D) + " MiB";
        if (size >= 1024L)
            return new DecimalFormat("#.00").format((double) size / 1024.0D) + " KiB";
        return size + " B";
    }
}
