package helper.creeperbox.utils.math;

public class MathUtil {
    public static int floorDouble(double n) {
        int i = (int) n;
        return n >= i ? i : i - 1;
    }

    public static float wrapAngleTo180_float(float value) {
        value = value % 360.0F;

        if (value >= 180.0F) {
            value -= 360.0F;
        }

        if (value < -180.0F) {
            value += 360.0F;
        }

        return value;
    }

    /**
     * the angle is reduced to an angle between -180 and +180 by mod, and a 360 check
     */
    public static double wrapAngleTo180_double(double value) {
        value = value % 360.0D;

        if (value >= 180) {
            value -= 360.0D;
        }

        if (value < -180) {
            value += 360.0D;
        }

        return value;
    }

    public static double hypot(float[] floats){
        if(floats.length<2){
            return 0;
        }
        return Math.hypot(floats[0],floats[1]);
    }

    public static Number clamp(Number value, Number min, Number max) {
        double doubleValue = value.doubleValue();
        double doubleMin = value.doubleValue();
        if(min != null){
            doubleMin = min.doubleValue();
        }

        double doubleMax = value.doubleValue();
        if (max!=null) {
            doubleMax = max.doubleValue();
        }

        double clampedValue = clamp(doubleValue, doubleMin, doubleMax);

        if (value instanceof Integer) {
            return (int) clampedValue;
        } else if (value instanceof Long) {
            return (long) clampedValue;
        } else if (value instanceof Float) {
            return (float) clampedValue;
        } else {
            return clampedValue;
        }
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue){
        return interpolate(oldValue, newValue, (float) interpolationValue).intValue();
    }
    public static float interpolateFloat(float oldValue, float newValue, double interpolationValue){
        return interpolate(oldValue, newValue, (float) interpolationValue).floatValue();
    }
    public static Double interpolate(double oldValue, double newValue, double interpolationValue){
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }


    public static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        } else return Math.min(value, max);
    }


}
