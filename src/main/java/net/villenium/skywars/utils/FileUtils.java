package net.villenium.skywars.utils;

import com.google.common.io.Files;

import java.io.*;

public final class FileUtils {

    public static void copyDirectory(File sourceDirectory, File toLocation) {

        if (!sourceDirectory.isDirectory()) {
            return;
        }

        File file = new File(toLocation, toLocation.getAbsolutePath() + "/" + sourceDirectory.getName());

        if (!file.exists()) {
            file.mkdirs();
        }

        for (File fileDirectory : sourceDirectory.listFiles()) {

            copyFiles(fileDirectory, file);

        }

    }

    public static void move(File file, File targetFile) {

        if (!file.isDirectory() && (file.listFiles() == null)) {
            try {
                Files.copy(file, new File(targetFile.getPath() + "/" + file.getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        File copiedFile = new File(targetFile.getPath() + "/" + file.getName());
        copiedFile.mkdirs();

        for (File file2 : file.listFiles()) {
            move(file2, copiedFile);
        }

    }

    public static void copyFiles(File from, File to) {
        try {
            Files.copy(from, to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFolder(File file) {
        File[] f = file.listFiles();

        if (f == null) {
            return;
        }

        File[] files;
        int j = (files = f).length;
        for (int i = 0; i < j; i++) {
            File altFile = files[i];
            if (altFile.isDirectory()) {
                deleteFolder(altFile);
            } else {
                altFile.delete();
            }
        }

        file.delete();
    }


    public static void copyInputStreamToFile(InputStream in, File file) {
        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }

                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveDefaultResource(File target, InputStream inputStream) {

        if (target.exists()) {
            return;
        }

        FileUtils.copyInputStreamToFile(inputStream, target);
    }

}