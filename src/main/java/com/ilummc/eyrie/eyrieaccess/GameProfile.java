package com.ilummc.eyrie.eyrieaccess;

import com.google.gson.Gson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GameProfile {

    transient long last = System.currentTimeMillis();

    /*UUID*/
    private String id;
    /*In game name*/
    private String name;
    private String password;

    public GameProfile(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = sha512(password);
    }

    public boolean cooldown() {
        boolean b = System.currentTimeMillis() - last >= 1000 * 10;
        last = System.currentTimeMillis();
        return b;
    }

    public String getId() {
        return id;
    }

    public boolean matches(String password) {
        return sha512(password).equals(this.password);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    private static final String sha512(String content) {
        String strResult = null;
        if (content != null && content.length() > 0) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA512");
                messageDigest.update(content.getBytes());
                byte byteBuffer[] = messageDigest.digest();
                StringBuilder strHexString = new StringBuilder();
                for (byte aByteBuffer : byteBuffer) {
                    String hex = Integer.toHexString(0xff & aByteBuffer);
                    if (hex.length() == 1) {
                        strHexString.append('0');
                    }
                    strHexString.append(hex);
                }
                strResult = strHexString.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return strResult;
    }
}
