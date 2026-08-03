package helper.creeperbox.utils.render.shader;

import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glValidateProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

public class ShaderUtil {



    public static ShaderProgram createShader(String fragmentShader,String vertexShader){
        int fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        int vertexId = glCreateShader(GL_VERTEX_SHADER);



        String a = "#version 320 es\n" +
                "precision mediump float;\n" +
                "layout(location = 1) in vec4 vColor;\n" +
                "out vec4 fragColor;\n" +
                "\n" +
                "int main(){\n" +
                "  fragColor = vColor;\n" +
                "}";


        glShaderSource(fragmentId,fragmentShader);
        glShaderSource(vertexId,vertexShader);
        glCompileShader(fragmentId);
        glCompileShader(vertexId);

        int programId = glCreateProgram();

        glAttachShader(programId,fragmentId);
        glAttachShader(programId,vertexId);
        glValidateProgram(programId);
        glLinkProgram(programId);
        glDeleteShader(fragmentId);
        glDeleteShader(vertexId);
        return new ShaderProgram(programId);
    }

    public static ShaderProgram defaultShader(){
        String vertexShader = "#version 320 es\n" +
                "layout(location = 0) in vec4 vPosition;\n" +
                "uniform mat4 u_MVPMatrix;\n" +
                "\n" +
                "void main() {\n" +
                "    gl_Position = u_MVPMatrix * vPosition;\n" +
                "}";

        String fragmentShader = "#version 320 es\n" +
                "precision mediump float;\n" +
                "uniform vec4 vColor;\n" +
                "out vec4 fragColor;\n" +
                "\n" +
                "void main() {\n" +
                "    fragColor = vColor;\n" +
                "}";

        return createShader(fragmentShader,vertexShader);
    }

    public static ShaderProgram createShader(String fragmentShader){
        String vertexShader = "uniform mat4 u_MVPMatrix;\n" +
                "attribute vec4 vPosition;\n" +
                "attribute vec2 aUv;\n" +
                "varying vec2 vUv;\n" +
                "void main(){\n" +
                "  gl_Position = u_MVPMatrix * vPosition;\n" +
                "  vUv = aUv;\n" +
                "}";
        return createShader(fragmentShader,vertexShader);
    }



}
