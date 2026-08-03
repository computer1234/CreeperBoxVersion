package helper.creeperbox.utils.render.bloom;

import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_REPEAT;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDeleteFramebuffers;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;

public class FrameBuffer {


    public int framebufferTextureWidth;
    public int framebufferTextureHeight;
    public int framebufferWidth;
    public int framebufferHeight;

    public int fbo;
    public int textureID;
    public FrameBuffer(int width,int height) {
        this.framebufferWidth = width;
        this.framebufferHeight = height;
        this.framebufferTextureWidth = width;
        this.framebufferTextureHeight = height;

        int[] fbo = new int[1];
        glGenFramebuffers(1,fbo,0);
        this.fbo = fbo[0];

        int[] texture = new int[1];
        glGenTextures(1,texture,0);
        this.textureID = texture[0];

        glBindTexture(GL_TEXTURE_2D, texture[0]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, framebufferTextureWidth, framebufferTextureHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
        glBindTexture(GL_TEXTURE_2D,0);

        glBindFramebuffer(GL_FRAMEBUFFER,fbo[0]);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture[0], 0);
        glClearColor(0f,0f,0f,0f);
        glClear(GL_COLOR_BUFFER_BIT);
        glBindFramebuffer(GL_FRAMEBUFFER,0);
    }

    public void bindFrameBuffer(){
        glBindFramebuffer(GL_FRAMEBUFFER,fbo);
    }

    public void unbindBuffer(){
        glBindFramebuffer(GL_FRAMEBUFFER,0);
    }

    public void bindFrameBufferTexture(){
        glBindTexture(GL_TEXTURE_2D,textureID);
    }

    public int getTextureID() {
        return textureID;
    }

    public void unbindFrameBufferTexture(){
        glBindTexture(GL_TEXTURE_2D,0);
    }


    public void deleteFrameBuffer(){
        glBindFramebuffer(GL_FRAMEBUFFER,0);
        glBindTexture(GL_TEXTURE_2D,0);

        int[] texture = {textureID};
        glDeleteTextures(GL_TEXTURE_2D,texture,0);

        int[] frameBuffer = {fbo};
        glDeleteFramebuffers(GL_FRAMEBUFFER,frameBuffer,0);

        fbo = 0;
        textureID = 0;
    }

    public void frameBufferClear() {
        bindFrameBuffer();
        glClearColor(0f,0f,0f,0f);
        glClear(GL_COLOR_BUFFER_BIT);
        unbindBuffer();
    }
}
