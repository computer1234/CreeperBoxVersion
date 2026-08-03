package helper.creeperbox.utils.render;

import static android.opengl.GLES20.GL_ALWAYS;
import static android.opengl.GLES20.GL_EQUAL;
import static android.opengl.GLES20.GL_KEEP;
import static android.opengl.GLES20.GL_REPLACE;
import static android.opengl.GLES20.GL_STENCIL_BUFFER_BIT;
import static android.opengl.GLES20.GL_STENCIL_TEST;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glColorMask;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glStencilFunc;
import static android.opengl.GLES20.glStencilOp;


public class StencilUtil {


    public static void initStencil(){
        glClear(GL_STENCIL_BUFFER_BIT);
        glEnable(GL_STENCIL_TEST);
    }

    public static void writeStencilBuffer(){
        glStencilFunc(GL_ALWAYS, 1, 1);
        glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
        glColorMask(false, false, false, false);
    }

    public static void readStencilBuffer(){
        glColorMask(true, true, true, true);
        glStencilFunc(GL_EQUAL, 1, 1);
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
    }

    public static void unUseStencil(){
        glDisable(GL_STENCIL_TEST);
    }
}
