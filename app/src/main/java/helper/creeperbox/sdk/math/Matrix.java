package helper.creeperbox.sdk.math;


import java.util.Arrays;

public class Matrix {
    public float[] m;

    public Matrix(float[] m){
        this.m = m;
    }


    @Override
    public String toString() {
        return Arrays.toString(m);
    }
}
