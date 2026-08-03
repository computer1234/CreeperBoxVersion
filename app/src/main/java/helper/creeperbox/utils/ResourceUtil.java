package helper.creeperbox.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Stack;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import helper.creeperbox.clickgui.font.CustomFont;
import helper.creeperbox.clients.CreeperBox;

public class ResourceUtil {

    private static final String SECRET_KEY = "DQxIiwgIm9kaSI6I";
    private static final String INIT_VECTOR = "jRiMzY3zGViYzAS4";
    public static InputStream decryptStream(InputStream encryptedStream) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
        SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

        return new CipherInputStream(encryptedStream, cipher);
    }

    public static InputStream encryptStream(InputStream inputStream) throws Exception {

        IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
        SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

        ByteArrayOutputStream encryptedOutputStream = new ByteArrayOutputStream();

        try (CipherOutputStream cipherOutputStream = new CipherOutputStream(encryptedOutputStream, cipher)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                cipherOutputStream.write(buffer, 0, bytesRead);
            }
        }

        return new ByteArrayInputStream(encryptedOutputStream.toByteArray());
    }




    public static void decryptModel(){
        String[] path = ("animations/csm_sub/164d2046182c41eaabb1e7dae0bcb2a0/animations.json\n" +
                "animations/csm_sub/164d2046182c41eaabb1e7dae0bcb2a0/carryon.animations.json\n" +
                "animations/csm_sub/3a23a51112ad487dad43ed6b76b3405e/animations.json\n" +
                "animations/csm_sub/e563fbccf45e454fb02d535cf219ffb7/animations.json\n" +
                "contents.json\n" +
                "folder_md5.json\n" +
                "manifest.json\n" +
                "models/entity/csm_sub/164d2046182c41eaabb1e7dae0bcb2a0/arm.json\n" +
                "models/entity/csm_sub/164d2046182c41eaabb1e7dae0bcb2a0/main.json\n" +
                "models/entity/csm_sub/3a23a51112ad487dad43ed6b76b3405e/arm.json\n" +
                "models/entity/csm_sub/3a23a51112ad487dad43ed6b76b3405e/main.json\n" +
                "models/entity/csm_sub/e563fbccf45e454fb02d535cf219ffb7/arm.json\n" +
                "models/entity/csm_sub/e563fbccf45e454fb02d535cf219ffb7/main.json\n" +
                "sounds/sound_definitions.json\n" +
                "textures/csm_avatar/164d2046182c41eaabb1e7dae0bcb2a0/cc.png\n" +
                "textures/entity/csm_sub/164d2046182c41eaabb1e7dae0bcb2a0/482c085d0e374a6993005f868869e7df.png\n" +
                "textures/entity/csm_sub/3a23a51112ad487dad43ed6b76b3405e/4b1a68de60334e3eb9649d557fb2b201.png\n" +
                "textures/entity/csm_sub/3a23a51112ad487dad43ed6b76b3405e/4b1a68de60334e3eb9649d557fb2b201_old.png\n" +
                "textures/entity/csm_sub/e563fbccf45e454fb02d535cf219ffb7/6bc25453b9b4419ab557a07605f52646.png\n" +
                "textures/textures_list.json").split("\n");

        Context context = CreeperBox.INSTANCE.context;

        if(new File(context.getDataDir(),"files/games/com.netease/packcache/model/manifest.json").exists()) return;

        for(String fontFile:path) {
            InputStream inputStream = null;
            FileOutputStream outputStream = null;

            try {
                inputStream = decryptStream(ResourceUtil.class.getClassLoader().getResourceAsStream("assets/model/" + fontFile));
                if (inputStream == null) {
                    continue;
                }

                String relativePath = "files/games/com.netease/packcache/model/" + fontFile;
                File outFile = new File(context.getDataDir(), relativePath);

                File current = outFile.getParentFile();
                Stack<File> dirsToCreate = new Stack<>();

                while (current != null) {
                    if (current.exists()) {
                        if (!current.isDirectory()) {
                            current.delete();
                            dirsToCreate.push(current);
                        }
                    } else {
                        dirsToCreate.push(current);
                    }
                    current = current.getParentFile();
                }

                while (!dirsToCreate.isEmpty()) {
                    File dir = dirsToCreate.pop();
                    if (!dir.mkdir() && !dir.isDirectory()) {
                        throw new IOException("Failed to create directory: " + dir.getAbsolutePath());
                    }
                }

                if (outFile.exists() && outFile.isDirectory()) {
                    outFile.delete();
                }

                try (OutputStream outputStream2 = new FileOutputStream(outFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream2.write(buffer, 0, length);
                    }
                    outputStream2.flush();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

}
