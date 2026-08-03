package helper.creeperbox.utils.render.shader;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform2f;
import static android.opengl.GLES20.glUniform2i;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniform3i;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniform4i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

import android.graphics.Color;
import android.opengl.GLES20;

import helper.creeperbox.utils.render.MatrixUtil;
import helper.creeperbox.utils.render.Render2DUtil;

public class ShaderProgram {

    public void drawQuadFlip(float x, float y, float width, float height) {
        glEnable(GL_TEXTURE_2D);


        float[] vertices = {
                x, y + height,
                x + width, y + height,
                x + width, y,
                x, y
        };


        setMVPMatrix();
        setVerticesArray(vertices, 2);

        float[] texCoords = {
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f
        };
        int pos = glGetAttribLocation(programId, "aUv");
        glEnableVertexAttribArray(pos);
        glVertexAttribPointer(pos, 2, GL_FLOAT, false, 0, Render2DUtil.getFloatBuffer(texCoords));
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        glDisable(GL_TEXTURE_2D);
    }

    private final int programId;

    ShaderProgram(int programId) {
        this.programId = programId;
    }

    public void useShader() {
        glUseProgram(programId);
    }

    public int getProgramId() {
        return programId;
    }

    public void unUseShader() {
        unUseShader(true);
    }

    public void unUseShader(boolean disable) {
        int pos1 = glGetAttribLocation(programId,"vPosition");
        if(pos1!=-1 && disable){
            glDisableVertexAttribArray(pos1);
        }

        int pos2 = glGetAttribLocation(programId,"aUv");
        if(pos2!=-1 && disable){
            glDisableVertexAttribArray(pos2);
        }

        glUseProgram(0);
    }

    public void setUniformf(String name, float... args) {
        int loc = glGetUniformLocation(programId, name);
        switch (args.length) {
            case 1:
                glUniform1f(loc, args[0]);
                break;
            case 2:
                glUniform2f(loc, args[0], args[1]);
                break;
            case 3:
                glUniform3f(loc, args[0], args[1], args[2]);
                break;
            case 4:
                glUniform4f(loc, args[0], args[1], args[2], args[3]);
                break;
        }
    }

    public void drawQuad(float x,float y,float width,float height) {
        glEnable(GL_TEXTURE_2D);

        float[] vertices = {
                x, y + height,
                x + width, y + height,
                x + width, y,
                x, y
        };


        setMVPMatrix();
        setVerticesArray(vertices,2);
        setUV();
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        glDisable(GL_TEXTURE_2D);
    }


    public void setVerticesArray(float[] arr,int size){
        int pos = glGetAttribLocation(programId,"vPosition");
        glEnableVertexAttribArray(pos);
        GLES20.glVertexAttribPointer(pos,size,GL_FLOAT,false,0, Render2DUtil.getFloatBuffer(arr));
    }


    public void setUV(){
        float[] texCoords = {
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f
        };
        int pos = glGetAttribLocation(programId,"aUv");
        glEnableVertexAttribArray(pos);
        glVertexAttribPointer(pos,2,GL_FLOAT,false,0,Render2DUtil.getFloatBuffer(texCoords));
    }

    public void setColor(int color){
        int pos = glGetUniformLocation(programId,"vColor");
        glUniform4f(pos, Color.red(color)/255.0f, Color.green(color)/255.0f, Color.blue(color)/255.0f, Color.alpha(color)/255.0f);
    }

    public void setUniformi(String name, int... args) {
        int loc = glGetUniformLocation(programId, name);
        switch (args.length) {
            case 1:
                glUniform1i(loc, args[0]);
                break;
            case 2:
                glUniform2i(loc, args[0], args[1]);
                break;
            case 3:
                glUniform3i(loc, args[0], args[1], args[2]);
                break;
            case 4:
                glUniform4i(loc, args[0], args[1], args[2], args[3]);
                break;
        }
    }

    public void setMVPMatrix(){
        int pos = glGetUniformLocation(programId, "u_MVPMatrix");
        glUniformMatrix4fv(pos,1,false, MatrixUtil.mvpMatrix(),0);
    }
}
