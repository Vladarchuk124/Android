package com.example.lab3;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class StorageHelper {

    private static final String FILE_NAME = "orders.txt";

    public static boolean saveOrder(Context context, OrderData data) {
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_APPEND);

            String text = data.toPrettyString()
                    + "\n------------------------------\n";

            fos.write(text.getBytes());
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String readOrders(Context context) {
        StringBuilder sb = new StringBuilder();

        try {
            FileInputStream fis = context.openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            reader.close();
            isr.close();
            fis.close();
        } catch (Exception e) {
            return "";
        }

        return sb.toString().trim();
    }

    public static boolean clearOrders(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write("".getBytes());
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}