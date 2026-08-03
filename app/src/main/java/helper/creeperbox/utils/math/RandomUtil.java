package helper.creeperbox.utils.math;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
    private final static Random rand;

    public static int nextInt(int in, int out) {
        int max = Math.max(in, out), min = Math.min(in, out);
        return rand.nextInt(max - min + 1) + min;
    }


    public static double getRandom(double min, double max) {
        if (min == max) {
            return min;
        } else if (min > max) {
            final double d = min;
            min = max;
            max = d;
        }
        return ThreadLocalRandom.current().nextDouble(min, max);
    }


    static {
        rand = new Random();
    }


}
