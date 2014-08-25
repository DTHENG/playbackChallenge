package com.dtheng.playback.spela;

import android.content.Context;
import android.content.ContextWrapper;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * author : Daniel Thengvall
 */
public class IO {
    public static boolean set(Object object, String filename, ContextWrapper context) {

        try {
            FileOutputStream stream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            if (object == null) {
                stream.write("".getBytes());
            } else {
                String asJsonString = new Gson().toJson(object);
                String asUtf8 = new String(asJsonString.getBytes(), "UTF-8");;
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < asJsonString.length(); i++) {
                    String value = Integer.toHexString(asJsonString.charAt(i));
                    int hexToInt = Integer.parseInt(value, 16);
                    char intToChar = (char)hexToInt;
                    builder.append(Integer.toHexString(asJsonString.charAt(i)) +"-");
                }
                stream.write(builder.toString().getBytes());
            }
            stream.close();
            return true;
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        return false;
    }

    public static Object get(String filename, Type type, ContextWrapper context) {
        try {
            FileInputStream stream = context.openFileInput(filename);
            int count;
            StringBuilder jsonStringData = new StringBuilder();
            while ((count = stream.read()) != -1) {
                jsonStringData.append(Character.toString((char)count));
            }
            stream.close();
            String decompressed = jsonStringData.toString();
            if (decompressed.equals("")) {
                return null;
            }
            StringBuilder builder = new StringBuilder();
            String[] brokenResult = decompressed.split("-");
            try {
                for (int i = 0; i < brokenResult.length; i++) {
                    String value = brokenResult[i];
                    int hexToInt = Integer.parseInt(value, 16);
                    char intToChar = (char) hexToInt;
                    builder.append(intToChar);
                }
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                return null;
            }
            return new Gson().fromJson(builder.toString(), type);
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        return null;
    }
}