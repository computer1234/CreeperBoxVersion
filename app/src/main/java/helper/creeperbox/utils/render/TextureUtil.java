package helper.creeperbox.utils.render;


import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_REPEAT;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexParameteri;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class TextureUtil {

    private static HashMap<String,Integer> resMap = new HashMap<>();


    public static int createTexture(Bitmap bitmap){
        IntBuffer buffer = IntBuffer.allocate(1);
        glGenTextures(1,buffer);
        int textureID = buffer.get();

        glBindTexture(GL_TEXTURE_2D,textureID);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_REPEAT);

        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);

        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        glBindTexture(GL_TEXTURE_2D,0);
        return textureID;
    }

    public static int createTexture(String res){

        if(resMap.containsKey(res)){
            return resMap.get(res);
        }

        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(TextureUtil.class.getClassLoader().getResourceAsStream("assets/"+res));
        }catch (Exception ex){
            ex.printStackTrace();
            return 0;
        }
        if(bitmap == null) return 0;
        int textureID = createTexture(bitmap);
        resMap.put(res,textureID);

        return textureID;
    }

}
