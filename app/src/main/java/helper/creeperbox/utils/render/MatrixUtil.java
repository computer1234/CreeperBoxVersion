package helper.creeperbox.utils.render;

import android.opengl.Matrix;

import java.util.Stack;

public class MatrixUtil {
    public static final int PROJECTION_MATRIX = 0;
    public static final int MODEL_MATRIX = 1;

    public static final int VIEW_MATRIX = 2;

    private static ThreadLocal<Stack<float[]>> viewMatrixStack = new ThreadLocal<Stack<float[]>>() {
        @Override
        protected Stack<float[]> initialValue() {
            return new Stack<float[]>();
        }
    };
    private static ThreadLocal<Stack<float[]>> modelMatrixStack = new ThreadLocal<Stack<float[]>>() {
        @Override
        protected Stack<float[]> initialValue() {
            return new Stack<float[]>();
        }
    };
    private static ThreadLocal<Stack<float[]>> projectionMatrixStack = new ThreadLocal<Stack<float[]>>() {
        @Override
        protected Stack<float[]> initialValue() {
            return new Stack<float[]>();
        }
    };
    private static ThreadLocal<Integer> currentMode =new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };



    public static void pushMatrix() {
        Stack<float[]> current = currentMatrix();
        assert current != null;
        current.push(current.peek().clone());
    }

    private static Stack<float[]> currentMatrix() {
        switch (currentMode.get()) {
            case PROJECTION_MATRIX:
                if(projectionMatrixStack.get().isEmpty()){
                    float[] normalMatrix = new float[16];
                    Matrix.setIdentityM(normalMatrix, 0);
                    projectionMatrixStack.get().add(normalMatrix);
                }
                return projectionMatrixStack.get();
            case MODEL_MATRIX:
                if(modelMatrixStack.get().isEmpty()){
                    float[] normalMatrix = new float[16];
                    Matrix.setIdentityM(normalMatrix, 0);
                    modelMatrixStack.get().add(normalMatrix);
                }
                return modelMatrixStack.get();
            case VIEW_MATRIX:
                if(viewMatrixStack.get().isEmpty()){
                    float[] normalMatrix = new float[16];
                    Matrix.setIdentityM(normalMatrix, 0);
                    viewMatrixStack.get().add(normalMatrix);
                }
                return viewMatrixStack.get();
            default:
                return null;
        }
    }

    private static void check(){
        if(projectionMatrixStack.get().isEmpty()){
            float[] normalMatrix = new float[16];
            Matrix.setIdentityM(normalMatrix, 0);
            projectionMatrixStack.get().add(normalMatrix);
        }
        if(modelMatrixStack.get().isEmpty()){
            float[] normalMatrix = new float[16];
            Matrix.setIdentityM(normalMatrix, 0);
            modelMatrixStack.get().add(normalMatrix);
        }
        if(viewMatrixStack.get().isEmpty()){
            float[] normalMatrix = new float[16];
            Matrix.setIdentityM(normalMatrix, 0);
            viewMatrixStack.get().add(normalMatrix);
        }
    }


    public static void popMatrix(){
        currentMatrix().pop();
    }

    public static void transtale(final float x,final float y,final float z){
        Matrix.translateM(currentMatrix().peek(),0,x,y,z);
    }

    public static void scale(final float x,final float y,final float z){
        Matrix.scaleM(currentMatrix().peek(),0,x,y,z);
    }


    public static void ortho(final float left,final float right,final float bottom,final float top,final float near,final float far){
        Matrix.orthoM(currentMatrix().peek(),0,left,right,bottom,top,near,far);
    }


    public static void loadIdentity(){
        Matrix.setIdentityM(currentMatrix().peek(),0);
    }

    public static float[] mvpMatrix() {
        check();
        float[] mvpMatrix = new float[16];
        float[] tempMatrix = new float[16];
        Matrix.multiplyMM(tempMatrix, 0, projectionMatrixStack.get().peek(), 0, viewMatrixStack.get().peek(), 0);
        Matrix.multiplyMM(mvpMatrix, 0, tempMatrix, 0, modelMatrixStack.get().peek(), 0);
        return mvpMatrix;
    }


    public static void rotate(final float a,final float x,final float y,final float z){
        Matrix.rotateM(currentMatrix().peek(),0,a,x,y,z);
    }




    public static void matrixMode(int mode) {
        currentMode.set(mode);
    }
}
