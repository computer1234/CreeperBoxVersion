package helper.creeperbox.feature.component;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;

import helper.creeperbox.clickgui.ClickGUIRenderer;
import helper.creeperbox.clickgui.font.InGameCustomFontRenderer;
import helper.creeperbox.clickgui.font.InGameFonts;
import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.Render3DData;
import helper.creeperbox.feature.event.EventPriority;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.Render2DEvent;
import helper.creeperbox.feature.event.events.Render3DEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.modules.render.MotionCamera;
import helper.creeperbox.sdk.block.EnumFacing;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.*;
import helper.creeperbox.sdk.render.Camera;
import helper.creeperbox.sdk.render.LevelRenderer;
import helper.creeperbox.utils.ResourceUtil;
import helper.creeperbox.utils.mc.TargetUtil;
import helper.creeperbox.utils.render.MatrixUtil;
import helper.creeperbox.utils.render.Render2DUtil;
import helper.creeperbox.utils.render.TextureUtil;
import helper.creeperbox.utils.render.bloom.InGameBloomUtil;
import helper.creeperbox.utils.render.shader.ShaderProgram;
import helper.creeperbox.utils.render.shader.ShaderUtil;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.opengl.GLES20.*;
import static android.opengl.GLES20.GL_DEPTH_TEST;

public class RenderHelperComponent {


    public static int getTexture(String name){
        switch (name){
            case "雪花":
                return snowTexture;
            case "五角星":
                return starTexture;
            case "美元":
                return dollarTexture;
            case "光晕":
            case "火焰":
                return fireFlyTexture;
            case "气泡":
                return bubbleTexture;
            case "心形":
            default:
                return healthTexture;
        }
    }



    public static void gradientImageXZ(int textureID,final float x, final float y,final float z, final float width, final float height,int color1,int color2,int color3,int color4,float[] mvpMatrix){

        gradientImageProgram.useShader();
        glEnable(GL_TEXTURE_2D);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D,textureID);

        int pos1 = GLES20.glGetUniformLocation(gradientImageProgram.getProgramId(), "u_first_color");
        GLES20.glUniform4f(pos1, (float)Color.red(color1) / 255.0F, (float)Color.green(color1) / 255.0F, (float)Color.blue(color1) / 255.0F, (float)Color.alpha(color1) / 255.0F);

        int pos2 = GLES20.glGetUniformLocation(gradientImageProgram.getProgramId(), "u_second_color");
        GLES20.glUniform4f(pos2, (float)Color.red(color2) / 255.0F, (float)Color.green(color2) / 255.0F, (float)Color.blue(color2) / 255.0F, (float)Color.alpha(color2) / 255.0F);

        int pos3 = GLES20.glGetUniformLocation(gradientImageProgram.getProgramId(), "u_third_color");
        GLES20.glUniform4f(pos3, (float)Color.red(color3) / 255.0F, (float)Color.green(color3) / 255.0F, (float)Color.blue(color3) / 255.0F, (float)Color.alpha(color3) / 255.0F);

        int pos4 = GLES20.glGetUniformLocation(gradientImageProgram.getProgramId(), "u_fourth_color");
        GLES20.glUniform4f(pos4, (float)Color.red(color4) / 255.0F, (float)Color.green(color4) / 255.0F, (float)Color.blue(color4) / 255.0F, (float)Color.alpha(color4) / 255.0F);

        int pos = GLES20.glGetUniformLocation(gradientImageProgram.getProgramId(), "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(pos, 1, false, mvpMatrix, 0);

        float[] vertices = new float[]{
                x, y, z,
                x + width, y, z,
                x + width, y, z + height,
                x, y, z + height
        };

        float[] texCoords = new float[]{
                0.0F, 1.0F,
                1.0F, 1.0F,
                1.0F, 0.0F,
                0.0F, 0.0F
        };

        float[] combinedData = new float[vertices.length + texCoords.length];

        for (int i = 0; i < vertices.length / 3; i++) {
            combinedData[i * 5] = vertices[i * 3];
            combinedData[i * 5 + 1] = vertices[i * 3 + 1];
            combinedData[i * 5 + 2] = vertices[i * 3 + 2];
            combinedData[i * 5 + 3] = texCoords[i * 2];
            combinedData[i * 5 + 4] = texCoords[i * 2 + 1];
        }

        int[] vbo = new int[1];
        GLES20.glGenBuffers(1, vbo, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vertexBuffer = Render2DUtil.getFloatBuffer(combinedData);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_DYNAMIC_DRAW);

        int vPosition = GLES20.glGetAttribLocation(gradientImageProgram.getProgramId(), "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 5 * 4, 0);

        int texCoordHandle = GLES20.glGetAttribLocation(gradientImageProgram.getProgramId(), "aUv");
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 5 * 4, 3 * 4);

        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        glDisable(GL_TEXTURE_2D);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glDeleteBuffers(1, vbo, 0);
        glBindTexture(GL_TEXTURE_2D,0);

        gradientImageProgram.unUseShader(false);
    }


    public static void rectangle(float x, float y, float z,
                                 EnumFacing facing, int color, float[] mvpMatrix) {

        defaultProgram.useShader();
        defaultProgram.setColor(color);

        int pos = GLES20.glGetUniformLocation(defaultProgram.getProgramId(), "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(pos, 1, false, mvpMatrix, 0);

        float[] vertices = getVerticesForDirection(x, y, z, facing);

        float[] texCoords = new float[]{0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F};

        float[] combinedData = new float[vertices.length + texCoords.length];

        for (int i = 0; i < vertices.length / 3; i++) {
            combinedData[i * 5] = vertices[i * 3];
            combinedData[i * 5 + 1] = vertices[i * 3 + 1];
            combinedData[i * 5 + 2] = vertices[i * 3 + 2];
            combinedData[i * 5 + 3] = texCoords[i * 2];
            combinedData[i * 5 + 4] = texCoords[i * 2 + 1];
        }

        int[] vbo = new int[1];
        GLES20.glGenBuffers(1, vbo, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vertexBuffer = Render2DUtil.getFloatBuffer(combinedData);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_DYNAMIC_DRAW);

        int vPosition = GLES20.glGetAttribLocation(defaultProgram.getProgramId(), "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 5 * 4, 0);

        int texCoordHandle = GLES20.glGetAttribLocation(defaultProgram.getProgramId(), "aUv");
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 5 * 4, 3 * 4);

        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        glDisable(GL_TEXTURE_2D);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glDeleteBuffers(1, vbo, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
        defaultProgram.unUseShader(false);
    }
    private static float[] getVerticesForDirection(float x, float y, float z, EnumFacing facing) {
        switch (facing) {
            case UP:
                return new float[]{
                        x, y, z,
                        x + 1f, y, z,
                        x + 1f, y, z + 1f,
                        x, y, z + 1f
                };
            case DOWN:
                return new float[]{
                        x, y - 1f, z,
                        x + 1f, y - 1f, z,
                        x + 1f, y - 1f, z + 1f,
                        x, y - 1f, z + 1f
                };
            case NORTH:
                return new float[]{
                        x, y, z,
                        x + 1f, y, z,
                        x + 1f, y - 1f, z,
                        x, y - 1f, z
                };
            case SOUTH:
                return new float[]{
                        x, y, z + 1f,
                        x + 1f, y, z + 1f,
                        x + 1f, y - 1f, z + 1f,
                        x, y - 1f, z + 1f
                };
            case EAST:
                return new float[]{
                        x + 1f, y, z,
                        x + 1f, y, z + 1f,
                        x + 1f, y - 1f, z + 1f,
                        x + 1f, y - 1f, z
                };
            case WEST:
                return new float[]{
                        x, y, z,
                        x, y, z + 1f,
                        x, y - 1f, z + 1f,
                        x, y - 1f, z
                };
            default:
                return new float[]{
                        x, y, z,
                        x + 1f, y, z,
                        x + 1f, y - 1f, z,
                        x, y - 1f, z
                };
        }
    }





    public static void drawLine(List<Vec3f> points, int color, float[] mvpMatrix) {
        if (points == null || points.size() < 2) {
            return;
        }

        glLineWidth(3f);
        defaultProgram.useShader();

        int mvpMatrixHandle = GLES20.glGetUniformLocation(defaultProgram.getProgramId(), "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        int pos = glGetUniformLocation(defaultProgram.getProgramId(), "vColor");
        glUniform4f(pos, Color.red(color)/255.0f, Color.green(color)/255.0f,
                Color.blue(color)/255.0f, Color.alpha(color)/255.0f);

        float[] vertices = new float[points.size() * 3];
        for (int i = 0; i < points.size(); i++) {
            Vec3f point = points.get(i);
            vertices[i * 3] = point.x;
            vertices[i * 3 + 1] = point.y;
            vertices[i * 3 + 2] = point.z;
        }

        int[] vbo = new int[1];
        GLES20.glGenBuffers(1, vbo, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vertexBuffer = Render2DUtil.getFloatBuffer(vertices);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4,
                vertexBuffer, GLES20.GL_STATIC_DRAW);

        int vPosition = GLES20.glGetAttribLocation(defaultProgram.getProgramId(), "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, points.size());

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glDeleteBuffers(1, vbo, 0);
        defaultProgram.unUseShader(false);

    }

    public static void drawLine(float x, float y, float z, float toX, float toY, float toZ, int color, float[] mvpMatrix) {
        glLineWidth(3f);
        defaultProgram.useShader();
        int mvpMatrixHandle = GLES20.glGetUniformLocation(defaultProgram.getProgramId(), "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        int pos = glGetUniformLocation(defaultProgram.getProgramId(),"vColor");
        glUniform4f(pos, Color.red(color)/255.0f, Color.green(color)/255.0f, Color.blue(color)/255.0f, Color.alpha(color)/255.0f);


        float[] vertices = new float[] {
                x, y, z,
                toX, toY, toZ
        };

        int[] vbo = new int[1];
        GLES20.glGenBuffers(1, vbo, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vertexBuffer = Render2DUtil.getFloatBuffer(vertices);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STATIC_DRAW);

        int vPosition = GLES20.glGetAttribLocation(defaultProgram.getProgramId(), "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glDeleteBuffers(1, vbo, 0);

        defaultProgram.unUseShader(false);

    }

    public static void scissorEnd(){
        glDisable(GL_SCISSOR_TEST);
    }


    public static void scissorStart(float x,float y,float width,float height){
        int[] viewport = new int[4];
        glGetIntegerv(GL_VIEWPORT, viewport, 0);
        glEnable(GL_SCISSOR_TEST);
        glScissor((int) x, (int) (viewport[3]-y-height), (int) width, (int) height);
    }


    private void setUpMatrix(){
        MatrixUtil.matrixMode(MatrixUtil.PROJECTION_MATRIX);
        MatrixUtil.pushMatrix();
        MatrixUtil.loadIdentity();
        int[] viewport = new int[4];
        glGetIntegerv(GL_VIEWPORT, viewport, 0);
        MatrixUtil.ortho(0, viewport[2], viewport[3], 0, -100, 100);
        MatrixUtil.matrixMode(MatrixUtil.MODEL_MATRIX);
        MatrixUtil.pushMatrix();
        MatrixUtil.loadIdentity();
    }


    public static void drawGradientRoundOld(float x, float y, float width, float height, float radius, int firstColor, int secondColor, boolean vertical) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);

        rgqProgram3.useShader();
        rgqProgram3.setUniformf("u_size",width,height);
        rgqProgram3.setUniformf("u_radius",radius);
        rgqProgram3.setUniformf("u_first_color", Color.red(firstColor) / 255.0F,Color.green(firstColor) / 255.0F,Color.blue(firstColor) / 255.0F,Color.alpha(firstColor) / 255.0F);
        rgqProgram3.setUniformf("u_second_color",Color.red(secondColor) / 255.0F,Color.green(secondColor) / 255.0F,Color.blue(secondColor) / 255.0F,Color.alpha(secondColor) / 255.0F);
        rgqProgram3.setUniformi( "u_direction", vertical ? 1 : 0);

        glEnable(GL_TEXTURE_2D);
        int pos = GLES20.glGetUniformLocation(rgqProgram3.getProgramId(), "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(pos, 1, false, MatrixUtil.mvpMatrix(), 0);
        float[] vertices = new float[]{
                x, y + height, 0,
                x + width, y + height, 0,
                x + width, y, 0,
                x, y, 0
        };
        float[] texCoords = new float[]{0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F};

        float[] combinedData = new float[vertices.length + texCoords.length];

        for (int i = 0; i < vertices.length / 3; i++) {
            combinedData[i * 5] = vertices[i * 3];
            combinedData[i * 5 + 1] = vertices[i * 3 + 1];
            combinedData[i * 5 + 2] = vertices[i * 3 + 2];
            combinedData[i * 5 + 3] = texCoords[i * 2];
            combinedData[i * 5 + 4] = texCoords[i * 2 + 1];
        }

        int[] vbo = new int[1];
        GLES20.glGenBuffers(1, vbo, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vertexBuffer = Render2DUtil.getFloatBuffer(combinedData);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_DYNAMIC_DRAW);

        int vPosition = GLES20.glGetAttribLocation(rgqProgram3.getProgramId(), "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 5 * 4, 0);

        int texCoordHandle = GLES20.glGetAttribLocation(rgqProgram3.getProgramId(), "aUv");
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 5 * 4, 3 * 4);

        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        glDisable(GL_TEXTURE_2D);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glDeleteBuffers(1, vbo, 0);

        rgqProgram3.unUseShader(false);

    }


    public static void drawLight(int textureID,final float x, final float y,final float width, final float height,int color){

        imageProgram2.useShader();
        glEnable(GL_TEXTURE_2D);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D,textureID);

        imageProgram2.setUniformi("uSampler",0);
        imageProgram2.setColor(color);

        int pos = GLES20.glGetUniformLocation(imageProgram2.getProgramId(), "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(pos, 1, false, MatrixUtil.mvpMatrix(), 0);

        float[] vertices = new float[]{
                x, y + height, 0,
                x + width, y + height, 0,
                x + width, y, 0,
                x, y, 0
        };
        float[] texCoords = new float[]{0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F};

        float[] combinedData = new float[vertices.length + texCoords.length];

        for (int i = 0; i < vertices.length / 3; i++) {
            combinedData[i * 5] = vertices[i * 3];
            combinedData[i * 5 + 1] = vertices[i * 3 + 1];
            combinedData[i * 5 + 2] = vertices[i * 3 + 2];
            combinedData[i * 5 + 3] = texCoords[i * 2];
            combinedData[i * 5 + 4] = texCoords[i * 2 + 1];
        }

        int[] vbo = new int[1];
        GLES20.glGenBuffers(1, vbo, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vertexBuffer = Render2DUtil.getFloatBuffer(combinedData);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_DYNAMIC_DRAW);

        int vPosition = GLES20.glGetAttribLocation(imageProgram2.getProgramId(), "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 5 * 4, 0);

        int texCoordHandle = GLES20.glGetAttribLocation(imageProgram2.getProgramId(), "aUv");
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 5 * 4, 3 * 4);

        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        glDisable(GL_TEXTURE_2D);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glDeleteBuffers(1, vbo, 0);
        glBindTexture(GL_TEXTURE_2D,0);

        imageProgram2.unUseShader(false);

    }


    public static void imageXY(int textureID,final float x, final float y, float z,final float width, final float height,int color,float[] mvpMatrix){
        imageProgram.useShader();
        glEnable(GL_TEXTURE_2D);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D,textureID);

        imageProgram.setUniformi("uSampler",0);
        imageProgram.setColor(color);

        int pos = GLES20.glGetUniformLocation(imageProgram.getProgramId(), "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(pos, 1, false, mvpMatrix, 0);

        float[] vertices = new float[]{
                x, y + height, z,
                x + width, y + height, z,
                x + width, y, z,
                x, y, z
        };
        float[] texCoords = new float[]{0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F};

        float[] combinedData = new float[vertices.length + texCoords.length];

        for (int i = 0; i < vertices.length / 3; i++) {
            combinedData[i * 5] = vertices[i * 3];
            combinedData[i * 5 + 1] = vertices[i * 3 + 1];
            combinedData[i * 5 + 2] = vertices[i * 3 + 2];
            combinedData[i * 5 + 3] = texCoords[i * 2];
            combinedData[i * 5 + 4] = texCoords[i * 2 + 1];
        }

        int[] vbo = new int[1];
        GLES20.glGenBuffers(1, vbo, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vertexBuffer = Render2DUtil.getFloatBuffer(combinedData);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_DYNAMIC_DRAW);

        int vPosition = GLES20.glGetAttribLocation(imageProgram.getProgramId(), "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 5 * 4, 0);

        int texCoordHandle = GLES20.glGetAttribLocation(imageProgram.getProgramId(), "aUv");
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 5 * 4, 3 * 4);

        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        glDisable(GL_TEXTURE_2D);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glDeleteBuffers(1, vbo, 0);
        glBindTexture(GL_TEXTURE_2D,0);

        imageProgram.unUseShader(false);
    }


    public static void image(int textureID,final float x, final float y,final float width, final float height,int color){

        imageProgram.useShader();
        glEnable(GL_TEXTURE_2D);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D,textureID);

        imageProgram.setUniformi("uSampler",0);
        imageProgram.setColor(color);

        int pos = GLES20.glGetUniformLocation(imageProgram.getProgramId(), "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(pos, 1, false, MatrixUtil.mvpMatrix(), 0);

        float[] vertices = new float[]{
                x, y + height, 0,
                x + width, y + height, 0,
                x + width, y, 0,
                x, y, 0
        };
        float[] texCoords = new float[]{0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F};

        float[] combinedData = new float[vertices.length + texCoords.length];

        for (int i = 0; i < vertices.length / 3; i++) {
            combinedData[i * 5] = vertices[i * 3];
            combinedData[i * 5 + 1] = vertices[i * 3 + 1];
            combinedData[i * 5 + 2] = vertices[i * 3 + 2];
            combinedData[i * 5 + 3] = texCoords[i * 2];
            combinedData[i * 5 + 4] = texCoords[i * 2 + 1];
        }

        int[] vbo = new int[1];
        GLES20.glGenBuffers(1, vbo, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vertexBuffer = Render2DUtil.getFloatBuffer(combinedData);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_DYNAMIC_DRAW);

        int vPosition = GLES20.glGetAttribLocation(imageProgram.getProgramId(), "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 5 * 4, 0);

        int texCoordHandle = GLES20.glGetAttribLocation(imageProgram.getProgramId(), "aUv");
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 5 * 4, 3 * 4);

        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        glDisable(GL_TEXTURE_2D);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glDeleteBuffers(1, vbo, 0);
        glBindTexture(GL_TEXTURE_2D,0);

        imageProgram.unUseShader(false);
    }


    private void endMatrix(){
        MatrixUtil.matrixMode(MatrixUtil.PROJECTION_MATRIX);
        MatrixUtil.popMatrix();
        MatrixUtil.matrixMode(MatrixUtil.MODEL_MATRIX);
        MatrixUtil.popMatrix();
    }

    private static int currentProgram;
    private static int currentActiveTexture;
    private static int sampleZeroBindingTexture;
    private static boolean usingDepth;


    public static void saveOpenGLCtx(){
        int[] currentProgram = new int[1];
        glGetIntegerv(GL_CURRENT_PROGRAM, currentProgram, 0);
        RenderHelperComponent.currentProgram = currentProgram[0];


        int[] currentActiveTexture = new int[1];
        glGetIntegerv(GL_ACTIVE_TEXTURE, currentActiveTexture, 0);
        RenderHelperComponent.currentActiveTexture = currentActiveTexture[0];


        glActiveTexture(GL_TEXTURE0);
        int[] currentTexture = new int[1];
        glGetIntegerv(GL_TEXTURE_BINDING_2D, currentTexture, 0);
        glActiveTexture(currentActiveTexture[0]);
        RenderHelperComponent.sampleZeroBindingTexture = currentTexture[0];

        usingDepth = glIsEnabled(GL_DEPTH_TEST);
        glEnable(GL_DEPTH_TEST);
    }


    public static void loadOpenGLCtx(){
        glUseProgram(currentProgram);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D,sampleZeroBindingTexture);
        glActiveTexture(currentActiveTexture);
        if(usingDepth){
            glEnable(GL_DEPTH_TEST);
        }else{
            glDisable(GL_DEPTH_TEST);
        }
    }



    @SubscribeEvent(EventPriority.VERY_LOW)
    public void preRender2D(Render2DEvent event){
        initResource();
        setUpMatrix();
        saveOpenGLCtx();
    }

    @SubscribeEvent(EventPriority.SYSTEM)
    public void postRender2D(Render2DEvent event){
        bloomUtil.draw();
        endMatrix();
        loadOpenGLCtx();
    }

    public static Render3DData render3DData;


    public static HashMap<Long,Vec4f> projectionsData = new HashMap<>();


    @SubscribeEvent
    public void hideHUD(Render2DEvent event){
        if(ClickGUIRenderer.toggleGUI || !ClickGUIRenderer.guiAnimation.isFinished()) glColorMask(false,false,false,false);
    }


    public static boolean refreshNextFrame;

    @SubscribeEvent
    public void onRefresh(Render3DEvent event){
        if(refreshNextFrame){
            event.getLevelRenderer().refreshWorld();
            refreshNextFrame = false;
        }
    }



    @SubscribeEvent(EventPriority.VERY_LOW)
    public void preRender3D(Render3DEvent event) {
        LevelRenderer renderer = event.getLevelRenderer();
        render3DData = new Render3DData();
        render3DData.cameraPos = renderer.getCameraPos();

        if(MotionCamera.currentPos !=null){
            render3DData.cameraPos = MotionCamera.currentPos;
        }

        Camera camera = event.getCtx().getCamera();
        render3DData.modelMatrix = camera.getModelMatrix();
        render3DData.proMatrix = camera.getProjectionMatrix();
        render3DData.viewMatrix = camera.getViewMatrix();
        render3DData.partialTicks = event.getCtx().getPartialTicks();
        float[] mvpMatrix = new float[16];
        float[] tempMatrix = new float[16];
        android.opengl.Matrix.multiplyMM(tempMatrix, 0, render3DData.proMatrix.m, 0, render3DData.viewMatrix.m, 0);
        android.opengl.Matrix.multiplyMM(mvpMatrix, 0, tempMatrix, 0, render3DData.modelMatrix.m, 0);
        render3DData.mvpMatrix = new Matrix(mvpMatrix);


        projectionsData.clear();
        EntityLocalPlayer player = CreeperBox.INSTANCE.getLocalPlayer();
        if (player!=null){
            for(EntityActor actor : TargetUtil.findTarget(player,200)){
                Vec3f playerPos = actor.getPos();
                Vec3f oldPos = actor.getPosPrev();
                float x = oldPos.x + (playerPos.x - oldPos.x) * render3DData.partialTicks;
                float y = oldPos.y + (playerPos.y - oldPos.y) * render3DData.partialTicks;
                float z = oldPos.z + (playerPos.z - oldPos.z) * render3DData.partialTicks;
                Vec2f size = actor.getHitBoxSize();
                AxisAlignedBB aabb = new AxisAlignedBB(x-size.x/2f,y-size.y,z-size.x/2f,x+size.x/2f,y+0.2f,z+size.x/2f);

                final List<Vec3f> vectors = Arrays.asList(new Vec3f((float) aabb.minX, (float) aabb.minY, (float) aabb.minZ),
                        new Vec3f((float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ),
                        new Vec3f((float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ),
                        new Vec3f((float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ),
                        new Vec3f((float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ),
                        new Vec3f((float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ),
                        new Vec3f((float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ),
                        new Vec3f((float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ));

                Vec4f position = null;
                for (Vec3f vector : vectors) {
                    vector = worldToScreen(render3DData,vector);
                    if (vector.z > 0 && vector.z < 1) {
                        if (position == null) position = new Vec4f(vector.x, vector.y, vector.z, 0);
                        position = new Vec4f(Math.min(vector.x, position.x),Math.min(vector.y, position.y),Math.max(vector.x, position.z),Math.max(vector.y, position.w));
                    }
                }

                if(position!=null) projectionsData.put(actor.getRuntimeID(),position);
            }
        }
    }


    public static Vec3f worldToScreen(Render3DData data, Vec3f pos) {

        Vec3f cameraPos = data.cameraPos;
        float deltaX = pos.x - cameraPos.x;
        float deltaY = pos.y - cameraPos.y;
        float deltaZ = pos.z - cameraPos.z;

        float[] worldPos = {deltaX, deltaY, deltaZ, 1.0f};

        float[] viewPos = new float[4];
        android.opengl.Matrix.multiplyMV(viewPos, 0, data.viewMatrix.m, 0, worldPos, 0);

        float[] clipPos = new float[4];
        android.opengl.Matrix.multiplyMV(clipPos, 0, data.proMatrix.m, 0, viewPos, 0);

        if(clipPos[3] != 0){
            float ndcX = clipPos[0] / clipPos[3];
            float ndcY = clipPos[1] / clipPos[3];

            int[] viewport = new int[4];
            glGetIntegerv(GL_VIEWPORT, viewport, 0);

            return new Vec3f((ndcX + 1) * 0.5f * viewport[2] + viewport[0],
                    (1 - (ndcY + 1) * 0.5f) * viewport[3] + viewport[1],
                    clipPos[2]/clipPos[3]);
        }

        return null;
    }

    private void initResource(){
        initShader();
        initTexture();
        initFont();
    }


    public static ShaderProgram rgqProgram;
    public static ShaderProgram rgqProgram2;
    public static ShaderProgram shadowProgram;
    public static ShaderProgram imageProgram;
    public static ShaderProgram defaultProgram;
    public static ShaderProgram trailProgram;
    public static ShaderProgram gradientImageProgram;
    public static ShaderProgram skinProgram;
    public static InGameBloomUtil bloomUtil;
    public static ShaderProgram rgqProgram3;
    public static ShaderProgram roundImageProgram;
    public static ShaderProgram imageProgram2;

    private void initShader(){

        if(bloomUtil == null){
            bloomUtil = new InGameBloomUtil(20);
        }

        if(defaultProgram==null){
            defaultProgram = ShaderUtil.defaultShader();
        }

        if(rgqProgram == null){
            rgqProgram = ShaderUtil.createShader(
                    "precision mediump float;\n" +
                            "uniform int u_direction;\n" +
                            "uniform vec4 color1;\n" +
                            "uniform vec4 color2;\n" +
                            "uniform vec2 size;\n" +
                            "uniform float radius;\n" +
                            "varying vec2 vUv;\n" +
                            "\n" +
                            "float roundedBoxSDF(vec2 p, vec2 b, float r) {\n" +
                            "    float n = 4.0;\n" +
                            "    p = abs(p) - b + r;\n" +
                            "    if (min(p.x, p.y) < 0.0) {\n" +
                            "        return length(max(p, 0.0)) - r;\n" +
                            "    } else {\n" +
                            "        return pow(pow(p.x, n) + pow(p.y, n), 1.0/n) - r;\n" +
                            "    }\n" +
                            "}\n" +
                            "\n" +
                            "void main(void)\n" +
                            "{\n" +
                            "    vec4 currentColor = mix(color1, color2, u_direction > 0 ? vUv.y : vUv.x);\n" +
                            "    vec2 centeredCoord = vUv - vec2(0.5);\n" +
                            "    float aspect = size.x / size.y;\n" +
                            "    centeredCoord.x *= aspect;\n" +
                            "    vec2 halfSize = vec2(aspect * 0.5, 0.5);\n" +
                            "    float d = roundedBoxSDF(centeredCoord, halfSize, radius / size.y);\n" +
                            "    float smoothWidth = 0.001;\n" +
                            "    float fill = smoothstep(-smoothWidth, smoothWidth, -d);\n" +
                            "    gl_FragColor = currentColor * fill;\n" +
                            "    if(gl_FragColor.a <= 0.0) discard;\n" +
                            "}");
        }

        if(rgqProgram2 == null){
            rgqProgram2 = ShaderUtil.createShader(
                    "precision mediump float;\n" +
                            "uniform vec4 color1;\n" +
                            "uniform vec4 color2;\n" +
                            "uniform vec4 color3;\n" +
                            "uniform vec4 color4;\n" +
                            "uniform vec2 size;\n" +
                            "uniform vec2 location;\n" +
                            "uniform float radius;\n" +
                            "varying vec2 vUv;\n" +
                            "\n" +
                            "float roundedBoxSDF(vec2 p, vec2 b, float r) {\n" +
                            "    float n = 4.0;\n" +
                            "    p = abs(p) - b + r;\n" +
                            "    if (min(p.x, p.y) < 0.0) {\n" +
                            "        return length(max(p, 0.0)) - r;\n" +
                            "    } else {\n" +
                            "        return pow(pow(p.x, n) + pow(p.y, n), 1.0/n) - r;\n" +
                            "    }\n" +
                            "}\n" +
                            "\n" +
                            "void main(void)\n" +
                            "{\n" +
                            "    vec4 currentColor = mix(mix(color1, color2, vUv.x),mix(color3,color4,vUv.x),vUv.y);\n" +
                            "    vec2 centeredCoord = vUv - vec2(0.5);\n" +
                            "    float aspect = size.x / size.y;\n" +
                            "    centeredCoord.x *= aspect;\n" +
                            "    vec2 halfSize = vec2(aspect * 0.5, 0.5);\n" +
                            "    float d = roundedBoxSDF(centeredCoord, halfSize, radius / size.y);\n" +
                            "    float smoothWidth = 0.001;\n" +
                            "    float fill = smoothstep(-smoothWidth, smoothWidth, -d);\n" +
                            "    gl_FragColor = currentColor * fill;\n" +
                            "    if(gl_FragColor.a <= 0.0) discard;\n" +
                            "}");
        }

        if(shadowProgram == null){
            shadowProgram = ShaderUtil.createShader("precision mediump float;\n" +
                    "\n" +
                    "uniform vec4 u_first_color;\n" +
                    "uniform vec4 u_second_color;\n" +
                    "uniform vec2 size;\n" +
                    "uniform vec2 location;\n" +
                    "uniform int u_direction;\n" +
                    "uniform float radius;\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "float roundedBoxSDF(vec2 center, vec2 size, float radius) {\n" +
                    "    return length(max(abs(center) - size + radius, 0.0)) - radius;\n" +
                    "}\n" +
                    "\n" +
                    "\n" +
                    "vec4 createGradient(vec2 coords, vec4 u_first_color, vec4 u_second_color,int u_direction){\n" +
                    "    vec4 color = mix(u_first_color, u_second_color, u_direction > 0 ? coords.y : coords.x);\n" +
                    "\treturn color;\n" +
                    "}\n" +
                    "\n" +
                    "\n" +
                    "void main(void)\n" +
                    "{\n" +
                    "    vec2 centeredCoord = gl_FragCoord.xy - location - (size / 2.0);\n" +
                    "    vec2 normCoord = (gl_FragCoord.xy - location) / size;\n" +
                    "    vec4 gradientColor = createGradient(normCoord,u_first_color,u_second_color,u_direction);\n" +
                    "    float distance = roundedBoxSDF(centeredCoord, size / 2.0, radius);\n" +
                    "    float smoothedAlpha = (1.0 - smoothstep(-10., 10., distance)) * gradientColor.a;\n" +
                    "    float smoothedAlpha2 = (1.0 - smoothstep(-1., 1., distance)) * gradientColor.a;\n" +
                    "\t\n" +
                    "    if (smoothedAlpha2 < 1.0) {\n" +
                    "        gl_FragColor = vec4(gradientColor.rgb,smoothedAlpha);\n" +
                    "    } else {\n" +
                    "        discard;\n" +
                    "    }\n" +
                    "}\n" +
                    "\n");
        }

        if(imageProgram == null){
            imageProgram = ShaderUtil.createShader("precision mediump float;\n" +
                    "uniform vec4 vColor;\n" +
                    "varying vec2 vUv;\n" +
                    "uniform sampler2D uSampler;\n" +
                    "void main(){\n" +
                    "  gl_FragColor = texture2D(uSampler,vUv) * vColor;\n" +
                    "}");
        }

        if(imageProgram2 == null){
            imageProgram2 = ShaderUtil.createShader("precision mediump float;\n" +
                    "uniform vec4 vColor;\n" +
                    "varying vec2 vUv;\n" +
                    "uniform sampler2D uSampler;\n" +
                    "void main(){\n" +
                    "  gl_FragColor = vColor;\n" +
                    "  gl_FragColor.a = texture2D(uSampler,vUv).a;\n"+
                    "}");
        }



        if(trailProgram == null){

            String vertexShader = "#version 320 es\n" +
                    "layout(location = 0) in vec4 vPosition;\n" +
                    "layout(location = 1) in vec4 aColor;\n" +
                    "uniform mat4 u_MVPMatrix;\n" +
                    "out vec4 vColor;\n" +
                    "\n" +
                    "void main() {\n" +
                    "    gl_Position = u_MVPMatrix * vPosition;\n" +
                    "    vColor = aColor;\n" +
                    "}";

            String fragmentShader = "#version 320 es\n" +
                    "precision mediump float;\n" +
                    "in vec4 vColor;\n" +
                    "out vec4 fragColor;\n" +
                    "\n" +
                    "void main() {\n" +
                    "    fragColor = vColor;\n" +
                    "}";

            trailProgram = ShaderUtil.createShader(fragmentShader,vertexShader);
        }

        if(gradientImageProgram == null){
            gradientImageProgram = ShaderUtil.createShader("precision mediump float;\n" +
                    "uniform vec4 u_first_color;\n" +
                    "uniform vec4 u_second_color;\n" +
                    "uniform vec4 u_third_color;\n" +
                    "uniform vec4 u_fourth_color;\n" +
                    "varying vec2 vUv;\n" +
                    "uniform sampler2D uSampler;\n" +
                    "void main(){\n" +
                    "  vec4 top_color = mix(u_first_color, u_second_color, vUv.x);" +
                    "  vec4 bottom_color = mix(u_third_color, u_fourth_color, vUv.x);" +
                    "  vec4 final_color = mix(top_color, bottom_color, vUv.y);" +
                    "  vec4 texColor = texture2D(uSampler, vUv);\n" +
                    "  gl_FragColor = vec4(texColor.rgb, texColor.a) * final_color;" +
                    "}");
        }

        if(skinProgram == null){
            skinProgram = ShaderUtil.createShader("precision mediump float;\n" +
                    "uniform float u_radius;\n" +
                    "uniform vec2 u_size;\n" +
                    "uniform vec2 uvStart;\n" +
                    "uniform vec2 uvEnd;\n" +
                    "uniform vec4 vColor;\n" +
                    "varying vec2 vUv;\n" +
                    "uniform sampler2D uSampler;\n" +
                    "void main(){\n" +
                    "    vec2 uv = (vUv - uvStart)/(uvEnd - uvStart);\n" +
                    "    vec4 col = texture2D(uSampler,vUv);\n" +
                    "    gl_FragColor = vec4(col.rgb, col.a * smoothstep(1.0, 0.0, length(max((abs(uv - 0.5) + 0.5) * u_size - u_size + u_radius, 0.0)) - u_radius + 0.5)) * vColor;\n" +
                    "}");
        }


        if(rgqProgram3 == null){
            rgqProgram3 = ShaderUtil.createShader(
                    "precision mediump float;\n" +
                            "uniform float u_radius;\n" +
                            "uniform vec4 u_first_color;\n" +
                            "uniform vec4 u_second_color;\n" +
                            "uniform vec2 u_size;\n" +
                            "uniform int u_direction;\n" +
                            "varying vec2 vUv;\n" +
                            "void main(){\n" +
                            "  vec4 color = mix(u_first_color, u_second_color, u_direction > 0 ? vUv.y : vUv.x);\n" +
                            "  gl_FragColor = vec4(color.rgb, color.a * smoothstep(1.0, 0.0, length(max((abs(vUv - 0.5) + 0.5) * u_size - u_size + u_radius, 0.0)) - u_radius + 0.5));\n" +
                            "  if(gl_FragColor.a <= 0.0){\n" +
                            "    discard;\n" +
                            "  }else{\n" +
                            "    gl_FragColor.a = 1.0 * color.a;\n"+
                            "  }\n" +
                            "}");

        }

        if(roundImageProgram == null){
            roundImageProgram = ShaderUtil.createShader("precision mediump float;\n" +
                    "uniform vec2 size;\n" +
                    "uniform float radius;\n" +
                    "varying vec2 vUv;\n" +
                    "uniform sampler2D uSampler;\n" +
                    "\n" +
                    "float roundedBoxSDF(vec2 p, vec2 b, float r) {\n" +
                    "    float n = 4.0;\n" +
                    "    p = abs(p) - b + r;\n" +
                    "    if (min(p.x, p.y) < 0.0) {\n" +
                    "        return length(max(p, 0.0)) - r;\n" +
                    "    } else {\n" +
                    "        return pow(pow(p.x, n) + pow(p.y, n), 1.0/n) - r;\n" +
                    "    }\n" +
                    "}\n" +
                    "\n" +
                    "void main(void)\n" +
                    "{\n" +
                    "    vec4 col = texture2D(uSampler,vUv);\n" +
                    "    vec2 centeredCoord = vUv - vec2(0.5);\n" +
                    "    float aspect = size.x / size.y;\n" +
                    "    centeredCoord.x *= aspect;\n" +
                    "    vec2 halfSize = vec2(aspect * 0.5, 0.5);\n" +
                    "    float d = roundedBoxSDF(centeredCoord, halfSize, radius / size.y);\n" +
                    "    float smoothWidth = 0.001;\n" +
                    "    float fill = smoothstep(-smoothWidth, smoothWidth, -d);\n" +
                    "    gl_FragColor = col * fill;\n" +
                    "    if(gl_FragColor.a <= 0.0) discard;\n" +
                    "}");
        }

    }


    public static InGameCustomFontRenderer pingfang25;

    public static InGameCustomFontRenderer pingfang20;
    public static InGameCustomFontRenderer pingfang30;
    public static InGameCustomFontRenderer pingfang40;
    public static InGameCustomFontRenderer pingfang18;
    public static InGameCustomFontRenderer anta40;
    public static InGameCustomFontRenderer pingfang_medium_30;

    private void initFont(){

        if(pingfang18 == null){
            pingfang18 = InGameFonts.getFontRenderer("pingfang.ttf",18);
        }

        if(pingfang25 == null){
            pingfang25 = InGameFonts.getFontRenderer("pingfang.ttf",25);
        }

        if(pingfang20 == null){
            pingfang20 = InGameFonts.getFontRenderer("pingfang.ttf",20);
        }

        if(pingfang30 == null){
            pingfang30 = InGameFonts.getFontRenderer("pingfang.ttf",30);
        }

        if(pingfang40 == null){
            pingfang40 = InGameFonts.getFontRenderer("pingfang.ttf",40);
        }

        if(pingfang_medium_30 == null){
            pingfang_medium_30 = InGameFonts.getFontRenderer("pingfang-medium.ttf",30);
        }

        if(anta40 == null){
            anta40 = InGameFonts.getFontRenderer("anta.ttf",30);
        }

    }


    public static int healthTexture = -1;

    public static int layer1Texture = -1;
    public static int layer2Texture = -1;
    public static int layer3Texture = -1;


    public static int bubbleTexture = -1;
    public static int snowTexture = -1;
    public static int fireFlyTexture = -1;
    public static int heartTexture = -1;
    public static int dollarTexture = -1;
    public static int starTexture = -1;

    public static int circleTexture = -1;
    public static int radarTexture = -1;
    public static int lightTexture = -1;
    public static int targetTexture = -1;
    public static String[] effectIdToName = {"速度", "缓慢", "挖掘急迫", "挖掘缓慢", "力量", "瞬间治疗", "瞬间伤害", "跳跃提升", "反胃", "生命恢复", "抗性提升", "抗火", "水下呼吸", "隐身", "失明", "夜视", "饥饿", "虚弱", "中毒", "凋零", "生命提升", "伤害吸收", "饱和", "漂浮", "中毒(致命)", "潮涌能量", "缓降", "不祥征兆", "村庄英雄", "黑暗"};
    public static int[] effectIdToTexture;

    private void initTexture(){
        if(healthTexture == -1){
            Bitmap bitmap = BitmapFactory.decodeStream(TextureUtil.class.getClassLoader().getResourceAsStream("assets/imgs/heart.png"));
            healthTexture = TextureUtil.createTexture(bitmap);
        }
        if(layer1Texture == -1){
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(ResourceUtil.decryptStream(TextureUtil.class.getClassLoader().getResourceAsStream("assets/imgs/layer0.png")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            layer1Texture = TextureUtil.createTexture(bitmap);
        }
        if(layer2Texture == -1){
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(ResourceUtil.decryptStream(TextureUtil.class.getClassLoader().getResourceAsStream("assets/imgs/layer1.png")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            layer2Texture = TextureUtil.createTexture(bitmap);
        }
        if(layer3Texture == -1){
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(ResourceUtil.decryptStream(TextureUtil.class.getClassLoader().getResourceAsStream("assets/imgs/layer2.png")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            layer3Texture = TextureUtil.createTexture(bitmap);
        }


        if(bubbleTexture == -1){
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(ResourceUtil.decryptStream(TextureUtil.class.getClassLoader().getResourceAsStream("assets/imgs/particles/hitbubble.png")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            bubbleTexture = TextureUtil.createTexture(bitmap);
        }

        if(snowTexture == -1){
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(ResourceUtil.decryptStream(TextureUtil.class.getClassLoader().getResourceAsStream("assets/imgs/particles/snowflake.png")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            snowTexture = TextureUtil.createTexture(bitmap);
        }

        if(fireFlyTexture == -1){
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(ResourceUtil.decryptStream(TextureUtil.class.getClassLoader().getResourceAsStream("assets/imgs/particles/firefly.png")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            fireFlyTexture = TextureUtil.createTexture(bitmap);
        }

        if(heartTexture == -1){
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(ResourceUtil.decryptStream(TextureUtil.class.getClassLoader().getResourceAsStream("assets/imgs/particles/heart.png")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            heartTexture = TextureUtil.createTexture(bitmap);
        }

        if(dollarTexture == -1){
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(ResourceUtil.decryptStream(TextureUtil.class.getClassLoader().getResourceAsStream("assets/imgs/particles/dollar.png")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            dollarTexture = TextureUtil.createTexture(bitmap);
        }

        if(starTexture == -1){
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(ResourceUtil.decryptStream(TextureUtil.class.getClassLoader().getResourceAsStream("assets/imgs/particles/star.png")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            starTexture = TextureUtil.createTexture(bitmap);
        }

        if(circleTexture == -1){
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(ResourceUtil.decryptStream(TextureUtil.class.getClassLoader().getResourceAsStream("assets/imgs/particles/circle.png")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            circleTexture = TextureUtil.createTexture(bitmap);
        }

        if(radarTexture == -1){
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(TextureUtil.class.getClassLoader().getResourceAsStream("assets/imgs/radar.png"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            radarTexture = TextureUtil.createTexture(bitmap);
        }

        if(lightTexture == -1){
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(TextureUtil.class.getClassLoader().getResourceAsStream("assets/imgs/light.png"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            lightTexture = TextureUtil.createTexture(bitmap);
        }

        if(targetTexture == -1){
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(TextureUtil.class.getClassLoader().getResourceAsStream("assets/imgs/target.png"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            targetTexture = TextureUtil.createTexture(bitmap);
        }

        if(effectIdToTexture == null){
            effectIdToTexture = new int[effectIdToName.length];
            for(int i = 0; i < effectIdToTexture.length; i++){
                if(i == 5 || i == 6) {
                    effectIdToTexture[i] = -1;
                } else {
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(TextureUtil.class.getClassLoader().getResourceAsStream(String.format("assets/potions/%s.png",effectIdToName[i])));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    effectIdToTexture[i] = TextureUtil.createTexture(bitmap);
                }
            }
        }


    }


    public static void roundImage(int textureID,final float x, final float y, final float width, final float height,float radius) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
        roundImageProgram.useShader();
        glEnable(GL_TEXTURE_2D);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D,textureID);

        roundImageProgram.setUniformf("size",width,height);
        roundImageProgram.setUniformi("uSampler",0);
        roundImageProgram.setUniformf("radius",radius*2);

        int pos = GLES20.glGetUniformLocation(roundImageProgram.getProgramId(), "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(pos, 1, false, MatrixUtil.mvpMatrix(), 0);
        float[] vertices = new float[]{
                x, y + height, 0,
                x + width, y + height, 0,
                x + width, y, 0,
                x, y, 0
        };

        float[] texCoords = new float[]{0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F};

        float[] combinedData = new float[vertices.length + texCoords.length];

        for (int i = 0; i < vertices.length / 3; i++) {
            combinedData[i * 5] = vertices[i * 3];
            combinedData[i * 5 + 1] = vertices[i * 3 + 1];
            combinedData[i * 5 + 2] = vertices[i * 3 + 2];
            combinedData[i * 5 + 3] = texCoords[i * 2];
            combinedData[i * 5 + 4] = texCoords[i * 2 + 1];
        }

        int[] vbo = new int[1];
        GLES20.glGenBuffers(1, vbo, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vertexBuffer = Render2DUtil.getFloatBuffer(combinedData);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_DYNAMIC_DRAW);

        int vPosition = GLES20.glGetAttribLocation(roundImageProgram.getProgramId(), "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 5 * 4, 0);

        int texCoordHandle = GLES20.glGetAttribLocation(roundImageProgram.getProgramId(), "aUv");
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 5 * 4, 3 * 4);

        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        glDisable(GL_TEXTURE_2D);

        glBindTexture(GL_TEXTURE_2D,0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glDeleteBuffers(1, vbo, 0);

        roundImageProgram.unUseShader(false);
    }


    public static void drawShadow(float x, float y, float width, float height, float radius, int firstColor, int secondColor, boolean vertical, float[] mvpMatrix){

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);

        shadowProgram.useShader();

        int[] viewport = new int[4];
        glGetIntegerv(GL_VIEWPORT, viewport, 0);

        shadowProgram.setUniformf("size",width,height);
        shadowProgram.setUniformf("location",x,(viewport[3]-y-height));
        shadowProgram.setUniformf("u_first_color",Color.red(firstColor) / 255.0F,Color.green(firstColor) / 255.0F,Color.blue(firstColor) / 255.0F,Color.alpha(firstColor) / 255.0F);
        shadowProgram.setUniformf("u_second_color",Color.red(secondColor) / 255.0F,Color.green(secondColor) / 255.0F,Color.blue(secondColor) / 255.0F,Color.alpha(secondColor) / 255.0F);
        shadowProgram.setUniformi( "u_direction", vertical ? 1 : 0);
        shadowProgram.setUniformf("radius",radius);
        shadowProgram.setUniformf("glow",1f);


        x-=20;
        y-=20;
        width+=40;
        height+=40;

        int pos = GLES20.glGetUniformLocation(shadowProgram.getProgramId(), "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(pos, 1, false, mvpMatrix, 0);

        float[] vertices = new float[]{
                x, y + height, 0,
                x + width, y + height, 0,
                x + width, y, 0,
                x, y, 0
        };

        float[] texCoords = new float[]{0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F};

        float[] combinedData = new float[vertices.length + texCoords.length];

        for (int i = 0; i < vertices.length / 3; i++) {
            combinedData[i * 5] = vertices[i * 3];
            combinedData[i * 5 + 1] = vertices[i * 3 + 1];
            combinedData[i * 5 + 2] = vertices[i * 3 + 2];
            combinedData[i * 5 + 3] = texCoords[i * 2];
            combinedData[i * 5 + 4] = texCoords[i * 2 + 1];
        }


        int[] vbo = new int[1];

        GLES20.glGenBuffers(1, vbo, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vertexBuffer = Render2DUtil.getFloatBuffer(combinedData);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_DYNAMIC_DRAW);

        int vPosition = GLES20.glGetAttribLocation(shadowProgram.getProgramId(), "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 5 * 4, 0);

        int texCoordHandle = GLES20.glGetAttribLocation(shadowProgram.getProgramId(), "aUv");
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 5 * 4, 3 * 4);

        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        glDisable(GL_TEXTURE_2D);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glDeleteBuffers(1, vbo, 0);

        shadowProgram.unUseShader(false);
    }



    public static void rectangle(float x,float y,float width,float height,int color){
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);

        float[] vertices = {
                x,y,
                x+width,y,
                x+width,y+height,
                x,y+height
        };

        defaultProgram.useShader();
        defaultProgram.setMVPMatrix();
        defaultProgram.setColor(color);
        defaultProgram.setVerticesArray(vertices,2);
        glDrawArrays(GL_TRIANGLE_FAN,0,4);
        defaultProgram.unUseShader(false);

    }


    public static void drawGradientRound(float x, float y, float width, float height, float radius, int firstColor, int secondColor, boolean vertical) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);

        rgqProgram.useShader();
        rgqProgram.setUniformf("size",width,height);
        rgqProgram.setUniformf("radius",radius*2);
        rgqProgram.setUniformf("color1",Color.red(firstColor) / 255.0F,Color.green(firstColor) / 255.0F,Color.blue(firstColor) / 255.0F,Color.alpha(firstColor) / 255.0F);
        rgqProgram.setUniformf("color2",Color.red(secondColor) / 255.0F,Color.green(secondColor) / 255.0F,Color.blue(secondColor) / 255.0F,Color.alpha(secondColor) / 255.0F);
        rgqProgram.setUniformi( "u_direction", vertical ? 1 : 0);

        glEnable(GL_TEXTURE_2D);
        int pos = GLES20.glGetUniformLocation(rgqProgram.getProgramId(), "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(pos, 1, false, MatrixUtil.mvpMatrix(), 0);
        float[] vertices = new float[]{
                x, y + height, 0,
                x + width, y + height, 0,
                x + width, y, 0,
                x, y, 0
        };
        float[] texCoords = new float[]{0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F};

        float[] combinedData = new float[vertices.length + texCoords.length];

        for (int i = 0; i < vertices.length / 3; i++) {
            combinedData[i * 5] = vertices[i * 3];
            combinedData[i * 5 + 1] = vertices[i * 3 + 1];
            combinedData[i * 5 + 2] = vertices[i * 3 + 2];
            combinedData[i * 5 + 3] = texCoords[i * 2];
            combinedData[i * 5 + 4] = texCoords[i * 2 + 1];
        }

        int[] vbo = new int[1];
        GLES20.glGenBuffers(1, vbo, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vertexBuffer = Render2DUtil.getFloatBuffer(combinedData);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_DYNAMIC_DRAW);

        int vPosition = GLES20.glGetAttribLocation(rgqProgram.getProgramId(), "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 5 * 4, 0);

        int texCoordHandle = GLES20.glGetAttribLocation(rgqProgram.getProgramId(), "aUv");
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 5 * 4, 3 * 4);

        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        glDisable(GL_TEXTURE_2D);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glDeleteBuffers(1, vbo, 0);

        rgqProgram.unUseShader(false);
    }



    public static void drawGradientRound(float x, float y, float width, float height, float radius, int firstColor, int secondColor, int thirdColor,int fourthColor) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);

        int[] viewport = new int[4];
        glGetIntegerv(GL_VIEWPORT, viewport, 0);
        rgqProgram2.useShader();
        rgqProgram2.setUniformf("location",x,(viewport[3]-y-height));
        rgqProgram2.setUniformf("size",width,height);
        rgqProgram2.setUniformf("radius",radius*2);
        rgqProgram2.setUniformf("color1",Color.red(firstColor) / 255.0F,Color.green(firstColor) / 255.0F,Color.blue(firstColor) / 255.0F,Color.alpha(firstColor) / 255.0F);
        rgqProgram2.setUniformf("color2",Color.red(secondColor) / 255.0F,Color.green(secondColor) / 255.0F,Color.blue(secondColor) / 255.0F,Color.alpha(secondColor) / 255.0F);
        rgqProgram2.setUniformf("color3",Color.red(thirdColor) / 255.0F,Color.green(thirdColor) / 255.0F,Color.blue(thirdColor) / 255.0F,Color.alpha(thirdColor) / 255.0F);
        rgqProgram2.setUniformf("color4",Color.red(fourthColor) / 255.0F,Color.green(fourthColor) / 255.0F,Color.blue(fourthColor) / 255.0F,Color.alpha(fourthColor) / 255.0F);

        glEnable(GL_TEXTURE_2D);
        int pos = GLES20.glGetUniformLocation(rgqProgram2.getProgramId(), "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(pos, 1, false, MatrixUtil.mvpMatrix(), 0);
        float[] vertices = new float[]{
                x, y + height, 0,
                x + width, y + height, 0,
                x + width, y, 0,
                x, y, 0
        };

        float[] texCoords = new float[]{0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F};

        float[] combinedData = new float[vertices.length + texCoords.length];

        for (int i = 0; i < vertices.length / 3; i++) {
            combinedData[i * 5] = vertices[i * 3];
            combinedData[i * 5 + 1] = vertices[i * 3 + 1];
            combinedData[i * 5 + 2] = vertices[i * 3 + 2];
            combinedData[i * 5 + 3] = texCoords[i * 2];
            combinedData[i * 5 + 4] = texCoords[i * 2 + 1];
        }

        int[] vbo = new int[1];
        GLES20.glGenBuffers(1, vbo, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vertexBuffer = Render2DUtil.getFloatBuffer(combinedData);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_DYNAMIC_DRAW);

        int vPosition = GLES20.glGetAttribLocation(rgqProgram2.getProgramId(), "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 5 * 4, 0);

        int texCoordHandle = GLES20.glGetAttribLocation(rgqProgram2.getProgramId(), "aUv");
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 5 * 4, 3 * 4);

        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        glDisable(GL_TEXTURE_2D);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glDeleteBuffers(1, vbo, 0);

        rgqProgram2.unUseShader(false);
    }


}
