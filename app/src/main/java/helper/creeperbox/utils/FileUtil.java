package helper.creeperbox.utils;

import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import helper.creeperbox.clients.CreeperBox;

public class FileUtil {
    public static void copyDirectory(File sourceDir, File targetDir) throws IOException {
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new IOException("源文件夹不存在或不是文件夹: " + sourceDir.getAbsolutePath());
        }

        if (!targetDir.exists()) {
            if (!targetDir.mkdirs()) {
                throw new IOException("无法创建目标文件夹: " + targetDir.getAbsolutePath());
            }
        }

        File[] files = sourceDir.listFiles();



        int fileCount = 0;
        if (files == null) return;

        for (File file : files) {
            File targetFile = new File(targetDir, file.getName());
            if (file.isDirectory()) {
                copyDirectory(file, targetFile);
            } else {
                copyFile(file, targetFile);
                fileCount++;
            }
        }

        int finalFileCount = fileCount;
        CreeperBox.INSTANCE.activity.runOnUiThread(()->{
            Toast.makeText(CreeperBox.INSTANCE.context,"安装了"+ finalFileCount +"个",Toast.LENGTH_LONG).show();
        });
    }


    public static void copyFile(File sourceFile, File destFile) throws IOException {

        if (!destFile.exists()) {
            if (!destFile.createNewFile()) {
                throw new IOException("无法创建目标文件: " + destFile.getAbsolutePath());
            }
        }

        try (FileChannel source = new FileInputStream(sourceFile).getChannel();
             FileChannel destination = new FileOutputStream(destFile).getChannel()) {
            destination.transferFrom(source, 0, source.size());
        }
    }

}
