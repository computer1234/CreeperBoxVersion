package helper.creeperbox.utils.render;


import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SCISSOR_TEST;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_VIEWPORT;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glGetIntegerv;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glScissor;

import static helper.creeperbox.utils.render.MatrixUtil.*;

import android.graphics.Color;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import helper.creeperbox.clickgui.ClickGUIRenderer;
import helper.creeperbox.utils.render.shader.ShaderProgram;
import helper.creeperbox.utils.render.shader.ShaderUtil;


public class Render2DUtil {

    private static ShaderProgram defaultShader = ShaderUtil.defaultShader();
    private static ShaderProgram circleShader = ShaderUtil.createShader("precision mediump float;\n" +
            "uniform vec4 vColor;\n" +
            "varying vec2 vUv;\n" +
            "void main(){\n" +
            "  gl_FragColor = vColor * (1.-smoothstep(0.48,0.5,length(vUv - vec2(0.5, 0.5))));\n" +
            "}");

    private static ShaderProgram rgqShader = ShaderUtil.createShader("precision mediump float;\n" +
            "uniform int u_direction;\n" +
            "uniform vec4 color1;\n" +
            "uniform vec4 color2;\n" +
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
            "    vec4 color = mix(color1, color2, u_direction > 0 ? vUv.y : vUv.x);\n" +
            "    vec2 centeredCoord = gl_FragCoord.xy - location - (size / 2.0);\n" +
            "    vec2 uv = centeredCoord / size.y;\n" +
            "    vec2 normalizedSize = size / size.y;\n" +
            "    float d = roundedBoxSDF(uv, normalizedSize / 2.0, radius / size.y);\n" +
            "    float smoothWidth = 0.001;\n" +
            "    float fill = smoothstep(-smoothWidth, smoothWidth, -d);\n" +
            "    gl_FragColor = color * fill;\n" +
            "    if(gl_FragColor.a <= 0.0) discard;\n" +
            "}\n"
    );


    private static ShaderProgram rgqShader2 = ShaderUtil.createShader("precision mediump float;\n" +
            "uniform vec4 color;\n" +
            "uniform vec2 size;\n" +
            "uniform vec2 location;\n" +
            "uniform float radius1;\n" +
            "uniform float radius2;\n" +
            "uniform float radius3;\n" +
            "uniform float radius4;\n" +
            "varying vec2 vUv;\n" +
            "\n" +
            "float roundedBoxSDF(vec2 p, vec2 b, vec4 r) {\n" +
            "    float currentRadius = 0.0;\n" +
            "    if (p.x > 0.0) {\n" +
            "        currentRadius = (p.y > 0.0) ? r.x : r.y; \n" +
            "    } else {\n" +
            "        currentRadius = (p.y > 0.0) ? r.z : r.w;\n" +
            "    }\n" +
            "\n" +
            "    float n = 4.0;\n" +
            "    p = abs(p) - b + currentRadius;\n" +
            "    if (min(p.x, p.y) < 0.0) {\n" +
            "        return length(max(p, 0.0)) - currentRadius;\n" +
            "    } else {\n" +
            "        return pow(pow(p.x, n) + pow(p.y, n), 1.0/n) - currentRadius;\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "void main(void) {\n" +
            "    vec2 centeredCoord = gl_FragCoord.xy - location - (size / 2.0);\n" +
            "    vec2 uv = centeredCoord / size.y;\n" +
            "    vec2 normalizedSize = size / size.y;\n" +
            "\tvec4 radii = vec4(radius1,radius2,radius3,radius4);\n" +
            "    vec4 normalizedRadii = radii / size.y;\n" +
            "\n" +
            "    float d = roundedBoxSDF(uv, normalizedSize / 2.0, normalizedRadii);\n" +
            "    \n" +
            "    float smoothWidth = 0.005;\n" +
            "    float fill = smoothstep(-smoothWidth, smoothWidth, -d);\n" +
            "    \n" +
            "    gl_FragColor = color * fill;\n" +
            "    if (gl_FragColor.a <= 0.0) discard;\n" +
            "}\n");
    private static ShaderProgram imageShader = ShaderUtil.createShader("precision mediump float;\n" +
            "uniform vec4 vColor;\n" +
            "varying vec2 vUv;\n" +
            "uniform sampler2D uSampler;\n" +
            "void main(){\n" +
            "  gl_FragColor = texture2D(uSampler,vUv) * vColor;\n" +
            "}");

    private static ShaderProgram roundImageShader = ShaderUtil.createShader("precision mediump float;\n" +
            "uniform float u_radius;\n" +
            "uniform vec2 u_size;\n" +
            "uniform vec4 vColor;\n" +
            "varying vec2 vUv;\n" +
            "uniform sampler2D uSampler;\n" +
            "void main(){\n" +
            "    vec4 col = texture2D(uSampler,vUv);\n" +
            "    gl_FragColor = vec4(col.rgb, col.a * smoothstep(1.0, 0.0, length(max((abs(vUv - 0.5) + 0.5) * u_size - u_size + u_radius, 0.0)) - u_radius + 0.5)) * vColor;\n" +
            "}");


    private static ShaderProgram gradientImageShader = ShaderUtil.createShader("precision mediump float;\n" +
            "uniform vec4 u_first_color;\n" +
            "uniform vec4 u_second_color;\n" +
            "uniform int u_direction;\n" +
            "varying vec2 vUv;\n" +
            "uniform sampler2D uSampler;\n" +
            "void main(){\n" +
            "  vec4 color = mix(u_first_color, u_second_color, u_direction > 0 ? vUv.y : vUv.x);\n" +
            "  gl_FragColor = texture2D(uSampler,vUv) * color;\n" +
            "}");

    private static ShaderProgram fourGradientShader = ShaderUtil.createShader("precision mediump float;\n" +
            "uniform float u_radius;\n" +
            "uniform vec4 u_first_color;\n" +
            "uniform vec4 u_second_color;\n" +
            "uniform vec4 u_third_color;\n" +
            "uniform vec4 u_fourth_color;\n" +
            "uniform vec2 u_size;\n" +
            "varying vec2 vUv;\n" +
            "void main(){\n" +
            "  vec4 color = mix(mix(u_first_color, u_second_color, vUv.x), mix(u_third_color, u_fourth_color, vUv.x), vUv.y);" +
            "  gl_FragColor = vec4(color.rgb, color.a * smoothstep(1.0, 0.0, length(max((abs(vUv - 0.5) + 0.5) * u_size - u_size + u_radius, 0.0)) - u_radius + 0.5));\n" +
            "  if(gl_FragColor.a <= 0.0){\n" +
            "    discard;\n" +
            "  }else{\n" +
            "    gl_FragColor.a = 1.0 * color.a;\n"+
            "  }\n" +
            "}");


    private static ShaderProgram hudShader = ShaderUtil.createShader("precision mediump float;\n" +
            "uniform vec4 color1;\n" +
            "uniform vec4 color2;\n" +
            "uniform vec4 color3;\n" +
            "uniform vec4 color4;\n" +
            "\n" +
            "uniform vec2 size;\n" +
            "uniform vec2 location;\n" +
            "\n" +
            "uniform float radius;\n" +
            "uniform float alpha;\n" +
            "\n" +
            "uniform float glow;\n" +
            "uniform float angle;\n" +
            "uniform float outline;\n" +
            "uniform float blend;\n" +
            "\n" +
            "varying vec2 vUv;\n" +
            "\n" +
            "\n" +
            "float roundedBoxSDF(vec2 center, vec2 size, float radius) {\n" +
            "    return length(max(abs(center) - size + radius, 0.0)) - radius;\n" +
            "}\n" +
            "\n" +
            "\n" +
            "vec3 createGradient(vec2 coords, vec3 color1, vec3 color2, vec3 color3, vec3 color4){\n" +
            "    vec2 centeredCoords = coords - vec2(0.5, 0.5);\n" +
            "    float angleInRadians = angle * 3.14159265 / 180.0;\n" +
            "    vec2 rotatedCoords = vec2(\n" +
            "    centeredCoords.x * cos(angleInRadians) - centeredCoords.y * sin(angleInRadians),\n" +
            "    centeredCoords.x * sin(angleInRadians) + centeredCoords.y * cos(angleInRadians)\n" +
            "    );\n" +
            "    rotatedCoords += vec2(0.5, 0.5);\n" +
            "\n" +
            "    vec3 color = mix(mix(color1.rgb, color2.rgb, coords.y), mix(color3.rgb, color4.rgb, rotatedCoords.y), rotatedCoords.x);\n" +
            "    color += mix(0.0019607843, -0.0019607843, fract(sin(dot(rotatedCoords.xy, vec2(12.9898, 78.233))) * 43758.5453));\n" +
            "    return color;\n" +
            "}\n" +
            "\n" +
            "\n" +
            "void main(void)\n" +
            "{\n" +
            "    vec2 centeredCoord = gl_FragCoord.xy - location - (size / 2.0);\n" +
            "    vec2 normCoord = (gl_FragCoord.xy - location) / size;\n" +
            "\n" +
            "    float distance = roundedBoxSDF(centeredCoord, size / 2.0, radius);\n" +
            "    float smoothedAlpha = (1.0 - smoothstep(-10., 10., distance)) * color1.a;\n" +
            "    float smoothedAlpha2 = (1.0 - smoothstep(-1., 1., distance)) * color1.a;\n" +
            "\n" +
            "    vec3 gradientColor = createGradient(normCoord, color1.rgb, color2.rgb, color3.rgb, color4.rgb);\n" +
            "\n" +
            "    if (smoothedAlpha2 < glow) {\n" +
            "        gl_FragColor = vec4(gradientColor, smoothedAlpha);\n" +
            "    } else {\n" +
            "        float distance1 = roundedBoxSDF(centeredCoord, (size / 2.0) + 0.5 - outline, radius);\n" +
            "        float blendAmount = smoothstep(0., 2., abs(distance1) - outline);\n" +
            "        vec3 insideColor = createGradient(normCoord, color1.rgb / blend, color2.rgb / blend, color3.rgb / blend, color4.rgb / blend);\n" +
            "        vec4 insideColorVec = (distance1 < 0.) ? vec4(insideColor, alpha) : vec4(insideColor, 0.0);\n" +
            "        gl_FragColor = mix(vec4(gradientColor, 1.), insideColorVec, blendAmount);\n" +
            "    }\n" +
            "\n" +
            "    if(gl_FragColor.a <= 0.0){\n" +
            "        discard;\n" +
            "    }\n" +
            "\n" +
            "}\n" +
            "\n");

    private static ShaderProgram rogShader = ShaderUtil.createShader("precision mediump float;\n" +
            "uniform vec2 u_size;\n" +
            "uniform float u_radius;\n" +
            "uniform float u_border_size;\n" +
            "uniform vec4 u_color;\n" +
            "varying vec2 vUv;\n" +
            "\n" +
            "void main(void)\n" +
            "{\n" +
            "    vec2 position = (abs(vUv - 0.5) + 0.5) * u_size;\n" +
            "    float distance = length(max(position - u_size + u_radius + u_border_size, 0.0)) - u_radius + 0.5;\n" +
            "    gl_FragColor = vec4(u_color.rgb, u_color.a * (smoothstep(0.0, 1.0, distance) - smoothstep(0.0, 1.0, distance - u_border_size)));\n" +
            "    if(gl_FragColor.a <= 0.0) discard;\n"+
            "}");

    public static void drawBorder(float x, float y, float width, float height,float border, float radius,int color) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
        rogShader.useShader();
        rogShader.setUniformf("u_size",width,height);
        rogShader.setUniformf("u_radius",radius);
        rogShader.setUniformf("u_color",Color.red(color) / 255.0F,Color.green(color) / 255.0F,Color.blue(color) / 255.0F,Color.alpha(color) / 255.0F);
        rogShader.setUniformf("u_border_size",border);
        rogShader.drawQuad(x,y,width,height);
        rogShader.unUseShader();
    }

    public static void drawGradientRound(float x, float y, float width, float height, float radius, int firstColor, int secondColor, boolean vertical) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
        rgqShader.useShader();
        rgqShader.setUniformf("location",x,(ClickGUIRenderer.height-y-height));
        rgqShader.setUniformf("size",width,height);
        rgqShader.setUniformf("radius",radius*2);
        rgqShader.setUniformf("color1",Color.red(firstColor) / 255.0F,Color.green(firstColor) / 255.0F,Color.blue(firstColor) / 255.0F,Color.alpha(firstColor) / 255.0F);
        rgqShader.setUniformf("color2",Color.red(secondColor) / 255.0F,Color.green(secondColor) / 255.0F,Color.blue(secondColor) / 255.0F,Color.alpha(secondColor) / 255.0F);
        rgqShader.setUniformi( "u_direction", vertical ? 1 : 0);
        rgqShader.drawQuad(x,y,width,height);
        rgqShader.unUseShader();
    }



    public static void drawGradientRound(float x, float y, float width, float height, float radius, int firstColor, int secondColor, int thirdColor,int fourthColor) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);

        fourGradientShader.useShader();
        fourGradientShader.setUniformf("u_size",width,height);
        fourGradientShader.setUniformf("u_radius",radius);
        fourGradientShader.setUniformf("u_first_color",Color.red(firstColor) / 255.0F,Color.green(firstColor) / 255.0F,Color.blue(firstColor) / 255.0F,Color.alpha(firstColor) / 255.0F);
        fourGradientShader.setUniformf("u_second_color",Color.red(secondColor) / 255.0F,Color.green(secondColor) / 255.0F,Color.blue(secondColor) / 255.0F,Color.alpha(secondColor) / 255.0F);
        fourGradientShader.setUniformf("u_third_color",Color.red(thirdColor) / 255.0F,Color.green(thirdColor) / 255.0F,Color.blue(thirdColor) / 255.0F,Color.alpha(thirdColor) / 255.0F);
        fourGradientShader.setUniformf("u_fourth_color",Color.red(fourthColor) / 255.0F,Color.green(fourthColor) / 255.0F,Color.blue(fourthColor) / 255.0F,Color.alpha(fourthColor) / 255.0F);

        fourGradientShader.drawQuad(x,y,width,height);
        fourGradientShader.unUseShader();
    }


    public static void drawRound(float x, float y, float width, float height, float radius,int color){
        drawGradientRound(x,y,width,height,radius,color,color);
    }

    public static void drawGradientRound(float x, float y, float width, float height, float radius, int firstColor, int secondColor) {
        drawGradientRound(x, y, width, height, radius, firstColor, secondColor, true);
    }

    public static void drawRound(float x, float y, float width, float height, float radius1,float radius2,float radius3,float radius4, int color){
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
        rgqShader2.useShader();
        rgqShader2.setUniformf("size",width,height);
        rgqShader2.setUniformf("location",x,(ClickGUIRenderer.height-y-height));
        rgqShader2.setUniformf("color",Color.red(color) / 255.0F,Color.green(color) / 255.0F,Color.blue(color) / 255.0F,Color.alpha(color) / 255.0F);
        rgqShader2.setUniformf("radius1",radius1*2);
        rgqShader2.setUniformf("radius2",radius2*2);
        rgqShader2.setUniformf("radius3",radius3*2);
        rgqShader2.setUniformf("radius4",radius4*2);

        int pos = GLES20.glGetUniformLocation(rgqShader2.getProgramId(), "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(pos, 1, false, mvpMatrix(), 0);

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
        int vPosition = GLES20.glGetAttribLocation(rgqShader2.getProgramId(), "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 5 * 4, 0);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glDeleteBuffers(1, vbo, 0);

        rgqShader2.unUseShader();
    }



    public static void scissorStart(float x,float y,float width,float height){
        glEnable(GL_SCISSOR_TEST);
        glScissor((int) x, (int) (ClickGUIRenderer.height-y-height), (int) width, (int) height);
    }


    public static void scissorEnd(){
        glDisable(GL_SCISSOR_TEST);
    }



    public static void circle(final float centerX,final float centerY,final float radius,final int color){
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);

        float x = centerX - radius;
        float y = centerY - radius;
        float width = radius*2;
        float height = width;
        circleShader.useShader();
        circleShader.setColor(color);
        circleShader.drawQuad(x,y,width,height);
        circleShader.unUseShader();
    }

    public static void line(final float x1,final float y1,final float x2,final float y2,final float width,final int color){
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);

        if(width>0){
            glLineWidth(width);
        }

        float[] vertices = {
                x1,y1,
                x2,y2
        };

        defaultShader.useShader();
        defaultShader.setMVPMatrix();
        defaultShader.setColor(color);
        defaultShader.setVerticesArray(vertices,2);
        glDrawArrays(GL_LINES,0,2);
        defaultShader.unUseShader();
    }

    public static void line(final float x1,final float y1,final float x2,final float y2,final int color){
       line(x1,y1,x2,y2,0,color);
    }

    public static void image(String res,final float x, final float y, final float width, final float height) {
        image(res,x,y,width,height,Color.WHITE);
    }

    public static void image(int textureID,final float x, final float y, final float width, final float height) {
        image(textureID,x,y,width,height,Color.WHITE);
    }



    public static void roundImage(String res,final float x, final float y, final float width, final float height,float radius) {
        roundImage(res,x,y,width,height,radius,Color.WHITE);
    }
    public static void roundImage(String res,final float x, final float y, final float width, final float height,float radius,int color) {
        int id = TextureUtil.createTexture(res);
        if(id == 0) return;
        roundImage(id,x,y,width,height,radius,color);
    }
    public static void roundImage(int textureID,final float x, final float y, final float width, final float height,float radius) {
        roundImage(textureID,x,y,width,height,radius,Color.WHITE);
    }

    public static void gradientImage(int textureID,final float x, final float y, final float width, final float height,int firstColor,int secondColor,boolean vertical){
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
        gradientImageShader.useShader();
        glEnable(GL_TEXTURE_2D);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D,textureID);
        gradientImageShader.setUniformf("u_first_color",Color.red(firstColor) / 255.0F,Color.green(firstColor) / 255.0F,Color.blue(firstColor) / 255.0F,Color.alpha(firstColor) / 255.0F);
        gradientImageShader.setUniformf("u_second_color",Color.red(secondColor) / 255.0F,Color.green(secondColor) / 255.0F,Color.blue(secondColor) / 255.0F,Color.alpha(secondColor) / 255.0F);
        gradientImageShader.setUniformi( "u_direction", vertical ? 1 : 0);
        gradientImageShader.setUniformi("uSampler",0);
        gradientImageShader.drawQuad(x,y,width,height);
        glBindTexture(GL_TEXTURE_2D,0);
        gradientImageShader.unUseShader();
    }

    public static void roundImage(int textureID,final float x, final float y, final float width, final float height,float radius,int color) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
        roundImageShader.useShader();
        glEnable(GL_TEXTURE_2D);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D,textureID);
        roundImageShader.setUniformf("u_size",width,height);
        roundImageShader.setUniformi("uSampler",0);
        roundImageShader.setUniformf("u_radius",radius);
        roundImageShader.setColor(color);
        roundImageShader.drawQuad(x,y,width,height);
        glBindTexture(GL_TEXTURE_2D,0);
        roundImageShader.unUseShader();
    }



    public static void image(int textureID,final float x, final float y, final float width, final float height,int color){
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
        imageShader.useShader();
        glEnable(GL_TEXTURE_2D);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D,textureID);
        imageShader.setUniformi("uSampler",0);
        imageShader.setColor(color);
        imageShader.drawQuad(x,y,width,height);
        glBindTexture(GL_TEXTURE_2D,0);
        imageShader.unUseShader();
    }


    public static void image(String res,final float x, final float y, final float width, final float height,int color){
        int id = TextureUtil.createTexture(res);
        if(id == 0) return;
        image(id,x,y,width,height,color);
    }


    public static void rectangle(final float x, final float y, final float width, final float height, final int color){

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);

        float[] vertices = {
                x,y,
                x+width,y,
                x+width,y+height,
                x,y+height
        };

        defaultShader.useShader();
        defaultShader.setMVPMatrix();
        defaultShader.setColor(color);
        defaultShader.setVerticesArray(vertices,2);
        glDrawArrays(GL_TRIANGLE_FAN,0,4);
        defaultShader.unUseShader();

    }


    public static void startRotation(float x, float y, float width, float height, float rotation){
        pushMatrix();
        x += width / 2;
        y += height / 2;
        transtale(x,y,0);
        rotate(rotation,0,0,1);
        transtale(-x,-y,0);
    }




    public static void endRotation(){
        popMatrix();
    }


    public static void startScale(float x, float y, float scale){
        pushMatrix();
        transtale(x,y,0);
        scale(scale,scale,0);
        transtale(-x,-y,0);
    }


    public static void endScale(){
        popMatrix();
    }


    public static FloatBuffer getFloatBuffer(float[] array) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(array.length * Float.SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(array);
        floatBuffer.position(0);
        return floatBuffer;
    }



    public static void drawGradientRoundOld(float x, float y, float width, float height, float radius, int firstColor, int secondColor, boolean vertical) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);

        rgqShaderOld.useShader();
        rgqShaderOld.setUniformf("u_size",width,height);
        rgqShaderOld.setUniformf("u_radius",radius);
        rgqShaderOld.setUniformf("u_first_color",Color.red(firstColor) / 255.0F,Color.green(firstColor) / 255.0F,Color.blue(firstColor) / 255.0F,Color.alpha(firstColor) / 255.0F);
        rgqShaderOld.setUniformf("u_second_color",Color.red(secondColor) / 255.0F,Color.green(secondColor) / 255.0F,Color.blue(secondColor) / 255.0F,Color.alpha(secondColor) / 255.0F);
        rgqShaderOld.setUniformi( "u_direction", vertical ? 1 : 0);
        rgqShaderOld.drawQuad(x,y,width,height);
        rgqShaderOld.unUseShader();
    }


    private static ShaderProgram rgqShaderOld = ShaderUtil.createShader(
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

    private static ShaderProgram rgqShader2Old = ShaderUtil.createShader("precision mediump float;\n" +
            "uniform vec4 color;\n" +
            "uniform vec2 size;\n" +
            "uniform vec2 location;\n" +
            "uniform float radius1;\n" +
            "uniform float radius2;\n" +
            "uniform float radius3;\n" +
            "uniform float radius4;\n" +
            "varying vec2 vUv;\n" +
            "\n" +
            "float roundBoxSDF(in vec2 p, in vec2 b, in vec4 r)\n" +
            "{\n" +
            "    r.xy = (p.x > 0.0) ? r.xy : r.zw;\n" +
            "    r.x  = (p.y > 0.0) ? r.x  : r.y;\n" +
            "    vec2 q = abs(p) - b + r.x;\n" +
            "    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r.x;\n" +
            "}\n" +
            "\n" +
            "void main(void)\n" +
            "{\n" +
            "    vec2 centeredCoord = gl_FragCoord.xy - location - (size / 2.0);\n" +
            "    vec2 uv = centeredCoord / size.y;\n" +
            "    vec2 normalizedSize = size / size.y;\n" +
            "    vec4 radii = vec4(radius1, radius2, radius3, radius4) / size.y;\n" +
            "    float d = roundBoxSDF(uv, normalizedSize / 2.0, radii);\n" +
            "    float fill = step(0.0, -d);\n" +
            "    gl_FragColor = color * fill;\n" +
            "\tif(gl_FragColor.a <= 0.0) discard;\n" +
            "}");

    public static void drawRoundOld(float x, float y, float width, float height, float radius1,float radius2,float radius3,float radius4, int color){
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
        rgqShader2Old.useShader();
        rgqShader2Old.setUniformf("size",width,height);
        rgqShader2Old.setUniformf("location",x,(ClickGUIRenderer.height-y-height));
        rgqShader2Old.setUniformf("color",Color.red(color) / 255.0F,Color.green(color) / 255.0F,Color.blue(color) / 255.0F,Color.alpha(color) / 255.0F);
        rgqShader2Old.setUniformf("radius1",radius1*2);
        rgqShader2Old.setUniformf("radius2",radius2*2);
        rgqShader2Old.setUniformf("radius3",radius3*2);
        rgqShader2Old.setUniformf("radius4",radius4*2);

        int pos = GLES20.glGetUniformLocation(rgqShader2Old.getProgramId(), "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(pos, 1, false, mvpMatrix(), 0);

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
        int vPosition = GLES20.glGetAttribLocation(rgqShader2Old.getProgramId(), "vPosition");
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 3, GLES20.GL_FLOAT, false, 5 * 4, 0);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glDeleteBuffers(1, vbo, 0);

        rgqShader2Old.unUseShader();
    }

}
