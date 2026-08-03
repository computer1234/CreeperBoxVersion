package helper.creeperbox.utils.render.bloom;

import static android.opengl.GLES10.glActiveTexture;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_VIEWPORT;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glGetIntegerv;
import static android.opengl.GLES20.glGetUniformLocation;
import static javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE0;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE20;

import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import helper.creeperbox.utils.render.Render2DUtil;
import helper.creeperbox.utils.render.shader.ShaderProgram;
import helper.creeperbox.utils.render.shader.ShaderUtil;

public class BloomUtil {

    private int radius;
    private float compression;

    public static ShaderProgram bloomShader;
    private FrameBuffer inputFrameBuffer;
    private FrameBuffer outputFrameBuffer;

    private List<Runnable> runnable;
    public BloomUtil(){
        if (bloomShader == null) {
            bloomShader = ShaderUtil.createShader("#version 320 es\n" +
                            "precision mediump float;\n" +
                            "uniform sampler2D u_diffuse_sampler;\n" +
                            "uniform sampler2D u_other_sampler;\n" +
                            "uniform vec2 u_texel_size;\n" +
                            "uniform vec2 u_direction;\n" +
                            "uniform float u_radius;\n" +
                            "uniform float u_kernel[128];\n" +
                            "in vec2 vUv;\n" +
                            "out vec4 fragColor;\n" +
                            "\n" +
                            "void main()\n" +
                            "{\n" +
                            "\t\n" +
                            "    if (u_direction.x == 0.0) {\n" +
                            "        float alpha = texture(u_other_sampler, vUv).a;\n" +
                            "        if (alpha > 0.0) discard;\n" +
                            "    }\n" +
                            "\n" +
                            "    float half_radius = u_radius / 2.0;\n" +
                            "    vec4 pixel_color = texture(u_diffuse_sampler, vUv);\n" +
                            "    pixel_color.rgb *= pixel_color.a;\n" +
                            "    pixel_color *= u_kernel[0];\n" +
                            "\n" +
                            "    for (int f = 1; f <= 6; f += 1) {\n" +
                            "        vec2 offset = float(f) * u_texel_size * u_direction;\n" +
                            "        vec4 left = texture(u_diffuse_sampler, vUv - offset);\n" +
                            "        vec4 right = texture(u_diffuse_sampler, vUv + offset);\n" +
                            "\n" +
                            "        left.rgb *= left.a;\n" +
                            "        right.rgb *= right.a;\n" +
                            "        pixel_color += (left + right) * u_kernel[int(f)];\n" +
                            "    }\n" +
                            "\n" +
                            "    fragColor = vec4(pixel_color.rgb / pixel_color.a, pixel_color.a);\n" +
                            "    if(fragColor.a <= 0.0) discard;\n" +
                            "}\n"

                    ,"#version 320 es\n" +
                    "uniform mat4 u_MVPMatrix;\n" +
                    "in vec4 vPosition;\n" +
                    "in vec2 aUv;\n" +
                    "out vec2 vUv;\n" +
                    "\n" +
                    "void main() {\n" +
                    "    gl_Position = u_MVPMatrix * vPosition;\n" +
                    "    vUv = aUv;\n" +
                    "}\n");
        }
        this.radius = 6;
        this.compression = 2.0f;
        this.runnable = new ArrayList<>();
    }


    public List<Runnable> getRunnable() {
        return runnable;
    }

    private GaussianKernel gaussianKernel = new GaussianKernel(0);

    public void addTask(Runnable runnable){
        this.runnable.add(runnable);
    }


    public void draw(){

        if(runnable.isEmpty()) return;
        this.update();

        inputFrameBuffer.bindFrameBuffer();
        for (int i = 0; i < runnable.size(); i++) {
            runnable.get(i).run();
        }
        outputFrameBuffer.bindFrameBuffer();

        bloomShader.useShader();

        if (this.gaussianKernel.getSize() != radius) {
            this.gaussianKernel = new GaussianKernel(radius);
            this.gaussianKernel.compute();
            final FloatBuffer buffer = Render2DUtil.getFloatBuffer(this.gaussianKernel.getKernel());
            bloomShader.setUniformi("u_radius",radius);
            GLES20.glUniform1fv(glGetUniformLocation(bloomShader.getProgramId(),"u_kernel"),this.gaussianKernel.getKernel().length, buffer);
            bloomShader.setUniformi("u_diffuse_sampler",0);
            bloomShader.setUniformi("u_other_sampler",20);
        }

        bloomShader.setUniformf("u_texel_size", 1.0F / inputFrameBuffer.framebufferWidth, 1.0F / inputFrameBuffer.framebufferHeight);
        bloomShader.setUniformf("u_direction",compression,0f);

        glActiveTexture(GL_TEXTURE0);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE,GL_SRC_ALPHA);
        inputFrameBuffer.bindFrameBufferTexture();
        bloomShader.drawQuadFlip(0,0, inputFrameBuffer.framebufferWidth, inputFrameBuffer.framebufferHeight);
        inputFrameBuffer.unbindBuffer();
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
        bloomShader.setUniformf("u_direction",0f,compression);
        outputFrameBuffer.bindFrameBufferTexture();
        glActiveTexture(GL_TEXTURE20);
        inputFrameBuffer.bindFrameBufferTexture();
        glActiveTexture(GL_TEXTURE0);
        bloomShader.drawQuadFlip(0,0, inputFrameBuffer.framebufferWidth, inputFrameBuffer.framebufferHeight);
        bloomShader.unUseShader();
        inputFrameBuffer.unbindBuffer();
        this.runnable.clear();
    }


    public void update(){

        int[] viewport = new int[4];
        glGetIntegerv(GL_VIEWPORT, viewport, 0);

        int width = viewport[2];
        int height = viewport[3];

        if(inputFrameBuffer == null){
            inputFrameBuffer = new FrameBuffer(width,height);
        }

        if(outputFrameBuffer == null){
            outputFrameBuffer = new FrameBuffer(width,height);
        }

        if (width != inputFrameBuffer.framebufferWidth || height != inputFrameBuffer.framebufferHeight) {
            inputFrameBuffer.deleteFrameBuffer();
            inputFrameBuffer = new FrameBuffer(width,height);
            outputFrameBuffer.deleteFrameBuffer();
            outputFrameBuffer = new FrameBuffer(width,height);
        }

        inputFrameBuffer.frameBufferClear();
        outputFrameBuffer.frameBufferClear();
    }


}
